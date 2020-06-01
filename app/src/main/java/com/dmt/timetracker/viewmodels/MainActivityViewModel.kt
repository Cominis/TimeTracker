package com.dmt.timetracker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel constructor(isDark: Boolean) : ViewModel() {

        private val _title = MutableLiveData<String>()
        val title: LiveData<String>
                get() = _title

        fun updateActionBarTitle(title: String) = _title.postValue(title)

        private val _updateStatistics = MutableLiveData<Boolean>()
        val updateStatistics: LiveData<Boolean>
                get() = _updateStatistics

        fun toUpdateStatistics(toUpdate: Boolean) = _updateStatistics.postValue(toUpdate)


        private val _updateTheme = MutableLiveData<Boolean>()
        val updateTheme: LiveData<Boolean>
                get() = _updateTheme

        init{
                _updateTheme.value = isDark
        }

        fun changeToDarkTheme(toUpdate: Boolean) = _updateTheme.postValue(toUpdate)
}

