package com.saitejajanjirala.noteswithreminder.presentation.ui.detail

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitejajanjirala.noteswithreminder.domain.models.InvalidNoteException
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.repo.NotesRepo
import com.saitejajanjirala.noteswithreminder.util.Util
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo : NotesRepo,
    private val application :Application,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private var _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow : SharedFlow<UiEvent>
        get() = _eventFlow

    private var _noteTitle = mutableStateOf(
        TextFieldState(
            hint = "Enter the title here"
        )
    )
    val noteTitle : State<TextFieldState>
        get() = _noteTitle


    private var _noteDescription = mutableStateOf(
        TextFieldState(
            hint = "Enter the description here"
        )
    )

    val noteDescription : State<TextFieldState>
        get() = _noteDescription

    private var _noteDate = mutableStateOf(
        DatePickerState(
            date = 0L,
            isEnabled = true
        )
    )
    val noteDate : State<DatePickerState>
        get() =_noteDate

    private var _noteTime = mutableStateOf(
       TimePickerState(
           hour = 0,
           minute = 0,
           isEnabled = true
       )
    )
    val noteTime : State<TimePickerState>
        get() =_noteTime

    var currentNoteId : Int? = null

    var prevNote : Note? = null
    init {
        fetchNoteData()
    }

    fun fetchNoteData() {
        savedStateHandle.get<Int>(Util.NOTE_ID)?.let {noteId->
            viewModelScope.launch {
                repo.getNoteById(noteId)?.let {
                    val isEnabled = savedStateHandle.get<Boolean>(Util.IS_ENABLED)?:true
                    prevNote = it
                    currentNoteId = it.id
                    _noteTitle.value = noteTitle.value.copy(
                        text = it.title,
                        isHintVisible = false,
                        isEnabled =  isEnabled
                    )
                    _noteDescription.value = noteDescription.value.copy(
                        text = it.description?:"",
                        isHintVisible = false,
                        isEnabled =  isEnabled
                    )
                    _noteDate.value = noteDate.value.copy(
                        date = Converter.getDateInMillis(it.dateTime),
                        isEnabled = isEnabled
                    )
                    _noteTime.value = noteTime.value.copy(
                        hour = Converter.getHour(it.dateTime),
                        minute = Converter.getMinute(it.dateTime),
                        isEnabled = isEnabled
                    )
                }
            }
        }

    }

    fun onEvent(event: AddEditNotesEvent)
    {
        when(event){
            is AddEditNotesEvent.OnDescriptionEntered -> {
                _noteDescription.value = noteDescription.value.copy(
                    text = event.text
                )
            }
            is AddEditNotesEvent.OnDescriptionFocusChanged -> {
                _noteDescription.value = noteDescription.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteDescription.value.text.isBlank()
                )
            }
            is AddEditNotesEvent.OnSaveNote -> {
                saveNote()
            }
            is AddEditNotesEvent.OnTitleEntered -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.text
                )
            }
            is AddEditNotesEvent.OnTitleFocusChanged -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
                )
            }

            is AddEditNotesEvent.OnDatePicked -> {
                _noteDate.value = noteDate.value.copy(
                    date = event.date
                )
            }
            is AddEditNotesEvent.OnTimePicked -> {
                _noteTime.value = noteTime.value.copy(
                    hour = event.hour,
                    minute = event.min
                )
            }
        }
    }

    private fun saveNote() {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val note = getCurrentNote()
                val id = repo.insertNote(note)
                prevNote = repo.getNoteById(id)
                if(currentNoteId!=null){
                    repo.cancelNotification(prevNote!!)
                }
                repo.scheduleNotification(prevNote!!)
                currentNoteId = id
                _eventFlow.emit(UiEvent.SaveNote)
            }catch (e : InvalidNoteException){
                _eventFlow.emit(UiEvent.ShowSnackBar(e.message.toString()))
            }
        }
    }

    private fun getCurrentNote() : Note{
        return  Note(
            id = currentNoteId,
            title = noteTitle.value.text,
            description = noteDescription.value.text,
            dateTime = Converter.combineDateAndTime(noteDate.value.date,noteTime.value.hour,noteTime.value.minute)
        )
    }
}

sealed class UiEvent{
    object SaveNote : UiEvent()
    data class ShowSnackBar(val msg : String): UiEvent()
}