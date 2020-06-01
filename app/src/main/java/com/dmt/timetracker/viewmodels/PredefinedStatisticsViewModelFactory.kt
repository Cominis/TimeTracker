package com.dmt.timetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmt.timetracker.repository.TimeRepository

class PredefinedStatisticsViewModelFactory(
        private val repo: TimeRepository,
        private val timestampFrom: Long,
        private val timestampTo: Long
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredefinedStatisticsViewModel::class.java)) {
            return PredefinedStatisticsViewModel(
                repo,
                timestampFrom,
                timestampTo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
