package com.androidhf.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.androidhf.R

enum class Category(val type : Type, @StringRes val stringResId: Int) {
    FIZETES(Type.INCOME, R.string.category_salary),
    OSZTONDIJ(Type.INCOME, R.string.category_scholarship),
    BONUSZ(Type.INCOME, R.string.category_bonus),
    EGYEB_INC(Type.INCOME,R.string.category_otherincome),
    ELOFIZETES(Type.EXPENSE,R.string.category_subscription),
    ELELMISZER(Type.EXPENSE,R.string.category_food),
    ENTERTAINMENT(Type.EXPENSE,R.string.category_entertainment),
    EGYEB_EXP(Type.EXPENSE,R.string.category_otherexpense);

    fun getDisplayName(context: Context): String {
        return context.getString(stringResId)
    }

    enum class Type{
        INCOME,
        EXPENSE
    }
}