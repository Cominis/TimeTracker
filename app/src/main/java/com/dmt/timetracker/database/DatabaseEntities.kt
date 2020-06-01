package com.dmt.timetracker.database

import androidx.room.*

@Entity(tableName = "activity_table")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "image_name")
    var imageName: String = ""
) {
    override fun toString(): String {
        return this.name
    }
}

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