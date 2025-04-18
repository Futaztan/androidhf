package com.androidhf.data

enum class Category(val type : Type) {
    FIZETES(Type.INCOME),
    ELOFIZETES(Type.EXPENSE);


    enum class Type{
        INCOME,
        EXPENSE
    }
}