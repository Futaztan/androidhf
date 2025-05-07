package com.androidhf.data.database

import android.util.Log
import com.androidhf.data.Savings
import com.androidhf.data.Transaction
import com.androidhf.ui.screens.login.auth.AuthService
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FirebaseDB {
    private val firestore = Firebase.firestore

    fun addTranasctionToFirebase(transaction: Transaction)
    {
        val user = AuthService.getUserEmail()
        firestore
            .collection("users")
            .document(user)
            .collection("transactions")
            .add(transaction)
            .addOnSuccessListener { docRef ->
                Log.d("firestore","Transaction added with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.d("firestore","Error adding transaction: $e")
            }
    }

    fun addSavingToFirebase(saving : Savings)
    {
        val user = AuthService.getUserEmail()
        firestore
            .collection("users")
            .document(user)
            .collection("savings")
            .add(saving)
            .addOnSuccessListener { docRef ->
                Log.d("firestore","Transaction added with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.d("firestore","Error adding transaction: $e")
            }

    }
}