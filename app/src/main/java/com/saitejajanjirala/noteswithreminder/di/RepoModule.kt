package com.saitejajanjirala.noteswithreminder.di

import com.saitejajanjirala.noteswithreminder.data.repo.NotesRepoImpl
import com.saitejajanjirala.noteswithreminder.domain.repo.NotesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun getNotesRepo(notesRepoImpl: NotesRepoImpl):NotesRepo
}