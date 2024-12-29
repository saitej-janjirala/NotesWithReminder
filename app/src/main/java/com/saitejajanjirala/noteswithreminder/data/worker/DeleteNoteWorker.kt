package com.saitejajanjirala.noteswithreminder.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.saitejajanjirala.noteswithreminder.data.local.NotesDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltWorker
class DeleteNoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notesDao: NotesDao
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val noteId = inputData.getInt("note_id", -1)

        if (noteId == -1) {
            return Result.failure()
        }

        CoroutineScope(Dispatchers.IO).launch {
            notesDao.deleteNoteWithNoteId(noteId)
        }

        return Result.success()
    }
}
