package com.saitejajanjirala.noteswithreminder.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.saitejajanjirala.noteswithreminder.data.local.NotesDao
import javax.inject.Inject

class MyWorkerFactory @Inject constructor (private val notesDao: NotesDao) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return DeleteNoteWorker(appContext, workerParameters, notesDao)

    }
}
