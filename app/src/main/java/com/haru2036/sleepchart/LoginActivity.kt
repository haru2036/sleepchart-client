package com.haru2036.sleepchart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.haru2036.sleepchart.app.SleepChart
import com.haru2036.sleepchart.di.module.SleepModule
import com.haru2036.sleepchart.domain.usecase.AccountUsecase
import com.haru2036.sleepchart.domain.usecase.SleepUseCase
import com.haru2036.sleepchart.infra.repository.AccountRepository
import com.haru2036.sleepchart.infra.repository.SharedPreferencesRepository
import com.haru2036.sleepchart.presentation.activity.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class LoginActivity : FragmentActivity(), GoogleApiClient.OnConnectionFailedListener {
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

    private val statusText by lazy{
        findViewById<TextView>(R.id.sign_in_status_text)
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
        statusText.text = "未認証"
        signInButton.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            RC_SIGN_IN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {
                    sharedPreferencesRepository.saveToken(result.signInAccount!!.idToken!!)
                    Timber.tag("token").d(sharedPreferencesRepository.getToken())
                    accountUsecase.register()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                MainActivity.start(this)
                                finish()
                            }, {
                                Timber.tag("sleepchart-error").e(it)
                            })
                }else{
                    statusText.text = result.status.statusMessage
                }
            }

        }
    }
}
