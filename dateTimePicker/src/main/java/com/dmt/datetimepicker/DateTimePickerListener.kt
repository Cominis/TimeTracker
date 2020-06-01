package com.dmt.datetimepicker

import java.time.LocalDateTime

abstract class DateTimePickerListener {
    abstract fun onApproveDateTime(dateTime: LocalDateTime)
    open fun onCancelDateTime() {}
}