package com.bodycalc.platepilotpackage com.bodycalc.platepilot



import android.os.Bundleimport android.os.Bundle

import androidx.activity.ComponentActivityimport androidx.activity.ComponentActivity

import androidx.activity.compose.setContentimport androidx.activity.compose.setContent

import androidx.activity.enableEdgeToEdgeimport androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.layout.fillMaxSizeimport androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Scaffoldimport androidx.compose.foundation.layout.padding

import androidx.compose.runtime.getValueimport androidx.compose.material3.Scaffold

import androidx.compose.ui.Modifierimport androidx.compose.material3.Text

import androidx.navigation.compose.currentBackStackEntryAsStateimport androidx.compose.runtime.Composable

import androidx.navigation.compose.rememberNavControllerimport androidx.compose.ui.Modifier

import com.bodycalc.platepilot.ui.components.BottomNavigationBarimport androidx.compose.ui.tooling.preview.Preview

import com.bodycalc.platepilot.ui.navigation.AppNavigationimport com.bodycalc.platepilot.ui.theme.PlatePilotTheme

import com.bodycalc.platepilot.ui.navigation.Screen

import com.bodycalc.platepilot.ui.theme.PlatePilotThemeclass MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

class MainActivity : ComponentActivity() {        super.onCreate(savedInstanceState)

    override fun onCreate(savedInstanceState: Bundle?) {        enableEdgeToEdge()

        super.onCreate(savedInstanceState)        setContent {

        enableEdgeToEdge()            PlatePilotTheme {

        setContent {                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

            PlatePilotTheme {                    Greeting(

                val navController = rememberNavController()                        name = "Android",

                val navBackStackEntry by navController.currentBackStackEntryAsState()                        modifier = Modifier.padding(innerPadding)

                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route                    )

                                }

                // Determine if we should show the bottom nav            }

                val showBottomBar = currentRoute in listOf(        }

                    Screen.Home.route,    }

                    Screen.Plans.route,}

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
