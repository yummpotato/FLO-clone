package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.week1.HomeFragment
import com.example.week1.MainActivity
import com.example.week1.R
import com.example.week1.databinding.FragmentAlbumBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {

    lateinit var binding: FragmentAlbumBinding
    private var gson: Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상")

    private var isLiked: Boolean = false // 좋아요 여부

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)

        val albumJson = arguments?.getString("album")
        val album = gson.fromJson(albumJson, Album::class.java)
        isLiked = isLikedAlbum(album.id) // isLiked 초기 설정
        seInit(album)
        setOnClickListeners(album)

        binding.albumBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        val albumAdapter = AlbumVPAdapter(this@AlbumFragment, album)
        binding.albumContentVp.adapter = albumAdapter

        // TabLayout을 ViewPager2와 연결해주는 중재자
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) { tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    private fun seInit(album: Album) {
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()
        if(isLiked){
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    // f로그인한 사용자 정보 가져오기
    private fun getJwt() : Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE) // activity?: fragment에서 사용하는 방법
        return spf!!.getInt("jwt", 0) // spf에서 가져온 값이 없다면 0 반환
    }

    // 앨범에 좋아요 눌렀을 때, DB에 저장해주는 함수
    private fun likeAlbum(userId: Int, albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId, albumId)

        // DB에 데이터 저장
        songDB.albumDao().likeAlbum(like)
    }

    // 사용자가 좋아요를 눌렀는 지 안 눌렀는 지 확인
    private fun isLikedAlbum(albumId: Int): Boolean {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null // 사용자가 앨범을 좋아요하면 likeId가 null이 아니면 리턴값은 true, 아닐 때는 false
    }

    // 사용자가 좋아요 해제하면 DB에서 삭제
    private fun disLikedAlbum(albumId: Int) {
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        songDB.albumDao().isLikedAlbum(userId, albumId)
    }

    // 좋아요 눌렀을 때 클릭 처리
    private fun setOnClickListeners(album: Album) {
        var userId = getJwt()
        binding.albumLikeIv.setOnClickListener {
            if(isLiked) { // 좋아요 취소할 때 (좋아요 두 번 클릭 -> 이미 눌러져 있는 데 다시 누르는 것)
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(album.id)
            } else {
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
        }
    }
}