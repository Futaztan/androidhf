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
            val firestorelist = firebaseDB.getAllStockFromFirebase()
            for(i in firestorelist.indices)
            {
                stockDao.insertStock(firestorelist.get(i).toEntity())
            }
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
        val id = stockDao.insertStock(stock.toEntity())
        val withId = stock.copy(id=id)
        if(AuthService.isLoggedIn())
            firebaseDB.addStockToFirebase(withId)
    }

    suspend fun getAllCompanies() : Flow<List<Company>> {
        if(AuthService.isLoggedIn())
        {
            val firestorelist = firebaseDB.getAllCompanyFromFirebase()
            for(i in firestorelist.indices)
            {
                companyDao.insertCompany(firestorelist.get(i).toEntity())
            }
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
        val id = companyDao.insertCompany(company.toEntity())
        val withId = company.copy(id=id)
        if(AuthService.isLoggedIn())
            firebaseDB.addCompanyToFirebase(withId)

    }
    suspend fun updateCompany(company: Company) {
        companyDao.updateCompany(company.toEntity())
    }
    suspend fun deleteCompany(company: Company) {
        if(AuthService.isLoggedIn())
            firebaseDB.deleteCompanyFromFirebase(company)
        companyDao.deleteCompany(company.toEntity())
    }
    suspend fun deleteAllStock()
    {
        stockDao.clearTable()
    }
    suspend fun deleteAllCompany()
    {
        companyDao.clearTable()
    }

}