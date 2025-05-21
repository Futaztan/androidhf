package com.androidhf.ui.screens.stock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidhf.data.Company
import com.androidhf.data.Stock
import com.androidhf.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.polygon.kotlin.sdk.rest.AggregateDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//viewmodel hogy át lehessen vinni adatot screen között
@HiltViewModel
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository

) : ViewModel() {
     var stockData by mutableStateOf<List<AggregateDTO>?>(null)
         private set


    fun setData(data: List<AggregateDTO>) {
        stockData = data
    }

    private val _stock = MutableStateFlow<List<Stock>>(emptyList())
    val stock : StateFlow<List<Stock>> = _stock

    private val _company = MutableStateFlow<List<Company>>(emptyList())
    val company : StateFlow<List<Company>> = _company

    init {
        loadStock()
        loadCompany()
    }

    private fun loadStock(){
        viewModelScope.launch {
            stockRepository.getAllStocks().collect{ item: List<Stock> ->
                _stock.value = item
            }
        }
    }

    private fun loadCompany(){
        viewModelScope.launch {
            stockRepository.getAllCompanies().collect{ item: List<Company> ->
                _company.value = item
            }
        }
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.addStock(stock)
        }
    }
    fun deleteStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.deleteStock(stock)
        }
    }
    fun updateStock(stock: Stock) {
        viewModelScope.launch {
            stockRepository.updateStock(stock)
        }
    }
    fun addCompany(company: Company) {
        viewModelScope.launch {
            stockRepository.addCompany(company)
        }
    }
    fun deleteCompany(company: Company) {
        viewModelScope.launch {
            stockRepository.deleteCompany(company)
        }
    }
    fun updateCompany(company: Company) {
        viewModelScope.launch {
            stockRepository.updateCompany(company)
        }
    }

}