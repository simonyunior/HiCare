package com.example.hicare.ui.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.hicare.R
import com.example.hicare.ui.login.LoginActivity

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val themePreference: ListPreference? = findPreference("theme")
        themePreference?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                when (newValue as String) {
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                true
            }

        val logoutPreference: Preference? = findPreference("logout")
        logoutPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            logout()
            true
        }
    }

    private fun logout() {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        with(sharedPref?.edit()) {
            this?.remove("USERNAME")
            this?.remove("TOKEN")
            this?.apply()
        }

        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
