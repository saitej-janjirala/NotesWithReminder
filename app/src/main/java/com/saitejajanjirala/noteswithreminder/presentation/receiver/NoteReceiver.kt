package com.saitejajanjirala.noteswithreminder.presentation.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.saitejajanjirala.noteswithreminder.MyApplication
import com.saitejajanjirala.noteswithreminder.R
import com.saitejajanjirala.noteswithreminder.data.local.NotesDao
import com.saitejajanjirala.noteswithreminder.data.worker.DeleteNoteWorker
import com.saitejajanjirala.noteswithreminder.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class NoteReceiver: BroadcastReceiver() {
    @Inject
    lateinit var notesDao: NotesDao
    override fun onReceive(context: Context, intent: Intent?) {
        intent?.let {
            val noteId = it.getIntExtra(Util.NOTE_ID,-1)
            val noteTitle = it.getStringExtra(Util.NOTE_TITLE)?:"Reminder"
            val noteDescription = it.getStringExtra(Util.NOTE_DESCRIPTION)?:"Reminder Description"
            val notificationManager = NotificationManagerCompat.from(context)
            val notification = NotificationCompat.Builder(context,Util.NOTES_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(noteTitle)
                .setContentText(noteDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notificationManager.notify(noteId, notification)
            val workManager = WorkManager.getInstance(context)
            val workRequest = OneTimeWorkRequest.Builder(DeleteNoteWorker::class.java)
                .setInputData(workDataOf("note_id" to noteId))
                .build()

            workManager.enqueue(workRequest)
        }
    }
}