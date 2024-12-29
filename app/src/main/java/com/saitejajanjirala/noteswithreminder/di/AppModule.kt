package com.saitejajanjirala.noteswithreminder.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.saitejajanjirala.noteswithreminder.data.local.NotesDao
import com.saitejajanjirala.noteswithreminder.data.local.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesDatabase(application: Application) : NotesDatabase {
        return Room
            .databaseBuilder(application,NotesDatabase::class.java,NotesDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesNoteDao(notesDatabase: NotesDatabase) : NotesDao {
        return notesDatabase.notesDao
    }

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}