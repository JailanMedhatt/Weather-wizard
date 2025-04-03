package com.example.weatherwizard.data.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherwizard.alert.model.AlertModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.hasItem
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

@RunWith(AndroidJUnit4::class)
@SmallTest
class DaoTest {

    private lateinit var database: AppDb
    private lateinit var dao: AlertDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDb::class.java
        ).build()
        dao = database.getAlertDao()
    }

    @After
    fun tearDown() = database.close()




    @Test
    fun insertAndRetrieveAlarm() = runTest {
        //Given --> Create Alarm Object & Insert it (insertAlarm(alarm))
        val alarm = AlertModel(date =  "2025-04-01", time = "08:30")
        dao.insertAlert(alarm)

        //When --> Get All Alarms (getAllAlarms()) & Converting The Flow<List<Alarm>> To List<Alarm> Then To Alarm
        val alarms = dao.getAlerts().first()
        val retrievedAlarm = alarms.first()

        //Then --> Asserting The List "alarms" isNotEmpty & Comparing The Expected Values With The Actually Inserted Ones Without id (as it is auto-generated) "retrievedAlarm"
        assertTrue(alarms.isNotEmpty())
        assertEquals("2025-04-01", retrievedAlarm.date)
        assertEquals("08:30", retrievedAlarm.time)
    }

    @Test
    fun insertAndDeleteAlarm() = runTest {
        //Given --> Create Alarm Object & Insert it (insertAlarm(alarm))
        val alarm = AlertModel(date = "2025-04-01", time = "08:30")
        dao.insertAlert(alarm)

        //When --> Get All Alarms,Converting The Flow<List<Alarm>> To List<Alarm> & Then --> Assert "alarmsBeforeDelete" isNotEmpty
        val alarmsBeforeDelete = dao.getAlerts().first()
        assertTrue(alarmsBeforeDelete.isNotEmpty())

        //When --> Delete Alarm (deleteAlarm(alarm)) - Get All Alarms After Deletion And Converting The Flow<List<Alarm>> To List<Alarm> & Then --> Assert Retrieved List "alarmsAfterDelete" Not Contains The Deleted Alarm
        dao.deleteAlert(alarm)
        val alarmsAfterDelete = dao.getAlerts().first()
        assertFalse(alarmsAfterDelete.contains(alarm))
    }

}