package com.androidhf.data.datatypes

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.androidhf.data.enums.SavingsType
import java.time.LocalDate


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
    val Id : Long = 0,
    var Completed: Boolean = false,          //teljesített-e
    var Failed: Boolean = false,             //elbukott
    var Closed: Boolean =  false             //módosítható-e a Completed és Failed, ha closed true akkor nem
) {


    fun toEntity() : SavingsEntity
    {
        return SavingsEntity(
            Amount = this.Amount,
            StartDate = this.StartDate,
            EndDate = this.EndDate,
            Type = this.Type,
            Title = this.Title,
            Description = this.Description,
            Start = this.Start,
            Completed = this.Completed,
            Failed = this.Failed,
            Closed = this.Closed,
            id = this.Id
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
    var Completed: Boolean,
    var Failed: Boolean,
    var Closed: Boolean
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
            Start = this.Start,
            Completed = this.Completed,
            Failed = this.Failed,
            Closed = this.Closed,
            Id = this.id

        )
    }
}

