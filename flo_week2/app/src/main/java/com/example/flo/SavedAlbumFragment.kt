package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.databinding.FragmentSavedalbumBinding

class SavedAlbumFragment : Fragment() {
    lateinit var binding: FragmentSavedalbumBinding
    lateinit var albumDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedalbumBinding.inflate(inflater, container, false)

        albumDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecylerView()
    }

    private fun initRecylerView() {
        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // 리스너 객체 생성 및 전달
        val albumRVAdapter = AlbumLockerRVAdapter()

        albumRVAdapter.setMyItemClickListener(object: AlbumLockerRVAdapter.MyItemClickListener{
            override fun onRemoveSong(albumId: Int) {
                //albumDB.albumDao().getLikedAlbums(getJwt())
                albumDB.albumDao().disLikedAlbum(getJwt(), albumId)
            }
        })
        binding.lockerSavedSongRecyclerView.adapter = albumRVAdapter
        albumRVAdapter.addAlbums(albumDB.albumDao().getLikedAlbums(getJwt()) as ArrayList)
    }

    // 로그인 및 로그아웃 확인
    private fun getJwt() : Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE) // activity?: fragment에서 사용하는 방법
        val jwt = spf!!.getInt("jwt", 0)
        Log.d("MAIN_ACT/GET_JWT", "jwt_token: $jwt")

        return jwt
    }
}