package com.androidhf.ui.screens.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.androidhf.ui.screens.login.auth.AuthService


@Composable
fun UserScreen(navHostController: NavHostController)
{
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(AuthService.getUserEmail())
        Text(AuthService.getUserDisplayName())
        Button(onClick = {AuthService.logOut(); navHostController.navigate("home")}) { Text("kijelentkezes")}
    }
}