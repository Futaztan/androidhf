package com.androidhf.data.datatypes

enum class Category(val type : Type, val displayName : String) {
    FIZETES(Type.INCOME, "Fizetés"),
    OSZTONDIJ(Type.INCOME, "Ösztöndíj"),
    BONUSZ(Type.INCOME, "Bónusz"),
    EGYEB_INC(Type.INCOME,"Egyéb bevételek"),
    ELOFIZETES(Type.EXPENSE,"Előfizetés"),
    ELELMISZER(Type.EXPENSE,"Élelmiszer"),
    ENTERTAINMENT(Type.EXPENSE,"Szórakozás"),
    EGYEB_EXP(Type.EXPENSE,"Egyéb kiadások");


    enum class Type{
        INCOME,
        EXPENSE
    }
}