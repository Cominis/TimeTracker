package com.example.lifetracker.settings.datePickerPreference

import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.example.lifetracker.R
import com.example.lifetracker.convertLongToDateOnlyString
import com.example.lifetracker.convertLongToDateString


class DatePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat(){

    private lateinit var mDatePicker : DatePicker

    companion object {
        fun newInstance(key: String?): DatePreferenceDialogFragmentCompat? {
            val fragment = DatePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        mDatePicker = view.findViewById(R.id.edit) as DatePicker

        // Get the time from the related Preference
        var minutesAfterMidnight: Long? = null
        val preference: DialogPreference = preference
        if (preference is DatePreference) {
            minutesAfterMidnight = preference.time
        }
        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val c = Calendar.getInstance()
            c.timeInMillis = minutesAfterMidnight
            mDatePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) { // generate value to save

            val cal: Calendar = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, mDatePicker.dayOfMonth)
            cal.set(Calendar.MONTH, mDatePicker.month)
            cal.set(Calendar.YEAR, mDatePicker.year)
            val minutesAfterMidnight = cal.timeInMillis

            // Get the related Preference and save the value
            val preference = preference
            if (preference is DatePreference) {

                // This allows the client to ignore the user value.
                if (preference.callChangeListener(minutesAfterMidnight)) { // Save the value
                    preference.time = minutesAfterMidnight
                    preference.summary = convertLongToDateOnlyString(minutesAfterMidnight)
                }
            }
        }
    }

}