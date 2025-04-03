package com.example.weatherwizard.navigationUtills
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherwizard.R
import com.example.weatherwizard.alert.view.AlertScreen
import com.example.weatherwizard.fav.map.MapScreen
import com.example.weatherwizard.fav.view.FavouriteScreen
import com.example.weatherwizard.home.view.HomeScreen
import com.example.weatherwizard.home.viewModel.HomeViewModel
import com.example.weatherwizard.settings.view.SettingScreen
import kotlinx.coroutines.delay

@ExperimentalGlideComposeApi
@Composable
fun MyNavHost(myNavController: NavHostController,homeViewMode:HomeViewModel,snackBarHostState: SnackbarHostState){
  // val myNavController = rememberNavController()
    NavHost(navController =myNavController, startDestination =
    ScreenRoute.HomeRoute(0.0,0.0,"{}"),
        ){
     composable<ScreenRoute.HomeRoute> {
                   backStackEntry -> val args:ScreenRoute.HomeRoute = backStackEntry.toRoute()
         Log.i("tag", "${args.latitude} ${args.longitude} ")
         HomeScreen(homeViewMode,args.weatherObj)
                                    }
        composable<ScreenRoute.FavRoute> {
            FavouriteScreen(onNavigateToMap = {myNavController.navigate(ScreenRoute.MapRoute(false))},snackBarHostState,
                onNavigateToHome = {location,obj->myNavController.navigate(ScreenRoute.HomeRoute(location.longitude,location.latitude,obj))})

        }
       composable<ScreenRoute.SettingsRoute> {
           SettingScreen(onNavigateToMap = {myNavController.navigate(ScreenRoute.MapRoute(true))})
        }
        composable<ScreenRoute.MapRoute> {
                backStackEntry -> val args:ScreenRoute.MapRoute = backStackEntry.toRoute()
            MapScreen(snackBarHostState,args.fromSettings,popBackStack = {myNavController.popBackStack()})
        }

       composable<ScreenRoute.AlertRoute> {
           AlertScreen()

           }
        composable<ScreenRoute.SplashRoute> {
            SplashScreen({myNavController.navigate(ScreenRoute.HomeRoute(0.0,0.0,"{}")){

            }
            })
        }
    }
}
@Composable
fun SplashScreen(navigateToHome :()->Unit) {


    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        navigateToHome
      //  isVisible = false
//        navController.navigate("home") {
//            popUpTo("splash") { inclusive = true }
//        }
    }


        Box(
            modifier = Modifier
                .fillMaxSize(), // Change to your brand color
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))
                    val progress by animateLottieCompositionAsState(
                        composition = composition.value,
                        iterations = LottieConstants.IterateForever,
                        isPlaying = true // Change this to control play/pause
                    )

                    LottieAnimation(
                        composition = composition.value,
                        progress = progress,
                        Modifier.padding(horizontal = 128.dp, vertical = 256.dp).size(250.dp)

                    )


            }
        }

}



