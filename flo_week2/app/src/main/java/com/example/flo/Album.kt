package com.example.flo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.week1.Song

@Entity(tableName = "AlbumTable")
data class Album(
    @PrimaryKey(autoGenerate = false) var id: Int,
    var title: String? = "",
    var singer: String? = "",
    var coverImg: Int? = null,
    //var song: ArrayList<Song>? = null,
    //var play: Boolean = false
)