package com.androidhf.ui.screens.stock.uielements

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidhf.R
import com.androidhf.data.datatypes.Company
import com.androidhf.ui.reuseable.BorderBox
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
fun FavoriteCompanyBox(
    company: Company,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // StockViewModel hozzáadása, hogy hozzáférjünk az adatokhoz
    //val stockViewModel: StockViewModel = hiltViewModel()

    // Állapot változók a chart adatokhoz
    var stockData by remember { mutableStateOf<List<AggregateDTO>>(emptyList()) }
    var price by remember { mutableStateOf("0.00") }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }


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
                    Log.d("FavoriteCompanyBox", "Attempt ${attempts + 1} for ${company.companyCode}")

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
                                Log.d("FavoriteCompanyBox", "Data found for ${company.companyCode}! ${data.results.size} data points.")
                                stockData = data.results

                                // Utolsó záróár kinyerése és formázása
                                price = "%.2f$".format(data.results.last().close ?: 0.0)
                                foundData = true
                            } else {
                                Log.e("FavoriteCompanyBox", "No data: trying attempt " + attempts)
                                // Ha nem találtunk adatot, újra próbálkozunk
                                attempts++
                                delay(5000) // 5 másodperc várakozás a következő próbálkozás előtt
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("FavoriteCompanyBox", "Error fetching data: ${e.message}")
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
                Log.e("FavoriteCompanyBox", "Unexpected error", e)
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

    BorderBox(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Sárga csillag a jobb felső sarokban
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fav_48),
                    contentDescription = "Remove from favorites",
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFFFFD700) // Arany sárga szín
                )
            }

            // Tartalom elrendezése
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // Felső rész: Ticker kód és ár
                Text(
                    text = company.companyCode,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Ár megjelenítése (az árat a bottomwindow-ból vesszük)
                if (isLoading) {
                    Text(
                        text = "Loading...",
                        style = TextStyle(fontSize = 16.sp)
                    )
                } else {
                    Text(
                        text = price,
                        style = TextStyle(fontSize = 16.sp)
                    )
                }

                // Mini chart rész (a kép közepét és alját foglalja el)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 8.dp)
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
                    } else if (hasError || stockData.isEmpty()) {
                        Text(
                            text = "Chart data unavailable",
                            style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        // A már meglévő MiniStockChart használata
                        MiniStockChart(stockData = stockData, 130.dp)
                    }
                }
            }
        }
    }
}