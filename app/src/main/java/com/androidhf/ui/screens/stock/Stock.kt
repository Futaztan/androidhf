package com.androidhf.ui.screens.stock


import android.icu.text.CaseMap.Title
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.BottomSheetScaffold

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.androidhf.R
import com.androidhf.data.Data
import com.androidhf.ui.reuseable.HeaderText
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StockScreen(navController: NavController, stockViewModel: StockViewModel) {
    Data.topBarTitle = "Stock"
    var showChart by remember { mutableStateOf(false) }

    val stockData = remember { mutableStateListOf<AggregateDTO>()  }
    var isLoading by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    val list = mutableListOf("Apple", "Google", "Tesla", "Amazon", "Microsoft", "Meta", "Netflix", "Nvidia", "Blackrock")
    val codes = mutableListOf("AAPL", "GOOGL", "TSLA", "AMZN", "MSFT", "META", "NFLX", "NVDA", "BLK")

    var showBottomWindow = remember { mutableStateOf(false) }

    fun stockQuery(company : String)
    {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch { //coroutint indit el a lekérdezéshez
            try {
                val POLYGON_API_KEY = PolygonRestClient("j69KmsF2J_JJn1KWl2f_1drc6HT9Cech")
                val data = stocksAggregatesBars(POLYGON_API_KEY,company, "2025-03-03", "2025-03-07")
                withContext(Dispatchers.Main) {
                    stockData.addAll(data.results)
                    showChart = true
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }




    Box(modifier = Modifier.fillMaxSize()){

        Box(modifier = Modifier.align(Alignment.BottomCenter)){
            Column(modifier = Modifier.fillMaxWidth()){

                LazyRow {
                    itemsIndexed(list, key = { index, _ -> UUID.randomUUID().toString() }) { index, item ->
                        if (index == 0) {
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                        }

                        Button(onClick = {showBottomWindow.value = true}/*, enabled = !isLoading*/) { Text(item)}

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
                        onClick = {},
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
    if(showBottomWindow.value) bottomwindow(showBottom = showBottomWindow)

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Stock Screen")
        Button(onClick = {stockQuery("AAPL")}, enabled = !isLoading) { Text("Apple")}
        Button(onClick = { stockQuery("MSFT")}, enabled = !isLoading) { Text("Microsoft")}
        Button(onClick = { stockQuery("BLK")}, enabled = !isLoading) { Text("Blackrock") }



        if (isLoading) {
                CircularProgressIndicator()
        }

    }

    if (showChart && stockData != null) {
        stockViewModel.setData(stockData)
        //navController.navigate("stock_detail")
    }
}

@Composable
fun bottomwindow(showBottom : MutableState<Boolean>){
    Box(modifier = Modifier.fillMaxSize()){
        Panel(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(), centerItems = false) {
            Box(modifier = Modifier.align(Alignment.TopEnd)){
                Row{
                    Text("34000 Ft") //TODO: esetleg novekedés
                    Spacer(modifier = Modifier.width(UIVar.Padding) )
                    Text("+1.4 %") //TODO: GYURI szín
                }

            }
            Column {
                HeaderText( "Apple" ) //TODO: ezt kell átírni majd nevet


                //TODO: gráf





                Button(onClick = {}) { Text("button1") }

                Row(modifier = Modifier.fillMaxWidth()){
                    Button(onClick = {showBottom.value = false}, modifier = Modifier
                        .weight(3f)
                        .align(Alignment.CenterVertically)) { Text("Mégse") }
                    Spacer(modifier = Modifier.width(UIVar.Padding) )
                    Button(onClick = {}, modifier = Modifier
                        .weight(3f)
                        .align(Alignment.CenterVertically)) { Text("Vásárlás")}
                    Spacer(modifier = Modifier.width(UIVar.Padding) )
                    IconButton(onClick = {}) {
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






