package com.saitejajanjirala.noteswithreminder.presentation.ui.home

import androidx.annotation.Px
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.ResistanceConfig
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.models.Result
import com.saitejajanjirala.noteswithreminder.presentation.ui.detail.Converter
import com.saitejajanjirala.noteswithreminder.presentation.ui.util.Screen
import com.saitejajanjirala.noteswithreminder.util.Util
import com.saitejajanjirala.noteswithreminder.util.Util.toPx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController:NavHostController,
    homeViewModel: MainViewModel = hiltViewModel()
    ){

    val state by homeViewModel.state.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "Notes Reminder")
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick =  {
                navController.navigate(Screen.Detail.route)
            }, modifier = Modifier.background(color = Color.Transparent)){
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }
        },

        ) { paddingValues ->
        Column(Modifier.padding(paddingValues), verticalArrangement = Arrangement.Center) {
            when(state){
                is Result.Error -> {
                    Text(text = "${(state as Result.Error).msg}")
                }
                is Result.Loading -> {
                    if((state as Result.Loading).isLoading) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    (state as Result.Success).data?.let {
                        Spacer(Modifier.height(8.dp))
                        LazyColumn {
                            items(it){ note->
                                SwipeableCardItem(
                                    navController = navController,
                                    note = note,
                                    onDelete = {deleteNote->
                                        homeViewModel.deleteNote(deleteNote)
                                    }
                                )
                            }
                        }
                    }?: Text(text = "No Notes found")
                }
            }
        }
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableCardItem(
    navController: NavHostController,
    note: Note,
    onDelete: (Note) -> Unit
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val width = 300.dp
    val anchors = mapOf(
        -width.toPx() to -1,
        0f to 0,
        width.toPx() to 1
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                resistance = ResistanceConfig(
                    basis = 150.dp.toPx(), // Resistance effect for better UX
                    factorAtMin = 1.5f,
                    factorAtMax = 1.5f
                ),
                orientation = Orientation.Horizontal
            )
            .background(
                when (swipeableState.targetValue) {
                    -1 -> Color.Red
                    1 -> Color.Green
                    else -> MaterialTheme.colorScheme.background
                }
            )
    ) {
        CardItem(
            navController = navController,
            note = note,
            modifier = Modifier.offset { IntOffset(swipeableState.offset.value.toInt(), 0) }
        )
    }

    LaunchedEffect(swipeableState.currentValue) {
        when (swipeableState.currentValue) {
            -1 -> onDelete(note)
            1 -> {
                navController.navigate(Screen.Detail.route + "?note_id=${note.id}"+"?is_enabled=true")
                swipeableState.snapTo(0)
            }
        }
    }
}


@Composable
fun CardItem(modifier: Modifier,navController: NavHostController,note:Note){
    Column (modifier = modifier){
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    navController.navigate(Screen.Detail.route + "?note_id=${note.id}" + "?is_enabled=false")
                }) {
            Column(
                Modifier
                    .background(Color.LightGray)
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = note.title,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.description ?: "Description",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = Converter.getDateAndTimeInMillis(note.dateTime),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}