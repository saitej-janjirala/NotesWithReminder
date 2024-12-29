package com.saitejajanjirala.noteswithreminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.saitejajanjirala.noteswithreminder.domain.models.Note

@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase(){
    abstract val notesDao: NotesDao
    companion object{
        const val DB_NAME = "sj_notes_db"
    }
}