package com.dmt.timetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmt.timetracker.domain.DataWithDuration
import com.dmt.timetracker.repository.TimeRepository
import kotlinx.coroutines.*
import java.time.Instant
import java.util.*

class CustomStatisticsViewModel(
        private val repo: TimeRepository
) : ViewModel() {

        private var viewModelJob = Job()
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        private var timestampFrom = Date(System.currentTimeMillis() - 86_400_000L)
        private var timestampTo = Date(System.currentTimeMillis())
        private var weekdays = listOf<String>()

        private val _data = MutableLiveData<List<DataWithDuration>>()
        val data: LiveData<List<DataWithDuration>>
                get() = _data

        init {
                getStatisticsData()
        }

        fun updateDatabase(millisFrom: Long, millisUntil: Long, weekdays: List<String>){

                timestampFrom = Date.from(Instant.ofEpochMilli(millisFrom))
                timestampTo = Date.from(Instant.ofEpochMilli(millisUntil))
                this.weekdays = weekdays

                getStatisticsData()
        }

        private fun getStatisticsData() {
                uiScope.launch {
                        _data.value = repo.getStatisticsData(timestampFrom, timestampTo)
                }
        }

        override fun onCleared() {
                super.onCleared()
                viewModelJob.cancel()
        }
}

