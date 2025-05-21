package com.androidhf.data.repository

import com.androidhf.data.datatypes.Company
import com.androidhf.data.datatypes.Stock
import com.androidhf.data.dao.CompanyDao
import com.androidhf.data.dao.StockDao
import com.androidhf.data.database.FirebaseDB
import com.androidhf.ui.screens.login.auth.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val stockDao: StockDao,
    private val companyDao: CompanyDao,
    private val firebaseDB: FirebaseDB
){
    suspend fun getAllStocks() : Flow<List<Stock>> {
        if(AuthService.isLoggedIn())
        {
            return flowOf(firebaseDB.getAllStockFromFirebase())
        }
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
        if(AuthService.isLoggedIn())
            firebaseDB.deleteStockFromFirebase(stock)
        stockDao.deleteStock(stock.toEntity())
    }

    suspend fun updateStock(stock: Stock) {
        stockDao.updateStock(stock.toEntity())
    }

    suspend fun addStock(stock: Stock) {
        if(AuthService.isLoggedIn())
            firebaseDB.addStockToFirebase(stock)
        stockDao.insertStock(stock.toEntity())
    }

    suspend fun getAllCompanies() : Flow<List<Company>> {
        if(AuthService.isLoggedIn())
        {
            return flowOf(firebaseDB.getAllCompanyFromFirebase())
        }
        return companyDao.getAllCompanies().map { companyEntities ->
            companyEntities.map { it.toDomain() }
        }
    }
    fun getCompanyById(companyId: Long) : Flow<Company> {
        return companyDao.getCompanyById(companyId).map { it.toDomain() }
        //ide lehet hogy kell kérdőjel
    }
    suspend fun addCompany(company: Company) {
        if(AuthService.isLoggedIn())
            firebaseDB.addCompanyToFirebase(company)
        companyDao.insertCompany(company.toEntity())
    }
    suspend fun updateCompany(company: Company) {
        companyDao.updateCompany(company.toEntity())
    }
    suspend fun deleteCompany(company: Company) {
        if(AuthService.isLoggedIn())
            firebaseDB.deleteCompanyFromFirebase(company)
        companyDao.deleteCompany(company.toEntity())
    }

}