package com.saitejajanjirala.noteswithreminder.domain.repo

import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.models.Result
import kotlinx.coroutines.flow.Flow

interface NotesRepo {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun updateNote(note: Note): Flow<Result<Note>>
    suspend fun deleteNote(note: Note)
    suspend fun insertNote(note: Note): Int
    suspend fun getNoteById(id: Int): Note?
    fun scheduleNotification(note: Note)
    fun cancelNotification(note: Note)
}