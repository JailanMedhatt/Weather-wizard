package com.example.weatherwizard.navigationUtills

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherwizard.home.view.HomeScreen
import com.example.weatherwizard.home.viewModel.HomeViewModel
import com.example.weatherwizard.settings.view.SettingScreen


@ExperimentalGlideComposeApi
@Composable
fun MyNavHost(myNavController: NavHostController,homeViewMode:HomeViewModel){
  // val myNavController = rememberNavController()
    NavHost(navController =myNavController, startDestination = ScreenRoute.HomeRoute){
     composable<ScreenRoute.HomeRoute> {
         HomeScreen(homeViewMode)
         //RegisterUi(navigateToLogin = {email,pass-> myNavController.navigate(ScreenRoute.LoginRoute(email,pass))}
        // )
     }
        composable<ScreenRoute.FavRoute> {
//           backStackEntry -> val args:ScreenRoute.LoginRoute = backStackEntry.toRoute()
//            LoginUi(enteredEmail = args.name, enteredPass = args.pass,
//                navigatetoHome = {email-> myNavController.navigate(ScreenRoute.HomeRoute(email))})
        }
       composable<ScreenRoute.SettingsRoute> {
//                backStackEntry -> val args:ScreenRoute.HomeRoute = backStackEntry.toRoute()
//          HomeUi(args.name)
           SettingScreen()
        }

    }
}



