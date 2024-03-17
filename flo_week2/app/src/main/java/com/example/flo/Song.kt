package com.example.week1

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.flo.Album


//제목, 가수, 사진, 재생시간,현재 재생시간, isplaying(재생 되고 있는지)

//
@Entity(tableName = "SongTable")
data class Song(
    val title: String = "",
    val singer: String = "",
    var second: Int = 0,
    var playTime: Int = 0,
    var isPlaying: Boolean = false,
    var music: String = "",
    var albumImg: Int? = null,
    var isLike: Boolean = false,
    var albumIdx: Int
){
    @PrimaryKey(autoGenerate = true) var id:Int = 0
}
