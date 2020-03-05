package com.example.lifetracker.settings

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.lifetracker.R
import com.example.lifetracker.convertLongToDateString
import com.example.lifetracker.mainActivity.MainActivityViewModel

//                requireActivity().supportFragmentManager.beginTransaction()
//                    .replace(R.id.nav_host_fragment, FilteringSettingsFragment())
//                    .addToBackStack(null)
//                    .commit()

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.global_preferences, rootKey)

        findPreference<Preference>("managing")?.setOnPreferenceClickListener { preference ->
            this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToManagingSettingsFragment())
            true
        }

        findPreference<Preference>("filtering")?.setOnPreferenceClickListener { preference ->
            this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToFilteringSettingsFragment())
            true
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.settings))
    }


}
