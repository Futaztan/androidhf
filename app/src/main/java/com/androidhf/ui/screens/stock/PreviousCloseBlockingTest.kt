package com.androidhf.ui.screens.stock

data class PreviousCloseBlockingTest(
    val adjusted: Boolean,
    val count: Int,
    val queryCount: Int,
    val request_id: String,
    val results: List<Result>,
    val resultsCount: Int,
    val status: String,
    val ticker: String
)
data class Result(
    val T: String,
    val c: Double,
    val h: Double,
    val l: Double,
    val n: Int,
    val o: Double,
    val t: Long,
    val v: Double,
    val vw: Double
)