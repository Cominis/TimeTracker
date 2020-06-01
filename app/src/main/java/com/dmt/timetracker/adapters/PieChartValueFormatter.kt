package com.dmt.timetracker.adapters

import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class PieChartValueFormatter : ValueFormatter() {

    private val hour = 3_600_000L
    private val minute = 60_000L
    private val second = 1_000L

    private val secondsInHour = 3_600L
    private val secondsInMin = 60L

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String? {
        val millis = value.toLong()
        val h = millis / secondsInHour
        val m = (millis % secondsInHour) / secondsInMin
        val s = millis % secondsInMin
        return "%02d:%02d:%02d".format(h, m, s)
    }

}