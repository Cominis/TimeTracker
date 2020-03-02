package com.example.lifetracker

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("routineTimeFormatted")
fun TextView.setRoutineTimeFormatted(timestamp: Long?) {
    timestamp?.let {
        text = convertLongToDateString(it)
    }
}