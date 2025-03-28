package com.example.weatherwizard
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Network.RetrofitHelper
import com.example.weatherwizard.data.database.AppDb
import com.example.weatherwizard.data.database.LocalDataSource
import com.example.weatherwizard.home.viewModel.HomeViewModel
import com.example.weatherwizard.navigationUtills.MyNavHost
import com.example.weatherwizard.navigationUtills.navBar
import com.example.weatherwizard.ui.theme.primary
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

const val TAG = "MainActivity"
const val REQUEST_LOCATION_CODE = 2005
class MainActivity : ComponentActivity() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        homeViewModel= ViewModelProvider(this, HomeViewModel.MyFactory(Repository.getInstance(
            RemoteDataSource(RetrofitHelper.retrofitInstance),
            LocalDataSource(AppDb.getInstance(this).getDao())
        ))).
        get(HomeViewModel::class.java)
        applySavedLanguage(this)
       // (application as MyApplication).applySavedLanguage(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        setContent {
//            homeViewModel.languageState = remember { mutableStateOf("en") }
            homeViewModel.locationState = remember { mutableStateOf(Location(LocationManager.GPS_PROVIDER)) }

            MainScreen()

            //Log.i(TAG, "onCreate: ${locationState.value.latitude}")

        }

    }

    fun applySavedLanguage(context: Context) {
        val sharedPreferences = SharedPref.getInstance(context)
        val language = sharedPreferences.getLanguage() ?: "en"
        Log.i("tag", "applySavedLanguage:$language ")
       // homeViewModel.languageState.value= language
        homeViewModel.setLanguage(language)
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
    @OptIn(ExperimentalGlideComposeApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun MainScreen() {

        val snackBarHostState = remember { SnackbarHostState() }
        val gradient = Brush.verticalGradient(colors =
        listOf(Color(0xFF08244F),Color(0xFF134CB5),
            Color(0xFF0B42AB)))

        val navController = rememberNavController()
        Box (modifier = Modifier
            .fillMaxSize()
            .background(gradient)){
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = { navBar(navController) },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets ,
            snackbarHost = {
                SnackbarHost(snackBarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        contentColor = Color.Black, // Custom text color
                        actionColor = primary // Custom action label color,
,                        containerColor = Color.White// Custom background color
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {

                MyNavHost(navController, homeViewModel,snackBarHostState)


        }}
    }}
    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), REQUEST_LOCATION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
            }
        }
    }

    fun checkPermissions(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        homeViewModel.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        homeViewModel.fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)
                    homeViewModel.locationState.value = location.lastLocation!!
                }
            }, Looper.myLooper()
        )
    }

    fun enableLocationServices() {
        Toast.makeText(this, "Turn on Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

}