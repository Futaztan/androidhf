package com.androidhf.data.datatypes

import android.content.Context
import androidx.annotation.StringRes
import com.androidhf.R

enum class SavingsType(@StringRes val stringResId: Int) {
    INCOMEGOAL_BYTIME(R.string.savingstype_time),
    INCOMEGOAL_BYAMOUNT(R.string.savingstype_incamount),
    EXPENSEGOAL_BYAMOUNT(R.string.savingstype_expamount);

    fun getDisplayName(context: Context): String {
        return context.getString(stringResId)
    }
}