package com.androidhf.ui.screens.finance.everyXtime

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.androidhf.data.Data
import com.androidhf.data.Frequency
import com.androidhf.data.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class DailyWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {


        Log.d("DailyWorker", "Munka fut: ${System.currentTimeMillis()}")
        for (transaction in Data.repetitiveTransactions)
        {
            val newTransaction = Transaction(transaction.amount,transaction.description, LocalDate.now(),
                LocalTime.now(),transaction.category,transaction.frequency)
            when(transaction.frequency)
            {
                Frequency.NAPI-> CoroutineScope(Dispatchers.IO).launch { Data.addTransaction(newTransaction) }
                Frequency.HETI -> {
                    val differenceInDays = Duration.between(transaction.date.atStartOfDay(),LocalDate.now().atStartOfDay()).toDays()
                    if((differenceInDays%7).toInt() ==0)
                    {
                        CoroutineScope(Dispatchers.IO).launch { Data.addTransaction(newTransaction) }
                    }
                }
                Frequency.HAVI -> {
                    val differenceInDays = Duration.between(transaction.date.atStartOfDay(),LocalDate.now().atStartOfDay()).toDays()
                    if((differenceInDays%30).toInt() ==0)
                    {
                        CoroutineScope(Dispatchers.IO).launch { Data.addTransaction(newTransaction) }
                    }
                }
                else -> {throw Exception("hogy kerultel ide")}
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}