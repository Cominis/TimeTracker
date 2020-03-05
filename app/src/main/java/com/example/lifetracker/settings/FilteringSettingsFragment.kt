package com.example.lifetracker.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.example.lifetracker.R
import com.example.lifetracker.mainActivity.MainActivityViewModel
import com.example.lifetracker.settings.datePickerPreference.DatePreference
import com.example.lifetracker.settings.datePickerPreference.DatePreferenceDialogFragmentCompat
import com.example.lifetracker.settings.timePickerPreference.TimePreference
import com.example.lifetracker.settings.timePickerPreference.TimePreferenceDialogFragmentCompat


class FilteringSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var activityViewModel : MainActivityViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.filtering_preferences, rootKey)

        activityViewModel = ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)

        activityViewModel.updateActionBarTitle(resources.getString(R.string.settings))

        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onDisplayPreferenceDialog(preference: Preference) { // Try if the preference is one of our custom Preferences
        var dialogFragment: DialogFragment? = null

        when (preference) {
            is TimePreference -> {
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey())
            }
            is DatePreference -> {
                dialogFragment = DatePreferenceDialogFragmentCompat.newInstance(preference.getKey())
            }
        }

        if (dialogFragment != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(parentFragmentManager, "androidx.preference.PreferenceFragmentCompat.DIALOG")
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        activityViewModel.toUpdateStatistics(true)
    }

}
