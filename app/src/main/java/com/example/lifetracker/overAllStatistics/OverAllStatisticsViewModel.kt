package com.example.lifetracker.overAllStatistics

import androidx.lifecycle.ViewModel
import com.example.lifetracker.database.RoutineDatabaseDao

class OverAllStatisticsViewModel(
        val routineId: Long = 0L,
        val database: RoutineDatabaseDao
) : ViewModel() {

}

