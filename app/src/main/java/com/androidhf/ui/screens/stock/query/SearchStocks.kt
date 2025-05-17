package com.androidhf.ui.screens.stock.query

import android.util.Log
import io.polygon.kotlin.sdk.rest.ComparisonQueryFilterParameters
import io.polygon.kotlin.sdk.rest.PolygonRestClient
import io.polygon.kotlin.sdk.rest.reference.SupportedTickersParameters
import io.polygon.kotlin.sdk.rest.reference.TickerDTO
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Collections

fun searchStocks(polygonClient: PolygonRestClient, searchQuery: String): List<TickerDTO>? {
    Log.d("StockSearch", "Searching for stocks matching: $searchQuery")

    try {
        // A SupportedTickersParameters segítségével kereshetünk részvényeket
        val params = SupportedTickersParameters(
            ticker = ComparisonQueryFilterParameters(searchQuery).toString(), // Keresés a ticker kódban
            market = "stocks", // Csak részvények
            activeSymbolsOnly = true,     // Csak aktív részvények
            limit = 15         // Korlátozzuk az eredmények számát
        )

        // Részvények lekérése
        val result = polygonClient.referenceClient.getSupportedTickersBlocking(params)

        //Log.d("StockSearch", "Found ${result.results.size} results for query: $searchQuery")

        // Visszaadjuk a találatokat
        return result.results
    } catch (e: Exception) {
        Log.e("StockSearch", "Error searching for stocks: ${e.message}")
        return emptyList()
    }
}

fun searchStocksREST(apiKey: String, searchQuery: String, callback: (List<Pair<String, String>>) -> Unit) {
    if (searchQuery.length < 2) {
        callback(emptyList())
        return
    }

    val client = OkHttpClient()
    val queryText = searchQuery.trim()
    val uppercaseQuery = queryText.uppercase()

    // Két keresés: egy a ticker kód alapján, egy a név alapján
    val urlTicker = "https://api.polygon.io/v3/reference/tickers?ticker.startsWith=$uppercaseQuery&active=true&sort=ticker&order=asc&limit=10&apiKey=$apiKey"
    val urlSearch = "https://api.polygon.io/v3/reference/tickers?search=$queryText&active=true&sort=ticker&order=asc&limit=10&apiKey=$apiKey"

    // Használjunk thread-biztos listát
    val allResults = Collections.synchronizedList(mutableListOf<Pair<String, String>>())
    val lock = Object() // Szinkronizációs objektum
    var completedRequests = 0

    // Első kérés: Ticker keresés
    client.newCall(Request.Builder().url(urlTicker).build()).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("StockSearch", "Ticker search failed: ${e.message}")
            synchronized(lock) {
                completedRequests++
                if (completedRequests == 2) {
                    // Mindkét kérés befejeződött, elküldjük az eredményeket
                    val uniqueResults = allResults.distinctBy { it.second }
                    callback(uniqueResults)
                }
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                try {
                    val jsonObject = JSONObject(responseBody)
                    val resultsArray = jsonObject.optJSONArray("results")

                    if (resultsArray != null) {
                        for (i in 0 until resultsArray.length()) {
                            val ticker = resultsArray.getJSONObject(i)
                            val symbol = ticker.getString("ticker")
                            val name = ticker.optString("name", symbol)

                            // Szűrjük az eredményeket, hogy csak a relevánsak maradjanak
                            if (symbol.contains(uppercaseQuery) ||
                                name.uppercase().contains(uppercaseQuery)) {
                                allResults.add(Pair(name, symbol))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("StockSearch", "JSON parsing error (ticker): ${e.message}", e)
                }
            } else {
                Log.e("StockSearch", "Ticker search API call failed with code: ${response.code}")
            }

            synchronized(lock) {
                completedRequests++
                if (completedRequests == 2) {
                    // Mindkét kérés befejeződött, elküldjük az eredményeket
                    val uniqueResults = allResults.distinctBy { it.second }
                    callback(uniqueResults)
                }
            }
        }
    })

    // Második kérés: Általános keresés
    client.newCall(Request.Builder().url(urlSearch).build()).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("StockSearch", "General search failed: ${e.message}")
            synchronized(lock) {
                completedRequests++
                if (completedRequests == 2) {
                    // Mindkét kérés befejeződött, elküldjük az eredményeket
                    val uniqueResults = allResults.distinctBy { it.second }
                    callback(uniqueResults)
                }
            }
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                try {
                    val jsonObject = JSONObject(responseBody)
                    val resultsArray = jsonObject.optJSONArray("results")

                    if (resultsArray != null) {
                        for (i in 0 until resultsArray.length()) {
                            val ticker = resultsArray.getJSONObject(i)
                            val symbol = ticker.getString("ticker")
                            val name = ticker.optString("name", symbol)

                            // Szűrjük az eredményeket, hogy csak a relevánsak maradjanak
                            if (symbol.contains(uppercaseQuery) ||
                                name.uppercase().contains(uppercaseQuery)) {
                                allResults.add(Pair(name, symbol))
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("StockSearch", "JSON parsing error (search): ${e.message}", e)
                }
            } else {
                Log.e("StockSearch", "General search API call failed with code: ${response.code}")
            }

            synchronized(lock) {
                completedRequests++
                if (completedRequests == 2) {
                    // Mindkét kérés befejeződött, elküldjük az eredményeket
                    val uniqueResults = allResults.distinctBy { it.second }
                    callback(uniqueResults)
                }
            }
        }
    })
}

/*
fun getCompanyCodesFromSearch(results: List<TickerDTO>?): List<String>
{

}*/