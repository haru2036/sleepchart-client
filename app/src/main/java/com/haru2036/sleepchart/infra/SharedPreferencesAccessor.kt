package com.haru2036.sleepchart.infra

import android.content.Context
import android.preference.PreferenceManager
import com.haru2036.sleepchart.R
import io.reactivex.Single
import javax.inject.Inject

class SharedPreferencesAccessor @Inject constructor(val context: Context){
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context)}

    fun getGadgetBridgePath() = Single.create<String> { it.onSuccess(sharedPreferences.getString("pref_key_gadgetbridge_import_path", context.getString(R.string.settings_default_db_path))) }!!
}

