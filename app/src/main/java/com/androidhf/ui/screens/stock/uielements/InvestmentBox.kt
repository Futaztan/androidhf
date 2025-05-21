package com.androidhf.ui.screens.stock.uielements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidhf.R
import com.androidhf.data.datatypes.Company
import com.androidhf.data.datatypes.Stock
import com.androidhf.ui.reuseable.BorderBox
import com.androidhf.ui.reuseable.UIVar
import com.androidhf.ui.screens.stock.StockViewModel
import com.androidhf.ui.screens.stock.query.stocksAggregatesBars
import io.polygon.kotlin.sdk.rest.AggregateDTO
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun InvestmentBox(
    stock: Stock,
    company: Company,
    amount: Int,
    buyprice: Float,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // StockViewModel hozzáadása, hogy hozzáférjünk az adatokhoz
    val stockViewModel: StockViewModel = hiltViewModel()

    // Állapot változók a chart adatokhoz
    var stockData by remember { mutableStateOf<List<AggregateDTO>>(emptyList()) }
    var currentPrice by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    var showPopup by remember { mutableStateOf(false) }

    if (showPopup) {
        Popup {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = 250.dp))
            {
                Box(
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxWidth()
                        .border(4.dp, UIVar.boxBorderColor(), RoundedCornerShape(UIVar.Radius))
                        .background(MaterialTheme.colorScheme.onError, RoundedCornerShape(UIVar.Radius))
                        .padding(UIVar.Padding)
                        .align(Alignment.BottomCenter)
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Biztosan törölni szeretné?", modifier = Modifier.align(Alignment.CenterVertically), color = MaterialTheme.colorScheme.error)
                            androidx.compose.material3.Icon(
                                painter = painterResource(id = R.drawable.ic_warning_48),
                                contentDescription = "Warning",
                                modifier = Modifier.align(Alignment.CenterVertically),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        Row {
                            Button(
                                onClick = {
                                    showPopup = false
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.weight(3f)
                            ) {
                                Text("Igen", color = MaterialTheme.colorScheme.onError) // szöveg szín
                            }
                            Spacer(modifier = Modifier.width(UIVar.Padding))
                            Button(onClick = {
                                showPopup=false
                            }, modifier = Modifier.weight(7f)
                            ) {
                                Text("Nem")
                            }
                        }
                    }
                }
            }
        }
    }

    // Adatok lekérése amikor a komponens létrejön
    LaunchedEffect(company.companyCode) {
        isLoading = true
        hasError = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val POLYGON_API_KEY = PolygonRestClient("kwQO2EZ6YFWcSA0Vkx4pCXyE6Guf2HJg")

                // Kezdeti paraméterek
                val now = LocalDate.now()
                var endDate = now
                var startDate = now.minusDays(8)

                var attempts = 0
                var foundData = false

                // Maximum 5 próbálkozás
                while (!foundData && attempts < 40) {
                    Log.d("InvestmentBox", "Attempt ${attempts + 1} for ${company.companyCode}")

                    try {
                        val data = stocksAggregatesBars(
                            POLYGON_API_KEY,
                            company.companyCode,
                            startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )

                        withContext(Dispatchers.Main) {
                            // Ellenőrizzük, hogy kaptunk-e értelmes adatot
                            if (data.results != null && data.results.isNotEmpty()) {
                                Log.d("InvestmentBox", "Data found for ${company.companyCode}! ${data.results.size} data points.")
                                stockData = data.results

                                // Utolsó záróár kinyerése
                                currentPrice = data.results.last().close ?: 0.0
                                foundData = true
                            } else {
                                Log.e("InvestmentBox", "No data: trying attempt " + attempts)
                                // Ha nem találtunk adatot, újra próbálkozunk
                                attempts++
                                delay(5000) // 5 másodperc várakozás a következő próbálkozás előtt
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("InvestmentBox", "Error fetching data: ${e.message}")
                        attempts++
                        delay(3000) // 3 másodperc várakozás hiba esetén
                    }
                }

                // Ha minden próbálkozás után sem találtunk adatot
                if (!foundData) {
                    withContext(Dispatchers.Main) {
                        hasError = true
                    }
                }
            } catch (e: Exception) {
                Log.e("InvestmentBox", "Unexpected error", e)
                withContext(Dispatchers.Main) {
                    hasError = true
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    // Százalékos változás kiszámítása
    val percentChange = if (buyprice > 0 && currentPrice > 0) {
        ((currentPrice - buyprice) / buyprice) * 100
    } else {
        0.0
    }

    // Meghatározzuk a százalék előjelének színét
    val percentColor = when {
        percentChange > 0 -> Color(0xFF4CAF50) // zöld pozitív változáshoz
        percentChange < 0 -> Color(0xFFFF5252) // piros negatív változáshoz
        else -> Color.Gray // szürke, ha nincs változás
    }

    // Előjel a százalékhoz
    val percentSign = if (percentChange > 0) "+" else ""

    BorderBox(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Törlés gomb a jobb felső sarokban
            IconButton(
                onClick = {showPopup = true},
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_24), // Használj egy X ikont
                    contentDescription = "Remove investment",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red
                )
            }

            // Tartalom elrendezése
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Ticker kód
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)) {
                            append(stock.companyCode + " ")
                        }
                        withStyle(style = SpanStyle(fontSize = 8.sp)) {
                            append(company.companyName)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(UIVar.Padding))
                Text(
                    text = "Amount: ${amount}",
                    style = TextStyle(fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(UIVar.Padding))

                // Jelenlegi ár
                if (isLoading) {
                    Text(
                        text = "Loading...",
                        style = TextStyle(fontSize = 14.sp)
                    )
                } else if (hasError) {
                    Text(
                        text = "N/A",
                        style = TextStyle(fontSize = 14.sp)
                    )
                } else {
                    Row (modifier = Modifier.fillMaxWidth()){
                        Box(modifier = Modifier.fillMaxWidth()){
                            Text(
                                text = "Bought for",
                                style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.align(Alignment.CenterStart)
                            )

                            Text(
                                text = "Currently",
                                style = TextStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                    Row (modifier = Modifier.fillMaxWidth()){
                        Box(modifier = Modifier.fillMaxWidth()){
                            Text(
                                text = "%.2f$".format(buyprice.toFloat() * amount),
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            Text(
                                text = " ➤ ",
                                style = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.align(Alignment.Center)
                            )
                            Text(
                                text = "%.2f$".format(currentPrice.toFloat() * amount),
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(UIVar.Padding))
                }

                // Százalékos változás
                if (!isLoading && !hasError) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 14.sp)) {
                                append("Profit: %.2f$".format(currentPrice.toFloat() * amount - buyprice.toFloat() * amount))
                            }
                            withStyle(style = SpanStyle(color = percentColor)) {
                                append("  ${percentSign}%.2f%%".format(percentChange.toFloat()))
                            }

                        }
                    )
                }

                // Chart terület (a kép alját foglalja el)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (isLoading) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hold on...",
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    } else if (hasError) {
                        Text(
                            text = "Chart data unavailable",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (stockData.isNotEmpty()) {
                        // Egyszerűen használjuk a meglévő MiniStockChart komponenst
                        MiniStockChart(stockData = stockData, 70.dp)
                    }
                }
            }
        }
    }
}