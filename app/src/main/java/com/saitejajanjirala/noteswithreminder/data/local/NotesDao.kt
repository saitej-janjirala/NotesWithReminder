package com.saitejajanjirala.noteswithreminder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * from notes")
    fun getAllNotes() : Flow<List<Note>>

    @Query("DELETE FROM notes where id=:id")
    suspend fun deleteNoteWithNoteId(id : Int)

    @Query("SELECT * FROM notes where id=:id")
    suspend fun getNoteById(id : Int) : Note?

    @Update
    suspend fun updateNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note) :Long
}