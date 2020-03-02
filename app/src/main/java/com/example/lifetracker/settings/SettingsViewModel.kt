package com.example.lifetracker.settings

import androidx.lifecycle.ViewModel
import com.example.lifetracker.database.RoutineDatabaseDao

class SettingsViewModel(
        val routineId: Long = 0L,
        val database: RoutineDatabaseDao
) : ViewModel() {

}

