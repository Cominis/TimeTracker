package com.dmt.timetracker.repository

import com.dmt.timetracker.database.Activity
import com.dmt.timetracker.database.Routine
import com.dmt.timetracker.database.TimeDatabase
import com.dmt.timetracker.domain.DataWithDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class TimeRepository(private val database: TimeDatabase) {

    suspend fun insert(vararg routine: Routine) : List<Long> {
        return withContext(Dispatchers.IO) {
            database.routineDao.insert(*routine)
        }
    }

    suspend fun delete(vararg routine: Routine) {
        withContext(Dispatchers.IO) {
            database.routineDao.delete(*routine)
        }
    }

    val latestRoutine = database.routineDao.getLatestRoutineLive()

    suspend fun getLatestRoutine() : Routine? {
        return withContext(Dispatchers.IO) {
            database.routineDao.getLatestRoutine()
        }
    }

    suspend fun eraseAll() {
        withContext(Dispatchers.IO) {
            database.eraseDao.eraseDatabase()
        }
    }

    suspend fun getAllActivities(): List<Activity> {
        return withContext(Dispatchers.IO) {
            database.activityDao.getAllActivities()
        }
    }

    suspend fun update(vararg routine: Routine) {
        withContext(Dispatchers.IO) {
            database.routineDao.update(*routine)
        }
    }

    suspend fun insert(vararg activity: Activity) : List<Long> {
        return withContext(Dispatchers.IO) {
            database.activityDao.insert(*activity)
        }
    }

    suspend fun update(vararg activity: Activity) {
        withContext(Dispatchers.IO) {
            database.activityDao.update(*activity)
        }
    }

    suspend fun getActivityByName(name : String): Activity? {
        return withContext(Dispatchers.IO) {
            database.activityDao.getActivityByName(name)
        }
    }

    suspend fun getStatisticsData(timestampFrom : Date, timestampTo : Date): List<DataWithDuration> {
            return withContext(Dispatchers.IO) {
                database.domainDao.getStatisticsDataConcise(timestampFrom, timestampTo)
        }
    }
}