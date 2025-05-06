package com.androidhf.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date
import java.util.UUID


//ez a class kezeli a megtakarításokat meg ilyeneket
data class Savings(
    val Amount: Int,               //a megtakarítási cél
    val StartDate: LocalDate, //mikor hoztuk létre
    val EndDate: LocalDate,  //mikorra szeretnénk elérni
    val Type: SavingsType,    //milyen típusú megtakarítást szeretnénk
                             // incomegoal_bytime, incomegoal_byamount, expensegoal_bytime, expensegoal_byamount
    val Title: String,   //a megtakarítás neve
    val Description: String,  //rövid leírása, hogy mit szerettünk volna elérni
    var Start: Int,  //kezdő pénz mennyisége
    val id : Long = 0
) {

//    val id: String =
//        UUID.randomUUID().toString() //TODO: ez lehet hogy nem működik ha elmentjük majd




    var Completed: Boolean = false          //teljesített-e
    var Failed: Boolean = false             //elbukott
    var Closed: Boolean =  false             //módosítható-e a Completed és Failed, ha closed true akkor nem

    fun toEntity() : SavingsEntity
    {
        return SavingsEntity(
            Amount = this.Amount,
            id = this.id,
            StartDate = this.StartDate,
            EndDate = this.EndDate,
            Type = this.Type,
            Title = this.Title,
            Description = this.Description,
            Start = this.Start,
            Completed = this.Completed,
            Failed = this.Failed,
            Closed = this.Closed,
        )
    }

}
@Entity(tableName = "savings")
data class SavingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val Amount: Int,
    val StartDate: LocalDate,
    val EndDate: LocalDate,
    val Type: SavingsType,
    val Title: String,
    val Description: String,
    var Start: Int,
    var Completed: Boolean = false,
    var Failed: Boolean = false,
    var Closed: Boolean =  false
)
{
    fun toDomain() : Savings
    {
        return Savings(
            Amount = this.Amount,
            StartDate = this.StartDate,
            EndDate = this.EndDate,
            Type = this.Type,
            Title = this.Title,
            Description = this.Description,
            Start = this.Start
        )
    }
}

