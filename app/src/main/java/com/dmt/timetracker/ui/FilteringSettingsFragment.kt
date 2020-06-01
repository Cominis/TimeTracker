package com.dmt.timetracker.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.dmt.datetimepicker.DateTimePicker
import com.dmt.datetimepicker.DateTimePickerListener
import com.dmt.timetracker.R
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.dmt.timetracker.utils.millisToString
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class FilteringSettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var activityViewModel : MainActivityViewModel
    private lateinit var sharedPref : SharedPreferences //todo for initial date in datetime picker

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.filtering_preferences, rootKey)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        val fromDateTimeKey = getString(R.string.datetime_picker_from_key)
        val toDateTimeKey = getString(R.string.datetime_picker_until_key)

        val fromPref = findPreference<Preference>(fromDateTimeKey)
        val toPref = findPreference<Preference>(toDateTimeKey)

        var fromMillis = sharedPref.getLong(fromDateTimeKey, -1L)
        var toMillis = sharedPref.getLong(toDateTimeKey, -1L)

        if(fromMillis != -1L)
            fromPref?.summary = millisToString(
                fromMillis,
                "yyyy-MM-dd HH:mm"
            )
        else
            fromMillis = Instant.now().toEpochMilli()

        if(toMillis != -1L)
            toPref?.summary = millisToString(
                toMillis,
                "yyyy-MM-dd HH:mm"
            )
        else
            toMillis = Instant.now().toEpochMilli()

        val fromListener = object : DateTimePickerListener() {
            override fun onApproveDateTime(dateTime: LocalDateTime) {
                fromMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                with (sharedPref.edit()) {
                    putLong(fromDateTimeKey, fromMillis)
                    commit()
                }
                fromPref?.summary = millisToString(
                    fromMillis,
                    "yyyy-MM-dd HH:mm"
                )
            }
        }

        val toListener = object : DateTimePickerListener() {
            override fun onApproveDateTime(dateTime: LocalDateTime) {
                toMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                if(toMillis < fromMillis) {
                    Toast.makeText(requireContext(), getString(R.string.invalid_later_date,
                        millisToString(fromMillis)
                    ), Toast.LENGTH_LONG).show()
                    return
                }
                with (sharedPref.edit()) {
                    putLong(toDateTimeKey, toMillis)
                    commit()
                }
                toPref?.summary = millisToString(
                    toMillis,
                    "yyyy-MM-dd HH:mm"
                )
            }
        }

        fromPref?.setOnPreferenceClickListener { _ ->
            val picker = DateTimePicker(parentFragmentManager, fromListener)
            picker.setInitialDateTime(fromMillis)
            picker.show()
            true
        }

        toPref?.setOnPreferenceClickListener { _ ->
            val picker = DateTimePicker(parentFragmentManager, toListener)
            picker.setInitialDateTime(toMillis)
            picker.show()
            true
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityViewModel = ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)

        activityViewModel.updateActionBarTitle(resources.getString(R.string.statistics_settings))

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        activityViewModel.toUpdateStatistics(true)
    }

}
