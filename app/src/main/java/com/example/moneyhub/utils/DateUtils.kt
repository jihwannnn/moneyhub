package com.example.moneyhub.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 날짜 -> 밀리초
    fun dateToMillis(dateStr: String): Long? {
        return try {
            sdf.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }

    // 밀리초 -> 날짜
    fun millisToDate(timeMillis: Long): String {
        return sdf.format(Date(timeMillis))
    }

    fun millisToLocalDateTime(timeMillis: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timeMillis)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        return dateTime
    }

    // timestamp의 자정(00:00:00.000)을 반환
    fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // timestamp의 해당 일의 마지막 시간(23:59:59.999)을 반환
    fun getEndOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    // timestamp의 해당 월의 첫 날 자정을 반환
    fun getStartOfMonth(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // timestamp의 해당 월의 마지막 날 마지막 시간을 반환
    fun getEndOfMonth(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis
    }
}