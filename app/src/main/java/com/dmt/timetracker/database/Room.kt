package com.dmt.timetracker.database

import android.content.Context
import androidx.room.*
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.time
    }

}

@Database(entities = [Routine::class, Activity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TimeDatabase : RoomDatabase() {
    abstract val eraseDao: EraseDao
    abstract val activityDao: ActivityDao
    abstract val routineDao: RoutineDao
    abstract val domainDao: DomainDao
}

private lateinit var INSTANCE: TimeDatabase

fun getDatabase(context: Context): TimeDatabase {
    synchronized(TimeDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TimeDatabase::class.java,
                "time")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}