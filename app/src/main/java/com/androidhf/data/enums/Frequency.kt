package com.androidhf.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.androidhf.R

enum class Frequency(@StringRes val stringResId: Int) {
    EGYSZERI(R.string.frequency_once),
    NAPI(R.string.frequency_daily),
    HETI(R.string.frequency_weekly),
    HAVI(R.string.frequency_monthly);

    fun getDisplayName(context: Context): String {
        return context.getString(stringResId)
    }
}