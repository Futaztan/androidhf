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

 data class Transaction(

     val amount : Int,
     val description : String,            //tranzakcio rövid leirasa a usertol
     val date : LocalDate,
     val time: LocalTime,
     val category : Category,            //Category-n belüli típus
     val frequency : Frequency,         //milyen gyakran van ez a tranzakcio
     var isRepetitive : Boolean = false,   /*ez jelzi h eredeti ismetlodo tranzakcio, ezek vannak a repetitive
                                          listába, room db miatt kell foleg   */
     val id : Long =0                    //nem hasznaljuk, csak db-hez kell

)
{


//    private var id : Int = 0
//    var isRepetitive = _isRepetitive
//    var amount by mutableStateOf(_amount)
//    var description by mutableStateOf(_description)
//    var date by mutableStateOf(_date)
//    var time by mutableStateOf(_time)
//    var category by mutableStateOf(_category)
//    var frequency by mutableStateOf(_frequency)





    fun toEntity(): TransactionEntity {


        
        return TransactionEntity(
            id = this.id,
            amount = this.amount,
            description = this.description,
            date = this.date,
            time = this.time,
            category = this.category,
            type = this.category.type,
            frequency = this.frequency,
            isRepetitive = this.isRepetitive
        )
    }


}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Int,
    val description: String,
    val date: LocalDate,
    val time : LocalTime,
    val category: Category,
    val type : Category.Type,
    val frequency: Frequency,
    val isRepetitive : Boolean
){
    fun toDomain() : Transaction
    {
        return Transaction(
            id = this.id,
            amount = this.amount,
            description = this.description,
            date = this.date,
            time = this.time,
            category = this.category,
            frequency = this.frequency,
            isRepetitive = this.isRepetitive
        )
    }
}

