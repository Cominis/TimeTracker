package com.dmt.timetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmt.timetracker.domain.DataWithDuration
import com.dmt.timetracker.repository.TimeRepository
import kotlinx.coroutines.*
import java.util.*

class PredefinedStatisticsViewModel(
        private val repo: TimeRepository,
        private val timestampFrom: Long,
        private val timestampTo: Long
) : ViewModel() {

        private var viewModelJob = Job()
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        private val _data = MutableLiveData<List<DataWithDuration>>()
        val data: LiveData<List<DataWithDuration>>
                get() = _data

        init {
                getStatisticsData()
        }

        private fun getStatisticsData() {
                uiScope.launch {
                        _data.value = getStatisticsDataFromDatabase()
                }
        }

        private suspend fun getStatisticsDataFromDatabase(): List<DataWithDuration> {
                return withContext(Dispatchers.IO) {
                        repo.getStatisticsData(Date(timestampFrom), Date(timestampTo))
                }
        }

        override fun onCleared() {
                super.onCleared()
                viewModelJob.cancel()
        }
}

