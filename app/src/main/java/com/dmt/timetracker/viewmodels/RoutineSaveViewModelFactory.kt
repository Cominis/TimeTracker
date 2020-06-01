package com.dmt.timetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmt.timetracker.repository.TimeRepository

class RoutineSaveViewModelFactory(
        private val repo: TimeRepository
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineSaveViewModel::class.java)) {
            return RoutineSaveViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
