package com.example.lifetracker.statistics

import android.content.SharedPreferences
import android.icu.util.Calendar
import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.example.lifetracker.R
import com.example.lifetracker.database.Activity
import com.example.lifetracker.database.RoutineDatabaseDao
import com.example.lifetracker.database.StatisticsData
import kotlinx.coroutines.*
import java.util.*

class StatisticsViewModel(
        val database: RoutineDatabaseDao,
        val sharedPref: SharedPreferences
) : ViewModel() {

        private var viewModelJob = Job()
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        private var timestampFrom = Date(System.currentTimeMillis() - 10000000000L)
        private var timestampTo = Date(System.currentTimeMillis())
        private var weekdays = listOf<String>()

        private val _data = MutableLiveData<List<StatisticsData>>()
        val data: LiveData<List<StatisticsData>>
                get() = _data

        init {
                getStatisticsData()
        }
        fun updateDatabase(dateFrom: Long, dateTo: Long, timeFrom: Int, timeTo: Int, weekdays: List<String>){
                val c = Calendar.getInstance()

                c.timeInMillis = dateFrom
                c.set(Calendar.HOUR_OF_DAY, timeFrom / 60)
                c.set(Calendar.MINUTE, timeFrom % 60)
                timestampFrom = Date(c.timeInMillis)

                c.timeInMillis = dateTo
                c.set(Calendar.HOUR_OF_DAY, timeTo / 60)
                c.set(Calendar.MINUTE, timeTo % 60)
                timestampTo = Date(c.timeInMillis)
                getStatisticsData()
                this.weekdays = weekdays
        }

        private fun getStatisticsData() {
                uiScope.launch {
                        _data.value = getStatisticsDataFromDatabase()
                }
        }

        private suspend fun getStatisticsDataFromDatabase(): List<StatisticsData> {
                return withContext(Dispatchers.IO) {
                        database.getStatisticsData(timestampFrom, timestampTo)
                }
        }
}

