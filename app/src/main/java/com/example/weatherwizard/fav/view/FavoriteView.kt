package com.example.weatherwizard.fav.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherwizard.MyColors
import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Network.RetrofitHelper
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.R
import com.example.weatherwizard.Repository
import com.example.weatherwizard.data.database.AppDb
import com.example.weatherwizard.data.database.LocalDataSource
import com.example.weatherwizard.data.model.FavoriteLocation
import com.example.weatherwizard.fav.viewModel.FavouriteViewModel
import com.example.weatherwizard.ui.theme.darkSecondary
import kotlinx.serialization.json.Json

@Composable
fun FavouriteScreen(onNavigateToMap:()->Unit, snackBarHostState: SnackbarHostState,onNavigateToHome:(location:FavoriteLocation,obj:String)->Unit){
    val context = LocalContext.current
    val FavFactory = FavouriteViewModel.FavouriteViewModelFactory(
        repository = Repository.getInstance(
            RemoteDataSource(RetrofitHelper.retrofitInstance), LocalDataSource(AppDb.getInstance(context).getDao(),AppDb.getInstance(context).getAlertDao(),AppDb.getInstance(context).getHomeDao())

        )
    )
    val viewModel:FavouriteViewModel= viewModel(factory = FavFactory)

LaunchedEffect (Unit){
    viewModel.getFavouriteLocations()
}
    val message = viewModel.message
    LaunchedEffect(message) {
        message.collect{
           val result= snackBarHostState.showSnackbar(it, actionLabel = "Undo", duration = SnackbarDuration.Short)
            if (result == SnackbarResult.ActionPerformed) {
                // Undo action triggered
                viewModel.restoreLocation()
            }

        }
    }
    val locations = viewModel.locations.collectAsStateWithLifecycle()
    Box(Modifier.fillMaxSize()){
    Column (Modifier.fillMaxSize()) {
        locations.value.forEachIndexed { index, favoriteLocation ->
            LocationCard(action = {
                viewModel.deleteFavouriteLocation(favoriteLocation)
            }, location = favoriteLocation,onNavigateToHome)
        }
    }
        FloatingActionButton(onClick = { onNavigateToMap() }
        , modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 22.dp, vertical = 64.dp), containerColor = Color.White)
        {
           Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = MyColors.primary.color)
        }}

}

@Composable
fun LocationCard(action :(FavoriteLocation)->Unit,location: FavWeatherDetails,onNavigateToHome:(location:FavoriteLocation,obj:String)->Unit){
    Row (Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(darkSecondary, shape = RoundedCornerShape(16.dp))
        .padding(vertical = 32.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween){
        Text(text = location.favoriteLocation.address, color = Color.White, fontSize = 22.sp,
            modifier = Modifier.padding(top=8.dp))
        Row{
        Button(onClick = {  action.invoke(location.favoriteLocation)},
            colors = ButtonDefaults.buttonColors(
            containerColor = MyColors.primary.color
        ), shape = RoundedCornerShape(12.dp)) {
            Text(stringResource(R.string.delete),fontSize = 18.sp)
        }
        IconButton(onClick = {
            val weatherJson = Json.encodeToString(location)

            onNavigateToHome(location.favoriteLocation,weatherJson)}) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Favorite",
                tint = Color.White
            )
        }}}
}
@Preview
@Composable
fun LocationCardPreview(){
    Row (Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .background(darkSecondary, shape = RoundedCornerShape(16.dp))
        .padding(32.dp), horizontalArrangement = Arrangement.SpaceBetween){
         Text(text = "Place", color = Color.White, fontSize = 22.sp,
             modifier = Modifier.padding(top=8.dp))
        Button(onClick = {}, colors = ButtonDefaults.buttonColors(
            containerColor = MyColors.primary.color
            ), ) {
            Text("Delete")
        }
    }

}