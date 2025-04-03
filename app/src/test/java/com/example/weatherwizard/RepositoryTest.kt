package com.example.weatherwizard

import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.data.database.LocalDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class RepositoryTest {

    private lateinit var repository: Repository
    private val remoteDataSource: RemoteDataSource = mockk(relaxed = true)
    private val localDataSource: LocalDataSource = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = Repository.getInstance(remoteDataSource= remoteDataSource,localDataSource= localDataSource)
    }

    @Test
    fun insertAlarm_shouldCallLocalDataSourceInsertAlarm() = runTest {

        //Given
        val alarm = AlertModel(date = "2025-04-01", time = "08:30")

        //When
        repository.insertAlert(alarm)

        //That
        coVerify { localDataSource.insertAlert(alarm) }
    }

    @Test
    fun deleteAlarm_shouldCallLocalDataSourceInsertAlarm() = runTest {

        //Given
        val alarm = AlertModel(date = "2025-04-01", time = "08:30")

        //When
        repository.deleteAlert(alarm)

        //That
        coVerify { localDataSource.deleteAlert(alarm) }
    }


}