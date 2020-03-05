package com.example.lifetracker.settings.timePickerPreference

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.example.lifetracker.R


class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat(){

    private lateinit var mTimePicker : TimePicker

    companion object {
        fun newInstance(key: String?): TimePreferenceDialogFragmentCompat? {
            val fragment = TimePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        mTimePicker = view.findViewById(R.id.edit) as TimePicker

        // Get the time from the related Preference
        var minutesAfterMidnight: Int? = null
        val preference: DialogPreference = preference
        if (preference is TimePreference) {
            minutesAfterMidnight = preference.time
        }
        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60
            val is24hour: Boolean = DateFormat.is24HourFormat(context)
            mTimePicker.setIs24HourView(is24hour)
            mTimePicker.hour = hours
            mTimePicker.minute = minutes
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) { // generate value to save

            val minutesAfterMidnight = mTimePicker.hour * 60 + mTimePicker.minute

            // Get the related Preference and save the value
            val preference = preference
            if (preference is TimePreference) {

                // This allows the client to ignore the user value.
                if (preference.callChangeListener(minutesAfterMidnight)) { // Save the value
                    preference.time = minutesAfterMidnight
                    preference.summary = " %02d:%02d".format(minutesAfterMidnight / 60, minutesAfterMidnight % 60)
                }
            }
        }
    }

}