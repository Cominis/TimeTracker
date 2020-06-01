package com.dmt.timetracker.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dmt.timetracker.domain.DataWithDuration
import com.dmt.timetracker.domain.StatisticsData
import java.util.*

interface BaseDao<T> {
    @Insert
    fun insert(vararg obj: T) : List<Long>

    @Update
    fun update(vararg obj: T)

    @Delete
    fun delete(vararg obj: T)
}

@Dao
abstract class ActivityDao : BaseDao<Activity> {

    @Query("SELECT * from activity_table WHERE id = :key")
    abstract fun getActivity(key: Long): Activity?

    @Query("SELECT * from activity_table WHERE name = :name LIMIT 1")
    abstract fun getActivityByName(name: String): Activity?

    @Query("SELECT * from activity_table WHERE id = :key")
    abstract fun getActivityWithId(key: Long): LiveData<Activity>

    @Query("SELECT * FROM activity_table ORDER BY id DESC")
    abstract fun getAllActivitiesLive(): LiveData<List<Activity>>

    @Query("SELECT * FROM activity_table ORDER BY id DESC")
    abstract fun getAllActivities(): List<Activity>
}

@Dao
abstract class RoutineDao : BaseDao<Routine> {

    @Query("SELECT * from routine_table WHERE id = :key")
    abstract fun getRoutine(key: Long): Routine?

    @Query("SELECT * from routine_table WHERE id = :key")
    abstract fun getRoutineWithId(key: Long): LiveData<Routine>

    @Query("SELECT * FROM routine_table ORDER BY id DESC")
    abstract fun getAllRoutinesLive(): LiveData<List<Routine>>

    @Query("SELECT * FROM routine_table ORDER BY id DESC")
    abstract fun getAllRoutines(): List<Routine>

    @Query("SELECT * FROM routine_table ORDER BY id DESC LIMIT 1")
    abstract fun getLatestRoutine(): Routine?

    @Query("SELECT * FROM routine_table ORDER BY id DESC LIMIT 1")
    abstract fun getLatestRoutineLive(): LiveData<Routine?>
}

@Dao
interface DomainDao {

    @Query("SELECT start_time_milli, end_time_milli, name, image_name FROM activity_table LEFT JOIN routine_table  ON activity_table.id = routine_table.activity_id WHERE start_time_milli BETWEEN :fromDate AND :toDate")
    fun getStatisticsData(fromDate : Date, toDate: Date): List<StatisticsData>

    @Query("SELECT name, image_name, SUM(end_time_milli - start_time_milli) AS duration FROM activity_table LEFT JOIN routine_table  ON activity_table.id = routine_table.activity_id WHERE start_time_milli BETWEEN :fromDate AND :toDate GROUP BY name")
    fun getStatisticsDataConcise(fromDate : Date, toDate: Date): List<DataWithDuration>
}

@Dao
interface EraseDao {

    @Query("DELETE FROM activity_table")
    fun clearActivities()

    @Query("DELETE FROM routine_table")
    fun clearRoutines()

    @Transaction
    fun eraseDatabase(){
        clearActivities()
        clearRoutines()
    }
}