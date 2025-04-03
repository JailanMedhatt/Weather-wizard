package com.example.weatherwizard.data.database


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherwizard.alert.model.AlertModel
import kotlinx.coroutines.flow.first

import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalDataSourceTest {

    private lateinit var database: AppDb
    private lateinit var dao: AlertDao
    private lateinit var locationDao: LocationDao
    private lateinit var homeDao: HomeDao
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDb::class.java
        )
            .allowMainThreadQueries()
            .build()
        dao = database.getAlertDao()
        locationDao = database.getDao()
        homeDao = database.getHomeDao()
        localDataSource = LocalDataSource(locationDao, dao, homeDao)
    }

    @After
    fun tearDown() {
        database.close()
    }



    @Test
    fun insertAndRetrieveAlarm() = runTest {
        //Given --> Create Alarm Object & Insert it (localDataSource.insertAlarm(alarm))
        val alarm = AlertModel(date =  "2025-04-01", time = "08:30")
        localDataSource.insertAlert(alarm)

        //When --> Get All Alarms (localDataSource.getAllAlarms().first()) & Converting The Flow<List<Alarm>> To List<Alarm> Then To Alarm
        val alarms = localDataSource.getAlerts().first()
        val retrievedAlarm = alarms.first()

        //Then --> Asserting The List "alarms" isNotEmpty & Comparing The Expected Values With The Actually Inserted Ones Without id (as it is auto-generated) "retrievedAlarm"
        assertTrue(alarms.isNotEmpty())
        assertEquals("2025-04-01", retrievedAlarm.date)
        assertEquals("08:30", retrievedAlarm.time)
    }

    @Test
    fun insertAndDeleteAlarm() = runTest {
        //Given --> Create Alarm Object & Insert it Using LocalDataSource (localDataSource.insertAlarm(alarm))
        val alarm = AlertModel(date =  "2025-04-01", time = "08:30")
        localDataSource.insertAlert(alarm)

        //When --> Get All Alarms Using LocalDataSource & Converting The Flow<List<Alarm>> To List<Alarm> Then To Alarm & Delete Alarm & Get All Alarms Using LocalDataSource & Converting The Flow<List<Alarm>> To List<Alarm> After Deletion
        val alarmsBeforeDelete = localDataSource.getAlerts().first()
        val retrievedAlarm = alarmsBeforeDelete.first()
        localDataSource.deleteAlert(retrievedAlarm)
        val alarmsAfterDelete = localDataSource.getAlerts().first()

        //Then --> Asserting "alarmsAfterDelete" isEmpty
        assertTrue(alarmsAfterDelete.isEmpty())
    }
}