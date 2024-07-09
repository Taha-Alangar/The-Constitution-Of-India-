package com.trinitydevelopers.constitutionofindia.database.bookmark

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)


    @Query("SELECT * FROM bookmarks WHERE entityId = :entityId AND type = :type")
    suspend fun getBookmarkById(entityId: String, type: String): BookmarkEntity?

    @Query("SELECT * FROM bookmarks")
    suspend fun getAllBookmarks(): List<BookmarkEntity>
}