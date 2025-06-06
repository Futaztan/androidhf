package com.androidhf.ui.screens.stock

import android.util.Log
import android.widget.Toast
import android.view.HapticFeedbackConstants

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment


import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController

import com.androidhf.R
import com.androidhf.data.datatypes.Company
import com.androidhf.data.datatypes.Stock

import com.androidhf.ui.reuseable.HeaderText
import com.androidhf.ui.reuseable.NumberTextField
import com.androidhf.ui.reuseable.Panel

import com.androidhf.ui.reuseable.UIVar

import com.androidhf.ui.screens.stock.query.stocksAggregatesBars
import io.polygon.kotlin.sdk.rest.AggregateDTO

import io.polygon.kotlin.sdk.rest.PolygonRestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import com.androidhf.ui.screens.stock.query.LineChartSample
import com.androidhf.ui.screens.stock.query.searchStocksREST
import com.androidhf.ui.screens.stock.uielements.FavoriteCompanyBox
import com.androidhf.ui.screens.stock.uielements.InvestmentBox
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun StockScreen(navController: NavController, stockViewModel: StockViewModel) {



    UIVar.topBarTitle = stringResource(id = R.string.stock_topbartitle)
    var showChart by remember { mutableStateOf(false) }

    val stockData = remember { mutableStateListOf<AggregateDTO>()  }
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingSearch by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val list = mutableListOf("Apple", "Google", "Tesla", "Amazon", "Microsoft", "Meta", "Netflix", "Nvidia", "Blackrock")
    val codes = mutableListOf("AAPL", "GOOGL", "TSLA", "AMZN", "MSFT", "META", "NFLX", "NVDA", "BLK")

    var searchResults by remember { mutableStateOf<List<Pair<String,String>>>(emptyList()) }

    val newlist = mutableListOf<String>()
    val newcodes = mutableListOf<String>()

    val displayedList = if(searchResults.isEmpty()) list else searchResults.map { it.first }
    val displayedCodes = if(searchResults.isEmpty()) codes else searchResults.map { it.second }

    val listState = rememberLazyListState()
    val haptic = LocalView.current
    val isScrolledToBottom by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val isLastItemFullyVisible = lastVisibleItem?.let {
                it.index == layoutInfo.totalItemsCount - 1 &&
                        it.offset + it.size <= layoutInfo.viewportEndOffset
            } ?: false

            val contentLargerThanViewport =
                layoutInfo.totalItemsCount > 0 &&
                        layoutInfo.visibleItemsInfo.size < layoutInfo.totalItemsCount

            isLastItemFullyVisible && contentLargerThanViewport
        }
    }
    if(isScrolledToBottom)      haptic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

    var currentCompanyCode by remember { mutableStateOf("") }
    var currentCompanyName by remember { mutableStateOf("") }

    var showBottomWindow = remember { mutableStateOf(false) }


    val companies by stockViewModel.company.collectAsState()
    val stocks by stockViewModel.stock.collectAsState()


    LaunchedEffect(searchQuery) {
        if(searchQuery.isEmpty()){
            searchResults = emptyList()
        }
    }


    fun stockQuery(company: String) {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val POLYGON_API_KEY = PolygonRestClient("kwQO2EZ6YFWcSA0Vkx4pCXyE6Guf2HJg")

                val now = LocalDate.now()
                var endDate = now
                var startDate = now.minusDays(8)

                var attempts = 0
                var foundData = false

                while (!foundData && attempts < 40) {
                    Log.d("StockQuery", "Attempt ${attempts + 1}: Querying from ${startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))} to ${endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")

                    try {
                        val data = stocksAggregatesBars(
                            POLYGON_API_KEY,
                            company,
                            startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )

                        withContext(Dispatchers.Main) {
                            if (data.results != null && data.results.isNotEmpty()) {
                                Log.d("StockQuery", "Data found for ${company}! ${data.results.size} data points.")
                                stockData.clear()
                                stockData.addAll(data.results)
                                showChart = true
                                currentCompanyCode = data.ticker?.toString() ?: company
                                currentCompanyName = currentCompanyCode
                                foundData = true
                            } else {
                                Log.e("StockQuery", "No data: trying attempt " + attempts)
                                attempts++
                                delay(5000)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("StockQuery", "Error fetching data: ${e.message}")
                        attempts++
                        delay(3000)
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    val intensity =
    if(showBottomWindow.value)
    {
        8.dp
    }
    else{
        0.dp
    }


    Box(modifier = Modifier.fillMaxSize().blur(intensity).padding(UIVar.Padding)){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 104.dp)
        ) {
            item {
                HeaderText(stringResource(id = R.string.stock_currentinvestments),modifier = Modifier.padding(start = UIVar.Padding))
                Spacer(modifier = Modifier.height(UIVar.Padding))
            }

            if (stocks.isEmpty()) {
                item {
                    Text(
                        stringResource(id = R.string.stock_noinvestments),
                        modifier = Modifier.padding(start = UIVar.Padding, bottom = UIVar.Padding)
                    )
                }
            } else {

                items(stocks.chunked(2)) { stockRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UIVar.Padding / 2)
                    ) {

                        val stock1 = stockRow[0]

                        val company1 = companies.find { it.companyCode == stock1.companyCode }
                            ?: Company(companyName = stock1.companyName, companyCode = stock1.companyCode)

                        InvestmentBox(
                            stock = stock1,
                            company = company1,
                            amount = stock1.stockAmount.toInt(),
                            buyprice = stock1.price,
                            onDelete = { stockViewModel.deleteStock(stock1) },
                            onClick = {
                                stockQuery(stock1.companyCode)
                                currentCompanyName = stock1.companyName;
                                currentCompanyCode = stock1.companyCode;
                                showBottomWindow.value = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = UIVar.Padding / 2)
                        )



                        if (stockRow.size > 1) {
                            val stock2 = stockRow[1]

                            val company2 = companies.find { it.companyCode == stock2.companyCode }
                                ?: Company(companyName = stock2.companyName, companyCode = stock2.companyCode)

                            InvestmentBox(
                                stock = stock2,
                                company = company2,
                                amount = stock2.stockAmount.toInt(),
                                buyprice = stock2.price,
                                onDelete = { stockViewModel.deleteStock(stock2) },
                                onClick = {
                                    stockQuery(stock2.companyCode)
                                    currentCompanyName = stock2.companyName;
                                    currentCompanyCode = stock2.companyCode;
                                    showBottomWindow.value = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = UIVar.Padding / 2)
                            )
                        } else {

                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(UIVar.Padding))

                }
            }


            item {
                HeaderText(stringResource(id = R.string.stock_favorites),modifier = Modifier.padding(start = UIVar.Padding))
                Spacer(modifier = Modifier.height(UIVar.Padding))
            }

            if(companies.isEmpty()){
                item{
                    Text(stringResource(id = R.string.stock_nocompanies),
                        modifier = Modifier.padding(start = UIVar.Padding, bottom = UIVar.Padding)
                    )

                }
            }

            items(companies.chunked(2)) { companyRow ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UIVar.Padding / 2)
                ) {

                    FavoriteCompanyBox(
                        company = companyRow[0],
                        onDelete = { stockViewModel.deleteCompany(companyRow[0]) },
                        onClick = {
                            stockQuery(companyRow[0].companyCode)
                            currentCompanyName = companyRow[0].companyName;
                            currentCompanyCode = companyRow[0].companyCode;
                            showBottomWindow.value = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = UIVar.Padding / 2)
                    )


                    if (companyRow.size > 1) {
                        FavoriteCompanyBox(
                            company = companyRow[1],
                            onDelete = { stockViewModel.deleteCompany(companyRow[1]) },
                            onClick = {
                                stockQuery(companyRow[1].companyCode)
                                currentCompanyName = companyRow[0].companyName;
                                currentCompanyCode = companyRow[0].companyCode;
                                showBottomWindow.value = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = UIVar.Padding / 2)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(UIVar.Padding))
            }
        }

        //alsó kereső
        Box(modifier = Modifier.align(Alignment.BottomCenter)){
            Column(modifier = Modifier.fillMaxWidth()){
                LazyRow(state = listState) {
                    itemsIndexed(displayedList, key = { index, _ -> UUID.randomUUID().toString() }) { index, item ->
                        if (index == 0) {
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                        }

                        Button(onClick = {
                            stockData.clear()

                            stockQuery(displayedCodes[index])

                            showBottomWindow.value = true
                        }, enabled = /*!isLoading*/ true) {
                            Text(item)
                        }

                        Spacer(modifier = Modifier.width(UIVar.Padding))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()){

                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(stringResource(id = R.string.stock_search1))},
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(5f)
                    )
                    Spacer(modifier = Modifier.width(UIVar.Padding) )
                    Button(

                        onClick = {
                            isLoadingSearch = true;
                            searchStocksREST("kwQO2EZ6YFWcSA0Vkx4pCXyE6Guf2HJg",searchQuery) { results ->
                                CoroutineScope(Dispatchers.Main).launch {
                                    isLoadingSearch = false;
                                    Log.e("search","search started with ${searchQuery}")
                                    if(results.isNotEmpty())
                                    {
                                        searchResults = results
                                    }else{
                                        searchResults = emptyList()
                                    }
                                }
                            }

                        },
                        enabled = searchQuery.length >= 2 /* && !isLoading*/,
                        modifier = Modifier
                            .weight(2f)
                            .align(Alignment.CenterVertically)
                    ){
                        Text(stringResource(id = R.string.stock_search2))
                    }

                }
            }
        }
    }
    if(showBottomWindow.value) bottomwindow(
        showBottom = showBottomWindow,
        stockData = stockData,
        companyName = currentCompanyName,
        isLoading = isLoading,
        currentCompanyCode = currentCompanyCode,
        stockViewModel
    )







    if (showChart && stockData != null) {
        stockViewModel.setData(stockData)
    }
}








@Composable
fun bottomwindow(
    showBottom: MutableState<Boolean>,
    stockData: List<AggregateDTO>,
    companyName: String,
    isLoading: Boolean,
    currentCompanyCode: String,
    stockViewModel: StockViewModel
) {
    var stockInput by remember { mutableStateOf("") }
    val companies by stockViewModel.company.collectAsState()
    val isFavorite = companies.any { it.companyCode == currentCompanyCode }
    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures {} }
    ) {
        Panel(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(UIVar.Radius))
            , centerItems = false) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                Row {
                    if(stockData.isEmpty() && !isLoading){
                        Text(stringResource(id = R.string.stock_tryagainlater),color = Color.Red)
                    }
                    if(!isLoading && stockData.isNotEmpty()){
                        Text("%.2f USD".format(stockData.last().close ?: 0.0)) //TODO: ÁT KELL VÁLTANI FORINTRA
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                        val prev = stockData.first().close;
                        val now = stockData.last().close;
                        val perc = 100- (prev?.div(now!!))?.times(100)!!

                        Text("%.2f%%".format(perc)) //TODO: GYURI szín
                    }

                }
            }
            Column {
                HeaderText(if(companyName == "null") stringResource(id = R.string.stock_nodata) else companyName)

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(id = R.string.stock_holdon))
                        }


                    }
                } else if (stockData.isNotEmpty()) {
                    LineChartSample(stockData, companyName + stringResource(id = R.string.stock_stock))
                    NumberTextField(
                        input = stockInput,
                        onInputChange = { stockInput = it },
                        placeholder = stringResource(id = R.string.stock_stockamount)
                    )
                }



                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { showBottom.value = false }, modifier = Modifier
                        .weight(3f)
                        .align(Alignment.CenterVertically)) { Text(stringResource(id = R.string.general_cancel)) }
                    if(stockData.isNotEmpty()){
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                        val context1 = LocalContext.current
                        Button(onClick = {
                            if(stockInput.toFloatOrNull() != null){
                                Toast.makeText(context1, context1.getString(R.string.stock_mark), Toast.LENGTH_SHORT).show()
                                stockViewModel.addStock(Stock(companyName = companyName, companyCode = currentCompanyCode, stockAmount = stockInput.toFloat(), price = stockData.last().close?.toFloat()
                                    ?: -1f))
                            }

                        }, modifier = Modifier
                            .weight(3f)
                            .align(Alignment.CenterVertically),
                            enabled = !isLoading && stockInput.isNotEmpty()
                        ) {
                            Text(stringResource(id = R.string.stock_mark))
                        }
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                        val context = LocalContext.current
                        IconButton(onClick = {
                            if (isFavorite) {
                                val companyToDelete = companies.find { it.companyCode == currentCompanyCode }
                                if (companyToDelete != null) {
                                    stockViewModel.deleteCompany(companyToDelete)
                                    Toast.makeText(context, context.getString(R.string.stock_favoritesremoved), Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                stockViewModel.addCompany(Company(companyName = companyName, companyCode = currentCompanyCode))
                                Toast.makeText(context, context.getString(R.string.stock_favoritesadded), Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(
                                 painter = painterResource(id = if(isFavorite) R.drawable.ic_fav_48 else R.drawable.ic_notfav_48),
                                contentDescription = "Not favourite icon",
                                modifier = Modifier.weight(1f)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}

