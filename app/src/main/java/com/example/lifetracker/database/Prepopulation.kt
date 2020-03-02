package com.example.lifetracker.database

object Prepopulation {

    fun populateInitialData(routineDatabaseDao: RoutineDatabaseDao) {
        for (i in 0..5){
            val activity = Activity()
            activity.name = "PREP:$i"
            routineDatabaseDao.insertActivity(activity)
        }
    }

}