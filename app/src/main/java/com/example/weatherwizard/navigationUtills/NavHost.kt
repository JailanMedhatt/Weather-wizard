package com.example.weatherwizard.navigationUtills

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherwizard.data.model.FavoriteLocation
import com.example.weatherwizard.fav.map.MapScreen
import com.example.weatherwizard.fav.view.FavouriteScreen
import com.example.weatherwizard.home.view.HomeScreen
import com.example.weatherwizard.home.viewModel.HomeViewModel
import com.example.weatherwizard.settings.view.SettingScreen


@ExperimentalGlideComposeApi
@Composable
fun MyNavHost(myNavController: NavHostController,homeViewMode:HomeViewModel,snackBarHostState: SnackbarHostState){
  // val myNavController = rememberNavController()
    NavHost(navController =myNavController, startDestination = ScreenRoute.HomeRoute(0.0,0.0)){
     composable<ScreenRoute.HomeRoute> {
                   backStackEntry -> val args:ScreenRoute.HomeRoute = backStackEntry.toRoute()
         Log.i("tag", "${args.latitude} ${args.longitude} ")
         HomeScreen(homeViewMode, FavoriteLocation(args.longitude?:0.0,args.latitude?:0.0,""))
         //RegisterUi(navigateToLogin = {email,pass-> myNavController.navigate(ScreenRoute.LoginRoute(email,pass))}
        // )
     }
        composable<ScreenRoute.FavRoute> {
            FavouriteScreen(onNavigateToMap = {myNavController.navigate(ScreenRoute.MapRoute(false))},snackBarHostState,onNavigateToHome = {location->myNavController.navigate(ScreenRoute.HomeRoute(location.longitude,location.latitude))})
//           backStackEntry -> val args:ScreenRoute.LoginRoute = backStackEntry.toRoute()
//            LoginUi(enteredEmail = args.name, enteredPass = args.pass,
//                navigatetoHome = {email-> myNavController.navigate(ScreenRoute.HomeRoute(email))})
        }
       composable<ScreenRoute.SettingsRoute> {
//                backStackEntry -> val args:ScreenRoute.HomeRoute = backStackEntry.toRoute()
//          HomeUi(args.name)
           SettingScreen(onNavigateToMap = {myNavController.navigate(ScreenRoute.MapRoute(true))})
        }
        composable<ScreenRoute.MapRoute> {
                backStackEntry -> val args:ScreenRoute.MapRoute = backStackEntry.toRoute()
            MapScreen(snackBarHostState,args.fromSettings,popBackStack = {myNavController.popBackStack()})
        }

    }
}



