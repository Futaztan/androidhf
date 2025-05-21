package com.androidhf.data.datatypes

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Stock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val companyName: String,
    val companyCode: String,
    val stockAmount: Float,
    val price: Float
){
    fun toEntity(): StockEntity {
        return StockEntity(
            id = this.id,
            companyName = this.companyName,
            companyCode = this.companyCode,
            stockAmount = this.stockAmount,
            price = this.price
        )
    }
}
@Entity(tableName = "Stocks")
data class StockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyName: String,
    val companyCode: String,
    val stockAmount: Float,
    val price: Float
){
    fun toDomain() : Stock
    {
        return Stock(
            id = this.id,
            companyName = this.companyName,
            companyCode = this.companyCode,
            stockAmount = this.stockAmount,
            price = this.price
        )
    }
}