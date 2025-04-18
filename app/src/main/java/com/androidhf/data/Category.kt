package com.androidhf.data

enum class Category(val type : Type, val displayName : String) {
    FIZETES(Type.INCOME, "Fizetés"),
    ELOFIZETES(Type.EXPENSE,"Előfizetés");


    enum class Type{
        INCOME,
        EXPENSE
    }
}