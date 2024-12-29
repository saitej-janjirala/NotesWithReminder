package com.saitejajanjirala.noteswithreminder.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.models.Result
import com.saitejajanjirala.noteswithreminder.domain.repo.NotesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor(private val notesRepo: NotesRepo):ViewModel(){
    private val _state = MutableStateFlow<Result<List<Note>>>(Result.Loading(false))
    val state : StateFlow<Result<List<Note>>> = _state


    init {
        fetchNotes()
    }

    fun fetchNotes(){
        _state.value = Result.Loading(true)
        viewModelScope.launch {
            notesRepo.getAllNotes().collect {
                _state.value = Result.Loading(false)
                _state.value = Result.Success(it)
            }
        }
    }


    fun deleteNote(note: Note){
        viewModelScope.launch {
            notesRepo.deleteNote(note)
        }

    }
}