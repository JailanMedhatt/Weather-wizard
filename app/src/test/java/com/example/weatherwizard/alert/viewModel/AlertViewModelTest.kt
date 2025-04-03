package com.example.weatherwizard.alert.viewModel

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherwizard.Repository
import com.example.weatherwizard.alert.model.AlertModel

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import org.hamcrest.CoreMatchers.`is`

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AlertViewModelTest {

    private lateinit var repository: Repository
    private lateinit var viewModel: AlertViewModel
   // private lateinit var context: Context

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        viewModel = AlertViewModel(repository)
      //  context = mockk(relaxed = true)
    }


    @Test
    @Config(manifest = Config.NONE)
    fun insertAlert_validAlert_emitsSuccessMessage() = runTest {
        // Given
        val alarm = AlertModel(date = "2025-04-01", time = "08:30")
        coEvery { repository.insertAlert(alarm) } returns Unit

        // When
        viewModel.insertAlert(alarm)
        advanceUntilIdle() // Ensure coroutine completes

        // Then
        coVerify { repository.insertAlert(alarm) }

        // Now collect the latest message
        val toastMessage = viewModel.message.first { it != null }  // Waits for non-null value
        assertThat(toastMessage, `is`("Alarm added successfully"))
    }


    @Test
    @Config(manifest = Config.NONE)
    fun deleteAlert_validAlert_emitsSuccessMessage() = runTest {

        //Given
        val alarm = AlertModel(date = "2025-04-01", time = "08:30")
        //Mocking
        coEvery { repository.deleteAlert(alarm) } returns Unit

        //When
        viewModel.deleteAlert(alarm)
        advanceUntilIdle() // Use advanceUntilIdle to make sure all coroutines finish execution

        //Then
        coVerify { repository.deleteAlert(alarm) }

        val toastMessage = viewModel.message.first()
        assertThat(toastMessage, `is`("Alarm deleted successfully"))
    }



}