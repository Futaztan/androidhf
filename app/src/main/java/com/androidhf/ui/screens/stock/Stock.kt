package com.androidhf.ui.screens.stock

import android.util.Log

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController

import com.androidhf.R
import com.androidhf.data.Company
import com.androidhf.data.Stock

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
fun StockScreen(navController: NavController) {

    val stockViewModel: StockViewModel = hiltViewModel()

    UIVar.topBarTitle = "Stock"
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
/*
    val valamilist = remember ( newlist ) {
        if(newlist.isEmpty())
        {
            list
        }else{
            newlist
        }
    }

    val valamicodes = remember ( newcodes ) {
        if(newcodes.isEmpty())
        {
            codes
        }else{
            newcodes
        }
    }
*/
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

                // Kezdeti paraméterek
                val now = LocalDate.now()
                var endDate = now
                var startDate = now.minusDays(8) // Kezdjük 20 nappal ezelőttről

                var attempts = 0
                var foundData = false

                // Maximum 3 próbálkozás (60 napra visszamenőleg)
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
                            // Ellenőrizzük, hogy kaptunk-e értelmes adatot
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
                                // Ha nem találtunk adatot, megnöveljük az időtartamot
                                attempts++
                                delay(5000)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("StockQuery", "Error fetching data: ${e.message}")
                        attempts++
                        delay(3000)
                        // Kivétel esetén szintén növeljük az időtartamot
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
/*
    if(searchQuery == ""){
        newlist.clear()
        newcodes.clear()
    }
*/

    Box(modifier = Modifier.fillMaxSize().blur(intensity).padding(UIVar.Padding)){

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 106.dp) // Helyet hagyunk az alsó kereső sávnak
        ) {
            // Fejléc
            item {
                HeaderText("Jelenlegi befektetések:")
                Spacer(modifier = Modifier.height(UIVar.Padding))
            }

            if (stocks.isEmpty()) {
                item {
                    Text(
                        "Még nincs befektetés",
                        modifier = Modifier.padding(start = UIVar.Padding, bottom = UIVar.Padding)
                    )
                }
            } else {
                // Befektetéseket 2-es csoportokba rendezzük, mint a kedvenceknél
                items(stocks.chunked(2)) { stockRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UIVar.Padding / 2)
                    ) {
                        // Első befektetés a sorban (mindig létezik)
                        val stock1 = stockRow[0]
                        // Megkeressük a hozzá tartozó céget
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
                                showBottomWindow.value = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = UIVar.Padding / 2)
                        )

                        // Második befektetés a sorban (ha létezik)
                        if (stockRow.size > 1) {
                            val stock2 = stockRow[1]
                            // Megkeressük a második cég adatait is
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
                                    showBottomWindow.value = true
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = UIVar.Padding / 2)
                            )
                        } else {
                            // Ha nincs második elem, akkor üres helyet hagyunk
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }
            }

            // Kedvencek fejléc
            item {
                HeaderText("Kedvencek:")
                Spacer(modifier = Modifier.height(UIVar.Padding))
            }

            if(companies.isEmpty()){
                item{
                    Text("Még nincs kedvenc cég")
                }
            }
            // Kedvencek listája
            items(companies.chunked(2)) { companyRow ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UIVar.Padding / 2)
                ) {
                    // Első elem a sorban (mindig létezik)
                    FavoriteCompanyBox(
                        company = companyRow[0],
                        onDelete = { stockViewModel.deleteCompany(companyRow[0]) },
                        onClick = {
                            stockQuery(companyRow[0].companyCode)
                            showBottomWindow.value = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = UIVar.Padding / 2)
                    )

                    // Második elem a sorban (ha létezik)
                    if (companyRow.size > 1) {
                        FavoriteCompanyBox(
                            company = companyRow[1],
                            onDelete = { stockViewModel.deleteCompany(companyRow[1]) },
                            onClick = {
                                stockQuery(companyRow[1].companyCode)
                                showBottomWindow.value = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = UIVar.Padding / 2)
                        )
                    } else {
                        // Ha nincs második elem, akkor üres helyet hagyunk
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(UIVar.Padding))
            }

            // Extra tér az alján, hogy a görgethető tartalom alján is legyen hely
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        //alsó kereső
        Box(modifier = Modifier.align(Alignment.BottomCenter)){
            Column(modifier = Modifier.fillMaxWidth()){
                LazyRow {
                    itemsIndexed(displayedList, key = { index, _ -> UUID.randomUUID().toString() }) { index, item ->
                        if (index == 0) {
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                        }

                        Button(onClick = {
                            // First clear previous data
                            stockData.clear()

                            // Query the stock data for the selected company
                            stockQuery(displayedCodes[index])

                            // Show the bottom window
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
                        placeholder = { Text("Keresés...")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(6f)
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
                        enabled = searchQuery.length >= 2 && !isLoading,
                        modifier = Modifier
                            .weight(2f)
                            .align(Alignment.CenterVertically)
                    ){
                        Text("Keresés")
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
        currentCompanyCode = currentCompanyCode
    )







    if (showChart && stockData != null) {
        stockViewModel.setData(stockData)
        //navController.navigate("stock_detail")
    }
}








@Composable
fun bottomwindow(
    showBottom: MutableState<Boolean>,
    stockData: List<AggregateDTO>,
    companyName: String,
    isLoading: Boolean,
    currentCompanyCode: String
) {
    var stockInput by remember { mutableStateOf("") }
    val stockViewModel: StockViewModel = hiltViewModel()
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
                        Text("Próbáld újra később!",color = Color.Red)
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
                HeaderText(if(companyName == "null") "Nincs adat" else companyName)

                // Show loading or chart
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
                            Text("Hold on...")
                        }


                    }
                } else if (stockData.isNotEmpty()) {
                    LineChartSample(stockData, companyName + " stock")
                    NumberTextField(
                        input = stockInput,
                        onInputChange = { stockInput = it },
                        placeholder = "Stock amount"
                    )
                }



                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { showBottom.value = false }, modifier = Modifier
                        .weight(3f)
                        .align(Alignment.CenterVertically)) { Text("Mégse") }
                    if(stockData.isNotEmpty()){
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                        Button(onClick = {
                            if(stockInput.toFloatOrNull() != null){
                                stockViewModel.addStock(Stock(companyName = companyName, companyCode = currentCompanyCode, stockAmount = stockInput.toFloat(), price = stockData.last().close?.toFloat()
                                    ?: -1f))
                            }

                        }, modifier = Modifier
                            .weight(3f)
                            .align(Alignment.CenterVertically),
                            enabled = !isLoading ) { // Disable during loading
                            Text("Megjelölés")
                        }
                        Spacer(modifier = Modifier.width(UIVar.Padding))
                        IconButton(onClick = {
                            stockViewModel.addCompany(Company(companyName = companyName, companyCode = currentCompanyCode))
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notfav_48),
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

