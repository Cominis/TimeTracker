package com.dmt.datetimepicker

import androidx.fragment.app.FragmentManager
import java.time.*

class DateTimePicker(private val fm: FragmentManager, listener: DateTimePickerListener? = null) {

    private var localDate: LocalDate = LocalDate.now()
    private var localTime: LocalTime = LocalTime.now()

    private var mListener: DateTimePickerListener? = null

    fun setListener(listener: DateTimePickerListener) {
        mListener = listener
    }

    fun setInitialDateTime(millis: Long){
       val instant = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
        localDate = instant.toLocalDate()
        localTime = instant.toLocalTime()
    }

    fun setInitialDateTime(localDateTime: LocalDateTime){
        localDate = localDateTime.toLocalDate()
        localTime = localDateTime.toLocalTime()
    }

    fun setInitialDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int){
        localDate = LocalDate.of(year, month, day)
        localTime = LocalTime.of(hour, minute)
    }

    init {
        if(listener != null)
            mListener = listener

        // See if there are any DialogFragments from the FragmentManager
        val ft = fm.beginTransaction()
        val prev = fm.findFragmentByTag(DateTimePickerDialogFragment.TAG)

        // Remove if found
        if (prev != null) {
            ft.remove(prev)
            ft.commit()
        }
    }

    fun show() {

        if(mListener == null){
            throw NullPointerException(
                "Attempting to bind null listener to SlideDateTimePicker")
        }


        val dialogFragment: DateTimePickerDialogFragment =
            DateTimePickerDialogFragment.newInstance(
                mListener!!,
                localDate,
                localTime
            )

        dialogFragment.show(fm,
            DateTimePickerDialogFragment.TAG
        )
    }
}