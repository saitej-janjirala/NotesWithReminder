package com.saitejajanjirala.noteswithreminder.data.repo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.saitejajanjirala.noteswithreminder.data.local.NotesDao
import com.saitejajanjirala.noteswithreminder.domain.models.InvalidNoteException
import com.saitejajanjirala.noteswithreminder.domain.models.Note
import com.saitejajanjirala.noteswithreminder.domain.models.Result
import com.saitejajanjirala.noteswithreminder.domain.repo.NotesRepo
import com.saitejajanjirala.noteswithreminder.presentation.receiver.NoteReceiver
import com.saitejajanjirala.noteswithreminder.util.Util
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

class NotesRepoImpl @Inject constructor(private val notesDao: NotesDao,private val context : Context) : NotesRepo {
    override fun getAllNotes(): Flow<List<Note>>
    {
        return notesDao.getAllNotes()
    }

    override suspend fun updateNote(note: Note): Flow<Result<Note>> =flow{
        emit(Result.Loading(true))
        notesDao.updateNote(note)
        emit(Result.Loading(false))
        emit(Result.Success(null))
    }

    override suspend fun deleteNote(note: Note) {
        note.id?.let {
            notesDao.deleteNoteWithNoteId(it)
        }
    }

    override suspend fun insertNote(note: Note):Int {
        var isAnyFalse = true
        var msg = ""
        if(note.title.isBlank()){
            isAnyFalse = false
            msg = "Title shouldn't be empty"
        }
        if(note.description.isBlank()){
            isAnyFalse = false
            msg = "Description shouldn't be empty"
        }
        when {
            note.dateTime == 0L -> {
                isAnyFalse = false
                msg = "Please Select Date and Time"
            }
            note.dateTime< Calendar.getInstance().timeInMillis ->{
                isAnyFalse = false
                msg = "Please Select the time in the future"
            }
        }
        if(isAnyFalse) {
            return notesDao.insertNote(note).toInt()
        }
        else{
            throw InvalidNoteException(msg)
        }
    }

    override suspend fun getNoteById(id: Int): Note? {
        return notesDao.getNoteById(id)
    }

    override fun scheduleNotification(note: Note) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NoteReceiver::class.java).apply {
            putExtra(Util.NOTE_ID, note.id)
            putExtra(Util.NOTE_TITLE, note.title)
            putExtra(Util.NOTE_DESCRIPTION, note.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id!!,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                scheduleAlarm(alarmManager, note, pendingIntent)
            }
        }
        else{
            scheduleAlarm(alarmManager, note, pendingIntent)
        }
    }

    fun scheduleAlarm(alarmManager: AlarmManager,note: Note,pendingIntent: PendingIntent){
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            note.dateTime,
            pendingIntent
        )
    }

    override fun cancelNotification(note: Note) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NoteReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id!!, // Use the note ID as the unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

}