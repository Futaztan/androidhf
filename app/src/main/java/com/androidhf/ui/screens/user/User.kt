package com.androidhf.ui.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.androidhf.data.Data
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.login.auth.AuthService


@Composable
fun UserScreen(navHostController: NavHostController)
{
    Data.topBarTitle = "User"
    Box(modifier = Modifier.fillMaxSize().padding(UIVar.Padding))
    {
        BorderBox {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Email: " + AuthService.getUserEmail(), color = UIVar.onBoxColor())
                Text("Name: " + AuthService.getUserDisplayName(), color = UIVar.onBoxColor())
                Button(onClick = {AuthService.logOut(); navHostController.navigate("home")}) { Text("Logout")}
            }
        }
    }

}