package com.saitejajanjirala.noteswithreminder.presentation.ui.home

import androidx.lifecycle.ViewModel
import app.cash.turbine.test
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.models.Result
import com.saitejajanjirala.noteswithreminder.domain.repo.NotesRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest{

    private lateinit var viewModel: MainViewModel

    private lateinit var notesRepo: NotesRepo

    @Before
    fun setUp(){
        Dispatchers.setMain(StandardTestDispatcher())
        notesRepo = mockk(relaxed = true)
    }

    @Test
    fun `init triggers fetchNotes and updates the states accordingly`()= runTest{
        val notes = listOf(
            Note(
                id = 1,
                title = "Hello",
                description = "Description",
                dateTime = 1L
            )
        )
        coEvery { notesRepo.getAllNotes() } returns flow{
            emit(notes)
        }
        viewModel = MainViewModel(notesRepo)

        viewModel.state.test {
            val first = awaitItem()
            assert(first is Result.Loading && first.isLoading)
            val third = awaitItem()
            assert(third is Result.Loading && !third.isLoading)
            val fourth = awaitItem()
            assert(fourth is Result.Success && fourth.data==notes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote calls repository with correct parameter`() = runTest {
        val mockNote = Note(1, "Title", "Content")
        val viewModel = MainViewModel(notesRepo)

        viewModel.deleteNote(mockNote)
        advanceUntilIdle()
        coVerify { (notesRepo).deleteNote(mockNote) }
    }

    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

}