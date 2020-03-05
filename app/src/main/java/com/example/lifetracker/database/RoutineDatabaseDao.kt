package com.example.lifetracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface RoutineDatabaseDao {
    @Insert
    fun insertActivity(activity: Activity) : Long

    @Update
    fun updateActivity(activity: Activity)

    @Query("DELETE FROM activity_table")
    fun clearActivities()

    @Query("SELECT * from activity_table WHERE id = :key")
    fun getActivity(key: Long): Activity?

    @Query("SELECT * from activity_table WHERE id = :key")
    fun getActivityWithId(key: Long): LiveData<Activity>

    @Query("SELECT * FROM activity_table ORDER BY id DESC")
    fun getAllActivitiesLive(): LiveData<List<Activity>>

    @Query("SELECT * FROM activity_table ORDER BY id DESC")
    fun getAllActivities(): List<Activity>

    @Insert
    fun insertRoutine(routine: Routine)

    @Update
    fun updateRoutine(routine: Routine)

    @Query("DELETE FROM routine_table")
    fun clearRoutines()

    @Query("SELECT * from routine_table WHERE id = :key")
    fun getRoutine(key: Long): Routine?

    @Query("SELECT * from routine_table WHERE id = :key")
    fun getRoutineWithId(key: Long): LiveData<Routine>

    @Query("SELECT * FROM routine_table ORDER BY id DESC")
    fun getAllRoutinesLive(): LiveData<List<Routine>>

    @Query("SELECT * FROM routine_table ORDER BY id DESC")
    fun getAllRoutines(): List<Routine>

    @Query("SELECT * FROM routine_table ORDER BY id DESC LIMIT 1")
    fun getLatestRoutine(): Routine?


    @Query("SELECT start_time_milli, end_time_milli, name, image_name FROM activity_table LEFT JOIN routine_table  ON activity_table.id = routine_table.activity_id WHERE start_time_milli BETWEEN :fromDate AND :toDate")
    fun getStatisticsData(fromDate : Date, toDate: Date): List<StatisticsData>

}