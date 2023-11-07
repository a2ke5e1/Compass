package haldia.hit.compass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import haldia.hit.compass.core.main.MainViewModel
import haldia.hit.compass.core.ui.screens.Home
import haldia.hit.compass.core.ui.screens.Profile
import haldia.hit.compass.core.ui.theme.CompassTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val items = listOf(
                Screen.Home,
                Screen.Profile
            )

            CompassTheme {

                val navController = rememberNavController()
                val mainViewModel: MainViewModel by viewModels()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { screen ->

                                val itemSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                val icon =  if (itemSelected) screen.selectedIcon else screen.icon

                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = null) },
                                    label = { Text(stringResource(screen.resourceId)) },
                                    selected = itemSelected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
                        composable(Screen.Home.route) { Home(navController, model =  mainViewModel) }
                        composable(Screen.Profile.route) { Profile(navController) }
                    }
                }
            }
        }
    }
}

