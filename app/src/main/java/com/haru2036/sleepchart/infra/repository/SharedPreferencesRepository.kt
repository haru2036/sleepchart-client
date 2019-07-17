package com.haru2036.sleepchart.infra.repository

import android.content.Context
import android.preference.PreferenceManager
import com.haru2036.sleepchart.R
import io.reactivex.Single
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(val context: Context) {
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context)}
    private val secureSharedPreferences by lazy { context.getSharedPreferences("tokens", Context.MODE_PRIVATE) }

    fun getGadgetBridgePath() = Single.create<String> { it.onSuccess(sharedPreferences.getString("pref_key_gadgetbridge_import_path", context.getString(R.string.settings_default_db_path))) }!!


    fun saveToken(token: String) = secureSharedPreferences.edit()
            .apply { putString("accessToken", token) }
            .apply()
}

