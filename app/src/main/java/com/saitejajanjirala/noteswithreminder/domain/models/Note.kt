package com.saitejajanjirala.noteswithreminder.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id : Int?=0,
    val title : String= "",
    val description : String = "",
    val dateTime : Long = 0
):Parcelable{

}

class InvalidNoteException(msg : String) : Exception(msg)
{

}
