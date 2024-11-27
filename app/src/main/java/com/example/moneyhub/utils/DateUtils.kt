package com.example.moneyhub.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun dateToMillis(dateStr: String): Long? {
        return try {
            sdf.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun millisToDate(timeMillis: Long): String {
        return sdf.format(Date(timeMillis))
    }
}