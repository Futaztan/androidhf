package com.androidhf.ui.screens.login.auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object AuthService {

    private val firebaseAuth: FirebaseAuth = Firebase.auth



    fun getUserEmail() : String
    {
        Log.d("useremail:", firebaseAuth.currentUser?.email.toString())
        return firebaseAuth.currentUser?.email.toString()

    }
    fun getUserDisplayName() : String
    {
        Log.d("userdisplayname:", firebaseAuth.currentUser?.displayName.toString())
        return firebaseAuth.currentUser?.displayName.toString()
    }
    fun logOut()
    {
        firebaseAuth.signOut()
    }
    fun isLoggedIn() : Boolean
    {
        if(firebaseAuth.currentUser==null) return false
        return true
    }



    fun registerWithEmailAndPassword(email: String, password: String, onResult: (Boolean) -> Unit)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                }
                onResult(task.isSuccessful)
            }
    }

    fun loginWithEmailAndPassword(email: String, password : String, onResult: (Boolean) -> Unit)
    {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){task->
            if(task.isSuccessful)
            {
                Log.d("TAG", "signInWithEmail:success")
            }
            else {
                Log.w("TAG", "signInWithEmail:failure", task.exception)
            }
            onResult(task.isSuccessful)
        }
    }

}