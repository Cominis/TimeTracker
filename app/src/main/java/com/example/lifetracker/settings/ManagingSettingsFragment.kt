package com.example.lifetracker.settings

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.lifetracker.R
import com.example.lifetracker.convertLongToDateString
import com.example.lifetracker.mainActivity.MainActivityViewModel


class ManagingSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.managing_preferences, rootKey)

        val summaryProvider = Preference.SummaryProvider<SwitchPreferenceCompat> { preference ->
            if(preference.isChecked){
                "Checked at ${convertLongToDateString(System.currentTimeMillis())}"
            } else {
                "Unchecked"
            }
        }

        findPreference<SwitchPreferenceCompat>("notifications")?.summaryProvider = summaryProvider

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.settings))
    }
}
