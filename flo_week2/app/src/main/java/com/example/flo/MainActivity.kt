package com.example.week1

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.Album
import com.example.flo.SongDatabase
import com.example.week1.databinding.ActivityMainBinding
import com.google.gson.Gson

open class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    //private var song: Song = Song()
    private var gson: Gson = Gson()
    var songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null
    var nowPos = 0

    private fun setResultNext() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // 서브 액티비티로부터 돌아올 때의 결과 값을 받아 올 수 있는 구문
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(
                    this@MainActivity,
                    result.data?.getStringExtra("title"),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        songDB = SongDatabase.getInstance(this@MainActivity)!!

        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()

            val intent = Intent(this@MainActivity, SongActivity::class.java)
            startActivity(intent)
        }
        initMiniPlayerClickListener()
        initBottomNavigation()
        Log.d("MainActivity", "onCreate()")
    }

    private fun initMiniPlayerClickListener() {
        binding.mainMiniplayerPreviousBtn.setOnClickListener {
            moveSong(-1)
        }

        binding.mainMiniplayerNextBtn.setOnClickListener {
            moveSong(+1)
        }

        binding.mainMiniplayerBtn.setOnClickListener {
            setMiniPlayerStatus(true)
        }

        binding.mainMiniplayerPauseBtn.setOnClickListener {
            setMiniPlayerStatus(false)
        }
    }

    private fun resetSong() {
        binding.mainMiniplayerPauseBtn.visibility = View.GONE
        binding.mainMiniplayerBtn.visibility = View.VISIBLE
        mediaPlayer?.reset()
        val pop = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this@MainActivity, pop)
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = songs[nowPos].second
        private var mills: Float = (second * 1000).toFloat()

        private fun reset() {
            second = 0
            mills = 0f
            runOnUiThread {
                if (songs[nowPos].isPlaying == false) {
                    setMiniPlayerStatus(true)
                    songs[nowPos].isPlaying = true
//                    mediaPlayer?.start()
                    startTimer()
                    timer?.isPlaying = true
                }
                else {
                    songs[nowPos].isPlaying = false
                    resetSong()
                    timer?.isPlaying = false
                }
            }
        }

        override fun run() {
            super.run()

            try {
                while (true) {
                    if (second >= playTime) {
                        reset()
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.mainProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }

    private fun moveSong(direct: Int) {
        if (nowPos + direct < 0) {
            Toast.makeText(this@MainActivity, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this@MainActivity, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        songs[nowPos].second = 0
//        saveMiniPlayerSong(songs[nowPos])
//        releseMedia()

        nowPos += direct
        mediaPlayer?.release()
        mediaPlayer = null

        mediaPlayer?.reset()
        val pop = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this@MainActivity, pop)
        setMiniPlayer(songs[nowPos])

        if (songs[nowPos].isPlaying) {
            setMiniPlayerStatus(true)
        } else {
            setMiniPlayerStatus(false)
        }
    }

    private fun saveMiniPlayerSong(song: Song) {
        songDB = SongDatabase.getInstance(this@MainActivity)!!
        songDB.songDao().update(song)
        Log.d("MainActivity", "saveMiniPlayerSong()")
    }

    private fun initBottomNavigation() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    fun setSong(album: Album) {
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        var music = album.title!!.lowercase()
        music = music.replace(" ", "")
        songs[nowPos] = Song(
            album.title!!,
            album.singer!!,
            0,
            15,
            false,
            "candy",
            album.coverImg!!,
            false,
            album.id
        )
        val songJson = gson.toJson(songs[nowPos])
        editor.putString("songData", songJson)
        editor.apply()
        binding.mainMiniplayerTitleTv.text = songs[nowPos].title
        binding.mainMiniplayerSingerTv.text = songs[nowPos].singer
        binding.mainProgressSb.progress = 0

    }

    private fun resetMusic() {
        setMiniPlayerStatus(false)
        val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this@MainActivity, music)
            Log.d("MainActivity", "mediaPlayer == null")
        }

        if (songs[nowPos].second != 0) {
            mediaPlayer!!.seekTo(songs[nowPos].second * 1000)
            Log.d("MainActivity", "songs[nowPos].second != 0")
        }

        startTimer()

        Log.d("MainActivity", "resetMusic()")
    }

    private fun setMiniPlayer(song: Song) {
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second * 100000) / song.playTime

        resetMusic()
        Log.d("MainActivity", "setMiniPlayer()")
    }

    fun setMiniPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer?.isPlaying = isPlaying

        if (isPlaying) {
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainMiniplayerPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainMiniplayerPauseBtn.visibility = View.GONE

            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        songs.clear()
        inputDummySongs()
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songSecond = spf.getInt("songSecond", 0)

        nowPos = getPlayingSongPosition(songId)
        songs[nowPos].second = songSecond
        binding.mainProgressSb.progress = (songs[nowPos].second * 1000 / songs[nowPos].playTime)
        Log.d("now Song ID", songs[nowPos].id.toString())
        Log.d("now Song Name", songs[nowPos].title)
        startTimer()
        setMiniPlayer(songs[nowPos])
        Log.d("MainActivity", "onStart()")
    }

    fun initAlbumMusic(playList: ArrayList<Song>) {
        songs.clear()
        songs.addAll(playList)
        nowPos = 0
        setMiniPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    private fun inputDummySongs() {
        val songDB = SongDatabase.getInstance(this@MainActivity)!!
        songs.addAll(songDB.songDao().getSongs())
        if (songs.isNotEmpty()) {
            return
        } else {
            songDB.songDao().insert(
                Song(
                    "Candy",
                    "NCT DREAM",
                    0,
                    15,
                    false,
                    "candy",
                    R.drawable.img_album_exp2,
                    false,
                    1
                )
            )

            songDB.songDao().insert(
                Song(
                    "Crescendo",
                    "악동뮤지션",
                    0,
                    15,
                    false,
                    "crescendo",
                    R.drawable.img_album_exp15,
                    false,
                    2
                )
            )

            songDB.songDao().insert(
                Song(
                    "Good Night",
                    "ZEROBASEONE",
                    0,
                    15,
                    false,
                    "goodnight",
                    R.drawable.img_album_exp16,
                    false,
                    3
                )
            )

            songDB.songDao().insert(
                Song(
                    "In Bloom",
                    "ZEROBASEONE",
                    0,
                    15,
                    false,
                    "inbloom",
                    R.drawable.img_album_exp16,
                    false,
                    3
                )
            )

            songDB.songDao().insert(
                Song(
                    "사람들이 움직이는게",
                    "악동뮤지션",
                    0,
                    15,
                    false,
                    "peoplemoving",
                    R.drawable.img_album_exp17,
                    false,
                    2
                )
            )

            songDB.songDao().insert(
                Song(
                    "발자국",
                    "NCT DREAM",
                    0,
                    15,
                    false,
                    "candy",
                    R.drawable.img_album_exp2,
                    false,
                    1
                )
            )

            songDB.songDao().insert(
                Song(
                    "입김",
                    "NCT DREAM",
                    0,
                    15,
                    false,
                    "candy",
                    R.drawable.img_album_exp2,
                    false,
                    1
                )
            )
        }

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer!!.start()
        Log.d("MainActivity", "startTimer()")
    }

    fun releseMedia() {
        timer?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("MainActivity", "onDestroy()")
    }

    override fun onPause() {
        super.onPause()
        songs[nowPos].second =
            ((binding.mainProgressSb.progress * songs[nowPos].playTime) / 100) / 1000
        songDB.songDao().update(songs[nowPos])

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()

        timer?.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("MainActivity", "onPause()")
    }
}
