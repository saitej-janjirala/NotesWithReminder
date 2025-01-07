package com.saitejajanjirala.noteswithreminder.data.repo

import android.content.Context
import app.cash.turbine.test
import com.saitejajanjirala.noteswithreminder.data.local.NotesDao
import com.saitejajanjirala.noteswithreminder.domain.models.InvalidNoteException
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.models.Result
import com.saitejajanjirala.noteswithreminder.presentation.ui.home.CardItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class NotesRepoImplTest {

    private lateinit var notesDao: NotesDao
    private lateinit var context: Context
    @Before
    fun setUp() {
        notesDao = mockk(relaxed = true)
        context = mockk(relaxed = true)
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @Test
    fun `test get all notes returns notes list`() = runTest{
        val notes = listOf(
            Note(
                id = 1,
                title = "Hello",
                description = "Description",
                dateTime = 1L
            )
        )
        coEvery { notesDao.getAllNotes() } returns flowOf(notes)
        val repo = NotesRepoImpl(notesDao,context)
        repo.getAllNotes().test {
            val first = awaitItem()
            assertEquals(first,notes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertNote throws exception when title is blank`()  = runTest{
        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY,get(Calendar.HOUR_OF_DAY)+1)
        }.timeInMillis
        val note = Note(0, "", "Content",time)
        val repo = NotesRepoImpl(notesDao, context)
        try {
            repo.insertNote(note)
        }catch (e:InvalidNoteException){
            assertEquals(e.message, "Title shouldn't be empty")
        }

    }

    @Test
    fun `insertNote inserts note when valid`() = runTest {
        val repo = NotesRepoImpl(notesDao,context)
        val note = Note(0, "Title", "Content", System.currentTimeMillis() + 1000)
        coEvery {  notesDao.insertNote(note) } returns 1L

        val result = repo.insertNote(note)

        assertEquals(1, result)
        coVerify {  (notesDao).insertNote(note)}
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}