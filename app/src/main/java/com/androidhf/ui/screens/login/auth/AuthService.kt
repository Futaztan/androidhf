package com.androidhf.ui.screens.login.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

object AuthService {

    private val firebaseAuth: FirebaseAuth = Firebase.auth



    fun getUserEmail() : String
    {

        return firebaseAuth.currentUser?.email.toString()

    }
    fun getUserDisplayName() : String
    {

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
    /*TODO*/
    fun setUserDisplayName(newname : String)
    {
        val changeRequest = userProfileChangeRequest { displayName = newname }
        firebaseAuth.currentUser?.updateProfile(changeRequest)
    }



    fun registerWithEmailAndPassword(email: String, password: String, context: Context,onResult: (Boolean) -> Unit)
    {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->

                if (!task.isSuccessful) {
                    Toast.makeText(context, task.exception?.message,Toast.LENGTH_LONG).show()
                }
                else {
                    val changeRequest = userProfileChangeRequest { displayName = getUserEmail().substringBefore('@') }
                    firebaseAuth.currentUser?.updateProfile(changeRequest)
                }
                onResult(task.isSuccessful)
            }
    }

    fun loginWithEmailAndPassword(email: String, password : String, context: Context, onResult: (Boolean) -> Unit)
    {
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(){task->
            if(!task.isSuccessful)
            {
                Toast.makeText(context, task.exception?.message,Toast.LENGTH_LONG).show()
            }
            onResult(task.isSuccessful)
        }
    }

}