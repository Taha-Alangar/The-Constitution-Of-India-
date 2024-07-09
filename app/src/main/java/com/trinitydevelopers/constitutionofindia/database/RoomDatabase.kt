package com.trinitydevelopers.constitutionofindia.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trinitydevelopers.constitutionofindia.database.bookmark.BookmarkDao
import com.trinitydevelopers.constitutionofindia.database.bookmark.BookmarkEntity
import com.trinitydevelopers.constitutionofindia.database.notes.NotesDao
import com.trinitydevelopers.constitutionofindia.database.notes.NotesEntity

@Database(entities = [BookmarkEntity::class, NotesEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun notesDao(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "constitution_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}