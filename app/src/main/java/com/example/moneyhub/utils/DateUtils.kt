package com.example.moneyhub.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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

    fun millisToLocalDateTime(timeMillis: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timeMillis)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        return dateTime
    }
}