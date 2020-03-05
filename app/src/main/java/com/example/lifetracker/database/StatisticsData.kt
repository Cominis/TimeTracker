package com.example.lifetracker.database

import androidx.room.ColumnInfo

data class StatisticsData(
    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "image_name")
    var imageName: String = ""
)