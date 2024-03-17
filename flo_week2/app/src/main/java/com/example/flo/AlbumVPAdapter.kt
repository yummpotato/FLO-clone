package com.example.flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson

class AlbumVPAdapter (fragment: Fragment, private var album: Album) :FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3 // 3개의 fragment (수록곡, 상세정보, 영상)

    override fun createFragment(position: Int): Fragment {
        return when(position) { // position: 수록곡, 상세정보, 영상
            0 -> SongFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            }
            1 -> DetailFragment()
            else -> VideoFragment()
        }
    }

}