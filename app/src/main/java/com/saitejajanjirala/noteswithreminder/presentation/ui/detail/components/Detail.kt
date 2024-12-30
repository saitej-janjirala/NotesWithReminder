package com.saitejajanjirala.noteswithreminder.presentation.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.saitejajanjirala.noteswithreminder.MyApplication
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.AddEditNotesEvent
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.DetailViewModel
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.UiEvent
import dagger.hilt.android.internal.Contexts.getApplication

@Composable
fun DetailScreen(
    navHostController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel(),
){

    val title = viewModel.noteTitle.value
    val description = viewModel.noteDescription.value
    val snackBarState = remember{
        SnackbarHostState()
    }

    val date by remember {viewModel.noteDate}
    val time by remember { viewModel.noteTime}
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val isEnabled by remember { viewModel.isEnabled }

    LaunchedEffect(key1 = true){
        viewModel.fetchNoteData()
        viewModel.eventFlow.collect{
            focusManager.clearFocus()
            when(it){
                 UiEvent.SaveNote ->{
                    snackBarState.showSnackbar(
                        message = "Note Saved",
                        duration = SnackbarDuration.Short
                    )
                }
                is UiEvent.ShowSnackBar -> {
                    snackBarState.showSnackbar(it.msg)
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarState)
        },
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues) // Respect scaffold's padding
                .padding(12.dp)
        ) {

            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                TransparentTextField(
                    isHintVisible = title.isHintVisible,
                    isEnabled = title.isEnabled,
                    text = title.text,
                    hint = title.hint,
                    onFocusChange = {
                        viewModel.onEvent(AddEditNotesEvent.OnTitleFocusChanged(it))
                    },
                    onValueChange = {
                        viewModel.onEvent(AddEditNotesEvent.OnTitleEntered(it))
                    },
                    textStyle = MaterialTheme.typography.titleLarge,
                    singleLine = true
                )
                Spacer(Modifier.height(20.dp))
                TransparentTextField(
                    modifier = Modifier.height(300.dp),
                    isHintVisible = description.isHintVisible,
                    isEnabled = description.isEnabled,
                    text = description.text,
                    hint = description.hint,
                    onFocusChange = {
                        viewModel.onEvent(AddEditNotesEvent.OnDescriptionFocusChanged(it))
                    },
                    onValueChange = {
                        viewModel.onEvent(AddEditNotesEvent.OnDescriptionEntered(it))
                    },
                    textStyle = MaterialTheme.typography.titleLarge,
                    singleLine = false
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                    horizontalArrangement = Arrangement.Absolute.SpaceEvenly,

                ) {
                    DatePickerFieldToModal(Modifier.weight(0.6f),date){
                        viewModel.onEvent(AddEditNotesEvent.OnDatePicked(it))
                    }
                    Spacer(Modifier.width(8.dp))
                    TimePickerFieldToModal(modifier = Modifier.weight(0.4f),time, onTimeSelected = { hour, min->
                        viewModel.onEvent(AddEditNotesEvent.OnTimePicked(hour, min))
                    })
                }
            }

            // Fixed Save Button
            Button(
                enabled = isEnabled,
                onClick = {
                    viewModel.onEvent(AddEditNotesEvent.OnSaveNote)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp) // Padding from bottom
            ) {
                Text(
                    text = "Save",
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }






}

