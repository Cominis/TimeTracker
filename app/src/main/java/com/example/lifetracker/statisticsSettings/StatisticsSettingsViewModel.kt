package com.example.lifetracker.statisticsSettings

import androidx.lifecycle.ViewModel
import com.example.lifetracker.database.RoutineDatabaseDao

class StatisticsSettingsViewModel(
        val routineId: Long = 0L,
        val database: RoutineDatabaseDao
) : ViewModel() {

}

