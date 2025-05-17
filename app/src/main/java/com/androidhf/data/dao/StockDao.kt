package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidhf.data.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockEntity)

    @Update
    suspend fun updateStock(stock: StockEntity)

    @Delete
    suspend fun deleteStock(stock: StockEntity)

    @Query("SELECT * FROM Stocks WHERE id = :stockId")
    fun getStockById(stockId: Long): Flow<StockEntity?>

    @Query("SELECT * FROM Stocks ORDER BY companyName ASC")
    fun getAllStocks(): Flow<List<StockEntity>>

    @Query("SELECT * FROM Stocks WHERE companyCode = :companyCode ORDER BY id ASC")
    fun getStocksByCompanyCode(companyCode: String): Flow<List<StockEntity>>
}