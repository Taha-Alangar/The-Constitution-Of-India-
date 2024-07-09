package com.trinitydevelopers.constitutionofindia.database.notes

import android.provider.ContactsContract
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesEntity)

    @Query("SELECT * FROM notes ORDER BY id DESC" )
    suspend fun getAllNotes(): List<NotesEntity>

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET title = :title, description = :description WHERE id = :noteId")
    suspend fun updateNote(noteId: Int, title: String, description: String)

}