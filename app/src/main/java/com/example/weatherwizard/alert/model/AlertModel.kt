package com.example.weatherwizard.alert.model

import androidx.room.Entity

@Entity(tableName = "alerts",primaryKeys = ["date","time"])
data class AlertModel(val date :String, val time :String)
