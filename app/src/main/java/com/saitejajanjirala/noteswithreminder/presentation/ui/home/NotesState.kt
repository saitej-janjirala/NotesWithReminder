package com.saitejajanjirala.noteswithreminder.presentation.ui.home

import com.saitejajanjirala.noteswithreminder.domain.models.Note

data class NotesState(
    var notes : List<Note> = emptyList()
)
