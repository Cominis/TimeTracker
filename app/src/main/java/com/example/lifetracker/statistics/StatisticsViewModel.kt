package com.example.lifetracker.statistics

import androidx.lifecycle.ViewModel
import com.example.lifetracker.database.RoutineDatabaseDao

class StatisticsViewModel(
        val routineId: Long = 0L,
        val database: RoutineDatabaseDao
) : ViewModel() {

}

