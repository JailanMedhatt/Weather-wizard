package com.example.weatherwizard.alert.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherwizard.MyColors
import com.example.weatherwizard.MyWorker
import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Network.RetrofitHelper
import com.example.weatherwizard.Repository
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.alert.viewModel.AlertViewModel
import com.example.weatherwizard.data.database.AppDb
import com.example.weatherwizard.data.database.LocalDataSource
import com.example.weatherwizard.ui.theme.DarkPrimary
import com.example.weatherwizard.ui.theme.darkSecondary
import java.util.Calendar
import java.util.concurrent.TimeUnit


@Preview
@Composable
fun AlertScreen() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            101
        )
    }

    val FavFactory = AlertViewModel.AlertViewModelFactory(
        repository = Repository.getInstance(
            RemoteDataSource(RetrofitHelper.retrofitInstance), LocalDataSource(
                AppDb.getInstance(context).getDao(),
                AppDb.getInstance(context).getAlertDao(),AppDb.getInstance(context).getHomeDao())

        )
    )
    val viewModel: AlertViewModel = viewModel(factory = FavFactory)
    val showBottomSheet = remember { mutableStateOf(false) } // Fixed mutable state
    LaunchedEffect(Unit) {
        viewModel.getAlerts()
    }
    val alerts = viewModel.alerts.collectAsStateWithLifecycle()
    alerts.value.forEach {
        println(it.date)
        println(it.time)
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            alerts.value.forEach {
                AlertCard(it, action = {viewModel.deleteAlert(it)})
            }
        }

        FloatingActionButton(
            onClick = { showBottomSheet.value = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 22.dp, vertical = 64.dp),
            containerColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Open Bottom Sheet",
                tint = MyColors.primary.color
            )
        }

        if (showBottomSheet.value) {
            BottomSheetContent(showBottomSheet,viewModel)

        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(showBottomSheet: MutableState<Boolean>,viewModel: AlertViewModel){
    val context = LocalContext.current
    val calendar = Calendar.getInstance() // Get today's date


    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDate =remember { mutableStateOf("Alert Date") } // Store selected date
   val verified = remember { mutableStateOf(true) }
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            selectedDate.value = "$selectedDay/${selectedMonth + 1}/$selectedYear" // Format selected date
        },
        year,
        month,
        day
    ).apply {
        datePicker.minDate = calendar.timeInMillis // Set minimum date to today
    }
 // Get current time

    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    var selectedTime =remember { mutableStateOf("Alert Time") } // Store selected time

    val timePickerDialog = TimePickerDialog(
        context,
        { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
            selectedTime.value = String.format("%02d:%02d", selectedHour, selectedMinute) // Format time as HH:mm
        },
        hour,
        minute,
        true // 24-hour format
    )
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { showBottomSheet.value = false },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Date", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { datePickerDialog.show() }, colors = ButtonDefaults.buttonColors(
                Color.White
            ), modifier = Modifier.padding(vertical = 16.dp), shape = RoundedCornerShape(8.dp)) {
                Text(selectedDate.value,color = DarkPrimary)
            }
            Text("Time", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { timePickerDialog.show() }, colors = ButtonDefaults.buttonColors(
                Color.White
            ), modifier = Modifier.padding(vertical = 16.dp), shape = RoundedCornerShape(8.dp)) {
                Text(selectedTime.value,color = DarkPrimary)
            }
            if(verified.value == false){
                Text("Incorrect time",color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row (Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp), horizontalArrangement = Arrangement.SpaceBetween){
                Button(
                onClick = {

                    val delay = viewModel.calculateDelay(selectedDate.value,selectedTime.value)
                    if (delay>0){
                    var request = OneTimeWorkRequestBuilder<MyWorker>().setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .build()
                    WorkManager.getInstance(context).enqueue(request)
                        verified.value=true
                    viewModel.insertAlert(AlertModel(selectedDate.value,selectedTime.value))
                    showBottomSheet.value = false}
                    else{
                        verified.value = false
                    }
                          },
                colors = ButtonDefaults.buttonColors(
                    DarkPrimary
                ), shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save",fontSize = 18.sp)
            }
                Button(
                    onClick = { showBottomSheet.value = false },
                    colors = ButtonDefaults.buttonColors(
                        DarkPrimary
                    ), shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel",fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert : AlertModel,action :(AlertModel)->Unit){
    Row (Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .background(darkSecondary, shape = RoundedCornerShape(16.dp))
        .padding(vertical = 32.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween){
        Row(Modifier.padding(top = 8.dp)){
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Favorite",
                tint = Color.White, modifier = Modifier.padding(top = 8.dp)
            )
            Text(text = "Date : ${alert.date}", color = Color.White, fontSize = 18.sp,

                modifier = Modifier.padding(top=8.dp, start = 16.dp, end = 16.dp))
            Text(text = "Time : ${alert.time}", color = Color.White, fontSize = 18.sp,

                modifier = Modifier.padding(top=8.dp))
        }



Button(onClick = { action.invoke(alert) }, colors = ButtonDefaults.buttonColors(DarkPrimary), modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Favorite",
                    tint = Color.White, modifier = Modifier.padding(top=0.dp)
                )

        }}
}