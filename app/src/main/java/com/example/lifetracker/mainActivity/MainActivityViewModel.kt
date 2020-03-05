package com.example.lifetracker.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lifetracker.database.RoutineDatabaseDao

class MainActivityViewModel() : ViewModel() {
        private val _title = MutableLiveData<String>()
        val title: LiveData<String>
                get() = _title
        fun updateActionBarTitle(title: String) = _title.postValue(title)

        private val _updateStatistics = MutableLiveData<Boolean>()
        val updateStatistics: LiveData<Boolean>
                get() = _updateStatistics

        fun toUpdateStatistics(toUpdate: Boolean) = _updateStatistics.postValue(toUpdate)
}

