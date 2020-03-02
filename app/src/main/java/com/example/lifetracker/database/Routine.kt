package com.example.lifetracker.database

import androidx.room.*

@Entity(
    tableName = "routine_table",
    foreignKeys = [
        ForeignKey(entity = Activity::class, childColumns = ["activity_id"], parentColumns = ["id"],
        onDelete = ForeignKey.SET_NULL)],
    indices = [Index(value = ["activity_id"])]
)
data class Routine(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    @ColumnInfo(name = "activity_id")
    var activityId: Long? = null
)