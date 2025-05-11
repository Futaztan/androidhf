package com.androidhf.data.database

import android.util.Log
import com.androidhf.data.RepetitiveTransaction
import com.androidhf.data.Savings
import com.androidhf.data.Transaction
import com.androidhf.ui.screens.login.auth.AuthService
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDB @Inject constructor() {
    private val firestore = Firebase.firestore

    fun addTransactionToFirebase(transaction: Transaction)
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

    fun updateSavingInFirebase(saving: Savings) {
        val user = AuthService.getUserEmail()
        if (saving.id == 0L) {
            Log.e("firestore", "Cannot update saving without a valid ID")
            return
        }

        firestore
            .collection("users")
            .document(user)
            .collection("savings")
            .document(saving.id.toString())
            .set(saving, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("firestore","Saving updated with ID: ${saving.id}")
            }
            .addOnFailureListener { e ->
                Log.d("firestore","Error updating saving: $e")
            }
    }

    fun addRepetitiveTransactionToFireabase(repTransaction: RepetitiveTransaction)
    {
        val user = AuthService.getUserEmail()
        firestore
            .collection("users")
            .document(user)
            .collection("repetitive_transactions")
            .add(repTransaction)
            .addOnSuccessListener { docRef ->
                Log.d("firestore","Transaction added with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.d("firestore","Error adding transaction: $e")
            }
    }


}