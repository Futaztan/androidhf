package com.androidhf.ui.screens.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidhf.ui.reuseable.NameField
import com.androidhf.ui.reuseable.PasswordField
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.login.auth.AuthService


private fun onLogin(email: String, password : String, navController: NavController, context: Context)
{

    AuthService.getUserEmail()
    AuthService.loginWithEmailAndPassword(email,password){ success->
        if(success)
            navController.navigate("home")
        else Toast.makeText(context,"Sikertelen bejelentkezes",Toast.LENGTH_LONG).show()
    }


}

@Composable
fun LoginScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            NameField(
                value = name,
                onChange = { name = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(UIVar.Padding))
            PasswordField(
                value = password,
                onChange = { password=it },
                submit = {onLogin(name,password,navController, context)},
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {onLogin(name,password,navController,context)}) { Text("LOGIN") }
            Button(onClick ={navController.navigate("register")}) { Text("REGISTER")}

        }

    }
}


