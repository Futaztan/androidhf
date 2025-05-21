package com.androidhf.data.database

import android.util.Log
import com.androidhf.data.datatypes.Company
import com.androidhf.data.enums.Category
import com.androidhf.data.enums.Frequency
import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.datatypes.Savings
import com.androidhf.data.datatypes.Stock
import com.androidhf.data.datatypes.Transaction
import com.androidhf.data.enums.SavingsType
import com.androidhf.ui.screens.login.auth.AuthService
import com.google.firebase.Firebase
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class FirebaseDB @Inject constructor() {
    private val firestore = Firebase.firestore



    suspend fun getAllTransactionsFromFirebase(): List<Transaction> = suspendCoroutine { continuation ->
        val transactionList = ArrayList<Transaction>()
        val user = AuthService.getUserEmail()

        firestore.collection("users")
            .document(user)
            .collection("transactions")
            .get()
            .addOnSuccessListener { result ->

                    for (transaction in result) {
                        val amount = transaction.getLong("amount")!!.toInt()
                        val category = enumValueOf<Category>(transaction.getString("category")!!)
                        val description = transaction.getString("description")!!
                        val frequency = enumValueOf<Frequency>(transaction.getString("frequency")!!)
                        val id = transaction.getLong("id")!!

                        val dateMap = transaction.get("date") as Map<*, *>
                        val date = LocalDate.of(
                            (dateMap["year"] as Long).toInt(),
                            (dateMap["monthValue"] as Long).toInt(),
                            (dateMap["dayOfMonth"] as Long).toInt()
                        )

                        val timeMap = transaction.get("time") as Map<*, *>
                        val time = LocalTime.of(
                            (timeMap["hour"] as Long).toInt(),
                            (timeMap["minute"] as Long).toInt(),
                            (timeMap["second"] as Long).toInt()
                        )

                        transactionList.add(Transaction(amount, description, date, time, category, frequency, id))
                    }
                    continuation.resume(transactionList)

            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }


    suspend fun getIncomeTransactionFromFirebase() : List<Transaction> = suspendCoroutine { continuation ->

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

                    val newtransaction= Transaction(amount,description,date,time,category,frequency,id)
                    if(newtransaction.category.type== Category.Type.INCOME)
                        transactionList.add(newtransaction)
                }
                continuation.resume(transactionList)
            }

    }

    suspend fun getExpenseTransactionFromFirebase() : List<Transaction> = suspendCoroutine { continuation ->

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

                    val newtransaction= Transaction(amount,description,date,time,category,frequency,id)
                    if(newtransaction.category.type== Category.Type.EXPENSE)
                        transactionList.add(newtransaction)
                }
                continuation.resume(transactionList)
            }

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

    suspend fun getSavingsFromFirebase() : List<Savings>  = suspendCoroutine { continuation ->

        val savinglist = ArrayList<Savings>()
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("savings")
            .get()
            .addOnSuccessListener { result->
                for(saving in result)
                {
                    val amount = saving.getLong("amount")!!.toInt()
                    val closed = saving.getBoolean("closed")!!
                    val completed = saving.getBoolean("completed")!!
                    val description = saving.getString("description")!!
                    val failed = saving.getBoolean("failed")!!

                    val start = saving.getLong("start")!!.toInt()
                    val title = saving.getString("title")!!
                    val typestring = saving.getString("type")!!
                    val type = enumValueOf<SavingsType>(typestring)
                    val id = saving.getLong("id")!!

                    // DÁTUM mező
                    val startdateMap = saving.get("startDate") as Map<*, *>
                    val startyear = (startdateMap.get("year") as Long).toInt()
                    val startmonth = (startdateMap.get("monthValue") as Long).toInt()
                    val startday = (startdateMap.get("dayOfMonth") as Long).toInt()
                    val startDate = LocalDate.of(startyear,startmonth,startday)

                    // DÁTUM mező
                    val enddateMap = saving.get("endDate") as Map<*, *>
                    val endyear = (enddateMap.get("year") as Long).toInt()
                    val endmonth = (enddateMap.get("monthValue") as Long).toInt()
                    val endday = (enddateMap.get("dayOfMonth") as Long).toInt()
                    val endDate = LocalDate.of(endyear,endmonth,endday)

                    val newsaving= Savings(amount,startDate,endDate,type,title,description,start,id,completed,failed,closed)
                    savinglist.add(newsaving)

                }
                continuation.resume(savinglist)
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


    suspend fun getRepTransactionsFromFirebase() : List<RepetitiveTransaction>   = suspendCoroutine { continuation ->

        val reptransactionList = ArrayList<RepetitiveTransaction>()
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("repetitive_transactions")
            .get()
            .addOnSuccessListener { result->
                for(reptransaction in result)
                {
                    val transaction = reptransaction.get("transaction") as Map<*,*>
                    val amount = (transaction.get("amount") as Long).toInt()
                    val categoryString = transaction.get("category") as String
                    val category = enumValueOf<Category>(categoryString)
                    val description = transaction.get("description") as String
                    val frequencyString = transaction.get("frequency") as String
                    val frequency = enumValueOf<Frequency>(frequencyString)
                    val id = transaction.get("id") as Long

                    val dateMap = transaction.get("date") as Map<*, *>
                    val year = (dateMap.get("year") as Long).toInt()
                    val month = (dateMap.get("monthValue") as Long).toInt()
                    val day = (dateMap.get("dayOfMonth") as Long).toInt()
                    val date = LocalDate.of(year,month,day)

                    val timeMap = transaction.get("time") as Map<*, *>
                    val hour = (timeMap.get("hour") as Long).toInt()
                    val minute = (timeMap.get("minute") as Long).toInt()
                    val second = (timeMap.get("second") as Long).toInt()
                    val time = LocalTime.of(hour, minute, second)

                    // DÁTUM mező
                    val fromdateMap = reptransaction.get("fromDate") as Map<*, *>
                    val fromyear = (fromdateMap.get("year") as Long).toInt()
                    val frommonth = (fromdateMap.get("monthValue") as Long).toInt()
                    val fromday = (fromdateMap.get("dayOfMonth") as Long).toInt()
                    val fromdate = LocalDate.of(fromyear,frommonth,fromday)

                    val untildateMap = reptransaction.get("fromDate") as Map<*, *>
                    val untilyear = (untildateMap.get("year") as Long).toInt()
                    val untilmonth = (untildateMap.get("monthValue") as Long).toInt()
                    val untilday = (untildateMap.get("dayOfMonth") as Long).toInt()
                    val untildate = LocalDate.of(untilyear,untilmonth,untilday)



                    val newtransaction = Transaction(amount,description,date,time,category,frequency,id)
                    reptransactionList.add(RepetitiveTransaction(newtransaction,fromdate,untildate))
                }
                continuation.resume(reptransactionList)
            }

    }

    fun addRepetitiveTransactionToFirebase(repTransaction: RepetitiveTransaction)
    {
        val user = AuthService.getUserEmail()
        firestore
            .collection("users")
            .document(user)
            .collection("repetitive_transactions")
            .add(repTransaction)

    }

    suspend fun getAllStockFromFirebase() : List<Stock>  = suspendCoroutine { continuation ->

        val stocklist = ArrayList<Stock>()
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("stocks")
            .get()
            .addOnSuccessListener { result->
                for(stock in result)
                {

                    val companycode = stock.getString("companyCode")!!
                    val companyname = stock.getString("companyName")!!
                    val id = stock.getLong("id")!!
                    val price = stock.getDouble("price")!!.toFloat()
                    val stockamount = stock.getDouble("stockAmount")!!.toFloat()





                    val newstock = Stock(id,companyname,companycode,stockamount,price)
                    stocklist.add(newstock)
                }
                continuation.resume(stocklist)
            }
    }
    suspend fun getAllCompanyFromFirebase() : List<Company>  = suspendCoroutine { continuation ->

        val companylist = ArrayList<Company>()
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("companies")
            .get()
            .addOnSuccessListener { result->
                for(company in result)
                {

                    val companycode = company.getString("companyCode")!!
                    val companyname = company.getString("companyName")!!
                    val id = company.getLong("id")!!

                    val newcompany= Company(id, companyname,companycode)
                    companylist.add(newcompany)
                }
                continuation.resume(companylist)
            }
    }
    fun deleteStockFromFirebase(stock : Stock)
    {
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("stocks")
            .whereEqualTo("id",stock.id)
            .get()
            .addOnSuccessListener { result->
                result.documents.forEach{it.reference.delete()}

            }
            .addOnFailureListener { e ->
                Log.e("firebase", "Hiba történt törléskor", e)
            }

    }
    fun deleteCompanyFromFirebase(company : Company)
    {
        val user = AuthService.getUserEmail()
        firestore.collection("users")
            .document(user)
            .collection("stocks")
            .whereEqualTo("id",company.id)
            .get()
            .addOnSuccessListener { result->
                result.documents.forEach{it.reference.delete()}

            }
            .addOnFailureListener { e ->
                Log.e("firebase", "Hiba történt törléskor", e)
            }

    }
    fun addStockToFirebase(stock : Stock)
    {
        val user = AuthService.getUserEmail()
        firestore
            .collection("users")
            .document(user)
            .collection("stocks")
            .add(stock)

    }

    fun addCompanyToFirebase(company : Company)
    {
        val user = AuthService.getUserEmail()
        firestore
            .collection("users")
            .document(user)
            .collection("companies")
            .add(company)
    }



}