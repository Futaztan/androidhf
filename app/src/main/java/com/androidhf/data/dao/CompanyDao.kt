package com.androidhf.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidhf.data.datatypes.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompany(company: CompanyEntity)

    @Update
    suspend fun updateCompany(company: CompanyEntity)

    @Delete
    suspend fun deleteCompany(company: CompanyEntity)

    @Query("SELECT * FROM Companies WHERE id = :id")
    fun getCompanyById(id: Long): Flow<CompanyEntity>
    //ide lehet hogy kell kérdőjel companyentity mögé


    @Query("SELECT * FROM Companies")
    fun getAllCompanies(): Flow<List<CompanyEntity>>
}