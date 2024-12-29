package com.saitejajanjirala.noteswithreminder.presentation.ui.detail

import com.saitejajanjirala.noteswithreminder.domain.models.InvalidNoteException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Converter {
    fun convertMillisToDate(millis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        val dateFormat = java.text.SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun combineDateAndTime(dateInMillis: Long, hour: Int, minute: Int): Long {
        if(dateInMillis==0L){
            throw InvalidNoteException("Please pick date")
        }
        if(hour==0 && minute==0){
            throw InvalidNoteException("Please pick time")
        }
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }

    fun getDateInMillis(dateTimeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    fun getHour(dateTimeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeInMillis
        return calendar.get(Calendar.HOUR_OF_DAY)
    }
    fun getMinute(dateTimeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeInMillis
        return calendar.get(Calendar.MINUTE)
    }

    fun adjustToLocalMidnight(dateMillis: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.DAY_OF_MONTH,get(Calendar.DAY_OF_MONTH)+1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

}