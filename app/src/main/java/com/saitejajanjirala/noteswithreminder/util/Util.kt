package com.saitejajanjirala.noteswithreminder.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

object Util {
    const val NOTE_ID = "note_id"
    const val IS_ENABLED = "is_enabled"
    const val NOTE_TITLE = "note_title"
    const val NOTE_DESCRIPTION = "note_description"
    const val NOTES_CHANNEL_ID = "NOTES_CHANNEL_ID"
    const val NOTES_CHANNEL_NAME = "NOTES_CHANNEL_NAME"

    @Composable
    fun Dp.toPx(): Float {
        val density = LocalDensity.current
        return with(density) { this@toPx.toPx() }
    }
}