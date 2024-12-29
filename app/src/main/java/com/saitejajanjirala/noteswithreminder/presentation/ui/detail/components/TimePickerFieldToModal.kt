package com.saitejajanjirala.noteswithreminder.presentation.ui.detail.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import com.saitejajanjirala.noteswithreminder.R
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.Converter
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.TimePickerState

@Composable
fun TimePickerFieldToModal(
    modifier: Modifier = Modifier,
    state: TimePickerState,
    is24Hour: Boolean = true,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    OutlinedTextField(
        enabled = state.isEnabled,
        value = if (state.hour==0 && state.minute==0) "" else Converter.formatTime(state.hour,state.minute),
        onValueChange = { },
        label = { Text("Time") },
        placeholder = { Text("HH:MM") },
        trailingIcon = {
            Icon(painter = painterResource(R.drawable.ic_clock), contentDescription = "Select time")
        },
        modifier = modifier
            .pointerInput(state) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null && state.isEnabled) {
                        showTimePickerDialog = true
                    }
                }
            }
    )

    if (showTimePickerDialog) {
        TimePickerDialogWithTimePicker(
            is24Hour = is24Hour,
            onTimeSelected = { hour, minute ->
                onTimeSelected(hour, minute)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }
}
