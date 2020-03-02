package com.example.lifetracker

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import java.text.SimpleDateFormat
import java.util.*

fun convertLongToDateString(systemTime: Long = 88888888888L): Spanned {
    val dateFormat = SimpleDateFormat("EEEE yyyy-MM-dd HH:mm:ss", Locale.US)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val string = dateFormat.format(Date(systemTime))
    return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
}