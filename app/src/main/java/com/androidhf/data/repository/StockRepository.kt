package com.androidhf.data.repository

import com.androidhf.data.Company
import com.androidhf.data.Stock
import com.androidhf.data.dao.CompanyDao
import com.androidhf.data.dao.StockDao
import com.androidhf.data.database.FirebaseDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val stockDao: StockDao,
    private val companyDao: CompanyDao,
    private val firebaseDB: FirebaseDB
){
    fun getAllStocks() : Flow<List<Stock>> {
        return stockDao.getAllStocks().map { stockEntities ->
            stockEntities.map { it.toDomain() }
        }
    }
    fun getStocksByCompanyCode(companyCode: String) : Flow<List<Stock>> {
        return stockDao.getStocksByCompanyCode(companyCode).map { stockEntities ->
            stockEntities.map { it.toDomain() }
        }
    }

    suspend fun deleteStock(stock: Stock) {
        stockDao.deleteStock(stock.toEntity())
    }

    suspend fun updateStock(stock: Stock) {
        stockDao.updateStock(stock.toEntity())
    }

    suspend fun addStock(stock: Stock) {
        stockDao.insertStock(stock.toEntity())
    }

    fun getAllCompanies() : Flow<List<Company>> {
        return companyDao.getAllCompanies().map { companyEntities ->
            companyEntities.map { it.toDomain() }
        }
    }
    fun getCompanyById(companyId: Long) : Flow<Company> {
        return companyDao.getCompanyById(companyId).map { it.toDomain() }
        //ide lehet hogy kell kérdőjel
    }
    suspend fun addCompany(company: Company) {
        companyDao.insertCompany(company.toEntity())
    }
    suspend fun updateCompany(company: Company) {
        companyDao.updateCompany(company.toEntity())
    }
    suspend fun deleteCompany(company: Company) {
        companyDao.deleteCompany(company.toEntity())
    }

}