package com.androidhf.data.datatypes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

data class RepetitiveTransaction(
    val transaction: Transaction,
    val fromDate: LocalDate,
    val untilDate: LocalDate)
{
    fun toEntity(): RepetitiveTransactionEntity {



        return RepetitiveTransactionEntity(
            id = this.transaction.id,
            amount = this.transaction.amount,
            description = this.transaction.description,
            date = this.transaction.date,
            time = this.transaction.time,
            category = this.transaction.category,
            type = this.transaction.category.type,
            frequency = this.transaction.frequency,
            fromDate = this.fromDate,
            untilDate = this.untilDate

            )
    }

}

@Entity(tableName = "repetitivetransactions")
data class RepetitiveTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val description: String,
    val date: LocalDate,
    val time : LocalTime,
    val category: Category,
    val type : Category.Type,
    val frequency: Frequency,
    val fromDate: LocalDate,
    val untilDate: LocalDate

    ){
    fun toDomain() : RepetitiveTransaction
    {
        return RepetitiveTransaction(
            Transaction(
            id = this.id,
            amount = this.amount,
            description = this.description,
            date = this.date,
            time = this.time,
            category = this.category,
            frequency = this.frequency),
            fromDate = this.fromDate,
            untilDate = this.fromDate

            )
    }
}




