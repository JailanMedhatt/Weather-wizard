package com.example.weatherwizard.navigationUtills
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.weatherwizard.MyColors
import com.example.weatherwizard.R
import com.example.weatherwizard.ui.theme.orange

@Composable
fun navBar(navController: NavHostController){
    val navigationItems = listOf(
        NavigationItem(
            title = stringResource(R.string.home),
            icon = Icons.Default.Home,
            route = ScreenRoute.HomeRoute(0.0,0.0)
        ),
        NavigationItem(
            title = stringResource(R.string.favourite),
            icon = Icons.Default.Favorite,
            route = ScreenRoute.FavRoute
        ),
        NavigationItem(
            title = stringResource(R.string.alert),
            icon = Icons.Default.Notifications,
            route = ScreenRoute.AlertRoute
        ),
        NavigationItem(
            title = stringResource(R.string.setting),
            icon = Icons.Default.Settings,
            route = ScreenRoute.SettingsRoute
        )
    )
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar (
        containerColor = Color.Transparent
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color =Color.White
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = MyColors.secondary.color
                    , unselectedIconColor = Color.White
                )

            )
        }
    }

}
@Preview(
)
@Composable
fun navBarP(){
    val navigationItems = listOf(
        NavigationItem(
            title = stringResource(R.string.home),
            icon = Icons.Default.Home,
            route = ScreenRoute.HomeRoute(0.0,0.0)
        ),
        NavigationItem(
            title = stringResource(R.string.favourite),
            icon = Icons.Default.Favorite,
            route = ScreenRoute.FavRoute
        ),
        NavigationItem(
            title = stringResource(R.string.alert),
            icon = Icons.Default.Notifications,
            route = ScreenRoute.AlertRoute
        ),
        NavigationItem(
            title = stringResource(R.string.setting),
            icon = Icons.Default.Settings,
            route = ScreenRoute.SettingsRoute
        )
    )
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar (
        containerColor = Color.Transparent
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index

                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color = Color.White
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    indicatorColor = MyColors.primary.color
                    , unselectedIconColor = Color.White
                )

            )
        }
    }

}