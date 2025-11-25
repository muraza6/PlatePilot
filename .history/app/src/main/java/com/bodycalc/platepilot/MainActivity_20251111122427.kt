package com.bodycalc.platepilotpackage com.bodycalc.platepilotpackage com.bodycalc.platepilot



import android.os.Bundle

import androidx.activity.ComponentActivity

import androidx.activity.compose.setContentimport android.os.Bundleimport android.os.Bundle

import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSizeimport androidx.activity.ComponentActivityimport androidx.activity.ComponentActivity

import androidx.compose.material3.Scaffold

import androidx.compose.runtime.getValueimport androidx.activity.compose.setContentimport androidx.activity.compose.setContent

import androidx.compose.ui.Modifier

import androidx.navigation.compose.currentBackStackEntryAsStateimport androidx.activity.enableEdgeToEdgeimport androidx.activity.enableEdgeToEdge

import androidx.navigation.compose.rememberNavController

import com.bodycalc.platepilot.ui.components.BottomNavigationBarimport androidx.compose.foundation.layout.fillMaxSizeimport androidx.compose.foundation.layout.fillMaxSize

import com.bodycalc.platepilot.ui.navigation.AppNavigation

import com.bodycalc.platepilot.ui.navigation.Screenimport androidx.compose.material3.Scaffoldimport androidx.compose.foundation.layout.padding

import com.bodycalc.platepilot.ui.theme.PlatePilotTheme

import androidx.compose.runtime.getValueimport androidx.compose.material3.Scaffold

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {import androidx.compose.ui.Modifierimport androidx.compose.material3.Text

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()import androidx.navigation.compose.currentBackStackEntryAsStateimport androidx.compose.runtime.Composable

        setContent {

            PlatePilotTheme {import androidx.navigation.compose.rememberNavControllerimport androidx.compose.ui.Modifier

                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()import com.bodycalc.platepilot.ui.components.BottomNavigationBarimport androidx.compose.ui.tooling.preview.Preview

                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

                import com.bodycalc.platepilot.ui.navigation.AppNavigationimport com.bodycalc.platepilot.ui.theme.PlatePilotTheme

                // Determine if we should show the bottom nav

                val showBottomBar = currentRoute in listOf(import com.bodycalc.platepilot.ui.navigation.Screen

                    Screen.Home.route,

                    Screen.Plans.route,import com.bodycalc.platepilot.ui.theme.PlatePilotThemeclass MainActivity : ComponentActivity() {

                    Screen.Profile.route

                )    override fun onCreate(savedInstanceState: Bundle?) {

                

                Scaffold(class MainActivity : ComponentActivity() {        super.onCreate(savedInstanceState)

                    modifier = Modifier.fillMaxSize(),

                    bottomBar = {    override fun onCreate(savedInstanceState: Bundle?) {        enableEdgeToEdge()

                        if (showBottomBar) {

                            BottomNavigationBar(        super.onCreate(savedInstanceState)        setContent {

                                currentRoute = currentRoute,

                                onNavigate = { route ->        enableEdgeToEdge()            PlatePilotTheme {

                                    navController.navigate(route) {

                                        popUpTo(Screen.Home.route) {        setContent {                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                                            saveState = true

                                        }            PlatePilotTheme {                    Greeting(

                                        launchSingleTop = true

                                        restoreState = true                val navController = rememberNavController()                        name = "Android",

                                    }

                                }                val navBackStackEntry by navController.currentBackStackEntryAsState()                        modifier = Modifier.padding(innerPadding)

                            )

                        }                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route                    )

                    }

                ) { innerPadding ->                                }

                    AppNavigation(

                        navController = navController,                // Determine if we should show the bottom nav            }

                        startDestination = Screen.Home.route

                    )                val showBottomBar = currentRoute in listOf(        }

                }

            }                    Screen.Home.route,    }

        }

    }                    Screen.Plans.route,}

}

                    Screen.Profile.route

                )@Composable

                fun Greeting(name: String, modifier: Modifier = Modifier) {

                Scaffold(    Text(

                    modifier = Modifier.fillMaxSize(),        text = "Hello $name!",

                    bottomBar = {        modifier = modifier

                        if (showBottomBar) {    )

                            BottomNavigationBar(}

                                currentRoute = currentRoute,

                                onNavigate = { route ->@Preview(showBackground = true)

                                    navController.navigate(route) {@Composable

                                        // Pop up to the start destination to avoid building up a large stackfun GreetingPreview() {

                                        popUpTo(Screen.Home.route) {    PlatePilotTheme {

                                            saveState = true        Greeting("Android")

                                        }    }

                                        // Avoid multiple copies of the same destination}
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        startDestination = Screen.Home.route
                    )
                }
            }
        }
    }
}
