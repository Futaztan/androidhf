package com.androidhf.ui.screens.finance.everyXtime

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidhf.data.Data
import com.androidhf.data.Frequency
import com.androidhf.data.RepetitiveTransaction
import com.androidhf.data.Transaction
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

class DailyWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {


        Log.d("DailyWorker", "Munka fut: ${System.currentTimeMillis()}")
        for (repTransaction in Data.repetitiveTransactions.toList()) {
            val newTransaction = Transaction(
                repTransaction.transaction.amount,
                repTransaction.transaction.description,
                LocalDate.now(),
                LocalTime.now(),
                repTransaction.transaction.category,
                repTransaction.transaction.frequency
            )
            val differenceInDays = Duration.between(repTransaction.fromDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays()
            if(differenceInDays<0) continue
            when (repTransaction.transaction.frequency) {
                Frequency.NAPI -> {
                    Data.addTransaction(newTransaction)
                    isStillActive(1, repTransaction)
                }


                Frequency.HETI -> {

                    if ((differenceInDays % 7).toInt() == 0) {
                        Data.addTransaction(newTransaction)
                    }
                    isStillActive(7, repTransaction)
                }

                Frequency.HAVI -> {

                    if ((differenceInDays % 30).toInt() == 0) {
                        Data.addTransaction(newTransaction)
                    }
                    isStillActive(30, repTransaction)
                }

                else -> {
                    throw Exception("hogy kerultel ide")
                }
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun isStillActive(days: Long, repTransaction: RepetitiveTransaction) {
        if (LocalDate.now().plusDays(days).isAfter(repTransaction.untilDate)) {
            Data.repetitiveTransactions.remove(repTransaction)
        }
    }
}