package com.androidhf.data.database

import android.util.Log
import com.androidhf.data.datatypes.Category
import com.androidhf.data.datatypes.Frequency
import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.datatypes.Transaction
import com.androidhf.ui.screens.login.auth.AuthService
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDB @Inject constructor() {
    private val firestore = Firebase.firestore



    fun getTransactionsFromFirebase() : ArrayList<Transaction>
    {

        val transactionList = ArrayList<Transaction>()
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("transactions")
            .get()
            .addOnSuccessListener { result->
                for(transaction in result)
                {
                    val amount = transaction.getLong("amount")!!.toInt()
                    val categoryString = transaction.getString("category")!!
                    val category = enumValueOf<Category>(categoryString)
                    val description = transaction.getString("description")!!
                    val frequencyString = transaction.getString("frequency") !!
                    val frequency = enumValueOf<Frequency>(frequencyString)
                    val id = transaction.getLong("id")!!

                    // DÁTUM mező
                    val dateMap = transaction.get("date") as Map<*, *>
                    val year = (dateMap.get("year") as Long).toInt() 
                    val month = (dateMap.get("monthValue") as Long).toInt() 
                    val day = (dateMap.get("dayOfMonth") as Long).toInt()
                    val date = LocalDate.of(year,month,day)

                    // IDŐ mező
                    val timeMap = transaction.get("time") as Map<*, *>
                    val hour = (timeMap.get("hour") as Long).toInt() 
                    val minute = (timeMap.get("minute") as Long).toInt() 
                    val second = (timeMap.get("second") as Long).toInt() 
                    val time = LocalTime.of(hour, minute, second)

                    transactionList.add(Transaction(amount,description,date,time,category,frequency,id))
                }
            }
        return transactionList
    }

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
        if (saving.Id == 0L) {
            Log.e("firestore", "Cannot update saving without a valid ID")
            return
        }

        firestore
            .collection("users")
            .document(user)
            .collection("savings")
            .document(saving.Id.toString())
            .set(saving, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("firestore","Saving updated with ID: ${saving.Id}")
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