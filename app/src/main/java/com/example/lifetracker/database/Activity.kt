package com.example.lifetracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
