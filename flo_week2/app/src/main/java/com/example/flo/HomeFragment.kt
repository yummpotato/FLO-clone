package com.example.week1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.Album
import com.example.flo.AlbumFragment
import com.example.flo.AlbumRVAdapter
import com.example.flo.BannerFragment
import com.example.flo.BannerVPAdapter
import com.example.flo.HomeVPAdapter
import com.example.flo.SongDatabase
import com.example.flo.songInterface
import com.example.week1.databinding.FragmentHomeBinding
import com.google.gson.Gson
import me.relex.circleindicator.CircleIndicator3


class HomeFragment : Fragment(), songInterface {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas: ArrayList<Album> = ArrayList()
    lateinit var songDB: SongDatabase
    lateinit var indicator: CircleIndicator3
    lateinit var viewPager: ViewPager2
    var currentPosition = 0
    val handler = Handler(Looper.getMainLooper()) {
        setPage()
        true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val homeAdapter = HomeVPAdapter(this@HomeFragment)
        binding.homePannelVp.adapter = homeAdapter
        indicator = binding.indicator
        viewPager = binding.homeBannerVp

        albumDatas = inputDummyAlbums()
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMusicAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeFragments(album)
            }

            override fun onPlayClick(album: Album) {
                albumSong(album)
            }
        })


        var bannerAdapter = BannerVPAdapter(this@HomeFragment)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        indicator.setViewPager(binding.homePannelVp)

        val thread = Thread(PageRunnable())
        thread.start()
        return binding.root
    }

    fun setPage() {
        if (currentPosition == 3) currentPosition = 0
        binding.homePannelVp.setCurrentItem(currentPosition, true)
        currentPosition += 1
    }

    inner class PageRunnable : Runnable {
        override fun run() {
            while (true) {
                Thread.sleep(3000)
                handler.sendEmptyMessage(0)
            }
        }
    }

    private fun changeFragments(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                    putInt("albumId", album.id) // Album의 id를 SongFragment로 전달
                }
            })
            .commitAllowingStateLoss()
    }

    override fun albumSong(album: Album) {
        songDB = SongDatabase.getInstance(requireContext())!!
        val playList = songDB.songDao().getAlbumSongs(album.id)
        (activity as MainActivity).releseMedia()
        (activity as MainActivity).initAlbumMusic(playList as ArrayList<Song>)
        (activity as MainActivity).setMiniPlayerStatus(true)
    }

    private fun inputDummyAlbums(): ArrayList<Album> {
        songDB = SongDatabase.getInstance(requireContext())!!
        var datas = songDB.albumDao().getAlbums()

        if(!datas.isEmpty()) {
            return (datas as ArrayList<Album>)
        }

        songDB.albumDao().apply {
            insert(Album(1, "Candy", "NCT DREAM", R.drawable.img_album_exp2))
            insert(Album(2, "Crescendo", "악동뮤지션", R.drawable.img_album_exp17))
            insert(Album(3, "In Bloom", "ZEROBASEONE", R.drawable.img_album_exp16))
        }
        datas = songDB.albumDao().getAlbums()
        return (datas as ArrayList<Album>)
    }
}