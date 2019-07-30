package com.haru2036.sleepchart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.AccountUsecase
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
import com.haru2036.sleepchart.presentation.activity.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class LoginActivity : FragmentActivity(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    private val RC_SIGN_IN = 1

    @Inject
    lateinit var sharedPreferencesRepository: SharedPreferencesRepository

    @Inject
    lateinit var accountUsecase: AccountUsecase

    @Inject
    lateinit var sleepUseCase: SleepUseCase

    private val signInButton by lazy {
        findViewById<SignInButton>(R.id.sign_in_button)
    }

    private val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SleepChart.getAppComponent().plus(SleepModule()).inject(this)
        setContentView(R.layout.activity_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        val googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        signInButton.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        if (GoogleSignIn.getLastSignedInAccount(this)?.isExpired == true) {
            Auth.GoogleSignInApi.silentSignIn(googleApiClient).setResultCallback {
                if (it.isSuccess) {
                    MainActivity.start(this)
                    finish()
                }
            }
        } else if (GoogleSignIn.getLastSignedInAccount(this)?.isExpired == false) {
            MainActivity.start(this)
            finish()
        } else {
            progressBar.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {
                    sharedPreferencesRepository.saveToken(result.signInAccount!!.idToken!!)
                    accountUsecase.register()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                MainActivity.start(this)
                                finish()
                            }, {
                                Timber.tag("sleepchart-error").e(it)
                            })
                }
            }

        }
    }
}
