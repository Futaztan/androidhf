package com.androidhf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidhf.ui.screen.AIScreen
import com.androidhf.ui.screen.HomeScreen
import com.androidhf.ui.screen.PenzugyScreen

import com.androidhf.ui.screen.StockScreen
import com.androidhf.ui.theme.AndroidhfTheme

class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidhfTheme {


                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeScreen() }
                        composable("penzugy") { PenzugyScreen() }
                        composable("stock") { StockScreen() }
                        composable("ai") { AIScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Text("Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("penzugy") },
            icon = { Text("Pénzügy") },
            label = { Text("Pénzügy") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("stock") },
            icon = { Text("Stock") },
            label = { Text("Stock") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("ai") },
            icon = { Text("AI") },
            label = { Text("AI") }
        )
    }
}



