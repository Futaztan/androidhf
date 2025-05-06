package com.androidhf.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime


//bevétel vagy kiadások
//mutable ha editelni akarjuk valamelyik tranzaikciot akkor változzonm az ui


 class Transaction(
   _amount : Int,
   _description : String,            //tranzakcio rövid leirasa a usertol
   _date : LocalDate,
   _time: LocalTime,
   _category : Category,            //Category-n belüli típus
    _frequency : Frequency         //milyen gyakran van ez a tranzakcio

)
{


    var id : Int = 0
    var amount by mutableStateOf(_amount)
    var description by mutableStateOf(_description)
    var date by mutableStateOf(_date)
    var time by mutableStateOf(_time)
    var category by mutableStateOf(_category)
    var frequency by mutableStateOf(_frequency)





    fun toEntity(): TransactionEntity {

        val tmp = TransactionEntity(
            id = this.id,
            amount = this.amount,
            description = this.description,
            date = this.date,
            time = this.time,
            category = this.category,
            type = this.category.type,
            frequency = this.frequency
        )
        return tmp
    }


}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Int,
    val description: String,
    val date: LocalDate,
    val time : LocalTime,
    val category: Category,
    val type : Category.Type,
    val frequency: Frequency
){
    fun toDomain() : Transaction
    {
        return Transaction(
            _amount = this.amount,
            _description = this.description,
            _date = this.date,
            _time = this.time,
            _category = this.category,
            _frequency = this.frequency
        )
    }
}
