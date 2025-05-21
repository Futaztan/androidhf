package com.androidhf.ui.screens.finance.dailycheck


import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androidhf.data.enums.Frequency
import com.androidhf.data.datatypes.RepetitiveTransaction
import com.androidhf.data.datatypes.Transaction
import com.androidhf.data.repository.RepetitiveTransactionRepository
import com.androidhf.data.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@HiltWorker
class DailyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repTransactionRepo: RepetitiveTransactionRepository,
    private val transactionRepo: TransactionRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {


        Log.d("DailyWorker", "Munka fut: ${System.currentTimeMillis()}")

        for (repTransaction in repTransactionRepo.getAllRepetitiveTransactions().first()) {
            val newTransaction = Transaction(
                repTransaction.transaction.amount,
                repTransaction.transaction.description,
                LocalDate.now(),
                LocalTime.now(),
                repTransaction.transaction.category,
                repTransaction.transaction.frequency
            )
            val differenceInDays = Duration.between(
                repTransaction.fromDate.atStartOfDay(),
                LocalDate.now().atStartOfDay()
            ).toDays()
            if (differenceInDays < 0) continue
            when (repTransaction.transaction.frequency) {
                Frequency.NAPI -> {
                    transactionRepo.addTransaction(newTransaction)
                    isStillActive(1, repTransaction,repTransactionRepo)
                }


                Frequency.HETI -> {

                    if ((differenceInDays % 7).toInt() == 0) {
                        transactionRepo.addTransaction(newTransaction)
                    }
                    isStillActive(7, repTransaction,repTransactionRepo)
                }

                Frequency.HAVI -> {

                    if ((differenceInDays % 30).toInt() == 0) {
                        transactionRepo.addTransaction(newTransaction)
                    }
                    isStillActive(30, repTransaction,repTransactionRepo)
                }

                else -> {
                    throw Exception("hogy kerultel ide")
                }
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private suspend fun isStillActive(days: Long, repTransaction: RepetitiveTransaction, repTransactionRepo : RepetitiveTransactionRepository) {
        if (LocalDate.now().plusDays(days).isAfter(repTransaction.untilDate)) {

            repTransactionRepo.deleteRepetitiveTransaction(repTransaction)

        }
    }
}
