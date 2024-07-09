package com.trinitydevelopers.constitutionofindia.database.bookmark

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0,
    val entityId: String, // The original ID of the amendment or part
    val title: String,
    val smallDescription:String?="",
    val description: String,
    val type: String // To differentiate between types like "news", "amendment", etc.
)