package com.saitejajanjirala.noteswithreminder.presentation.ui.detail.components

import android.util.Log
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.Converter
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.Converter.adjustToLocalMidnight
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.Converter.convertMillisToDate
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.Converter.getDateInMillis
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance(Locale.getDefault())

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = calendar.timeInMillis.also {
            Log.d("DatePickerModel", convertMillisToDate(it))
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { utcMillis ->
                    val localMidnightMillis = adjustToLocalMidnight(utcMillis)
                    Log.d("DatePickerModal", "UTC Millis: $utcMillis")
                    Log.d("DatePickerModal", "Local Midnight Millis: $localMidnightMillis")
                    Log.d("DatePickerModal", "Local Date: ${convertMillisToDate(localMidnightMillis)}")
                    onDateSelected(localMidnightMillis)
                } ?: onDateSelected(null)

                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState, showModeToggle = true)
    }
}