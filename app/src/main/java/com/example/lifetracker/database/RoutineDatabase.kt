package com.example.lifetracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Routine::class, Activity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class RoutineDatabase : RoomDatabase() {

    abstract val routineDatabaseDao: RoutineDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: RoutineDatabase? = null

        fun getInstance(context: Context): RoutineDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoutineDatabase::class.java,
                        "routine_history_database"
                    )
                       .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
