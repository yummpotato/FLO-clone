package com.example.week1

import android.content.Intent
import android.graphics.Point
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flo.Foreground
import com.example.flo.SongDatabase
import com.example.week1.databinding.ActivitySongBinding
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    //전역 변수
    lateinit var binding: ActivitySongBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()

        Log.d("SongActivity", "onCreate()")
    }

    private fun initClickListener() {
        binding.songDownIb.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", songs[nowPos].id)
            editor.apply()
            var intent = Intent(this@SongActivity, MainActivity::class.java)
            intent.putExtra("title", binding.songMusicTitleTv.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.songMiniplayerIv.setOnClickListener {
            serviceStart(binding.songMiniplayerIv)
            setPlayerStatus(true)
        }

        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }

        binding.songRandomIv.setOnClickListener {
            setRandomPlayerStatus(false)
        }

        binding.songNotRandomIv.setOnClickListener {
            setRandomPlayerStatus(true)
        }

        binding.songRepeatIv.setOnClickListener {
            setRepeatPlayerStatus(1)
        }

        binding.songRepeat1Iv.setOnClickListener {
            setRepeatPlayerStatus(2)
        }

        binding.songNotRepeatIv.setOnClickListener {
            setRepeatPlayerStatus(0)
        }

        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

    private fun serviceStart(view: View) {
        val intent = Intent(this@SongActivity, Foreground::class.java)
        ContextCompat.startForegroundService(this@SongActivity, intent)
    }

    fun serviceStop(view: View) {
        val intent = Intent(this@SongActivity, Foreground::class.java)
        stopService(intent)
    }

    private fun setLike(isLike: Boolean) {
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if(!isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            myToast("좋아요 한 곡에 담겼습니다.")
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            myToast("좋아요 한 곡에서 삭제되었습니다.")
        }
    }

    private fun myToast(message: String) {
        var inflater: LayoutInflater = layoutInflater
        var view: View = inflater.inflate(R.layout.toast_message, findViewById(R.id.toast_lo))
        val _view = windowManager.defaultDisplay
        val size = Point()
        _view.getRealSize(size)
        val height = size.y

        var text = view.findViewById(R.id.toast_message_tv) as TextView
        text.setText(message)
        var toastMessage = Toast.makeText(this@SongActivity, message, Toast.LENGTH_LONG)

        toastMessage.setGravity(Gravity.FILL_HORIZONTAL, 0, (height*0.22).toInt())
        toastMessage.view = view
        toastMessage.show()

    }


    private fun setPlayer(song: Song) {
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.albumImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        binding.songAlbumIv.setImageResource(song.albumImg!!)

        if(song.isLike) {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else {
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        if (mediaPlayer == null) {
            initMusic()
        } else {
            if(song.second != 0) {
                mediaPlayer?.seekTo(song.second * 1000)
            }
            setPlayerStatus(song.isPlaying)
        }
    }

    private fun initSong() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        Log.d("now Song ID", songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun moveSong(direct: Int) {
        if(nowPos + direct < 0) {
            Toast.makeText(this@SongActivity, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos + direct >= songs.size) {
            Toast.makeText(this@SongActivity, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        initMusic()
        setPlayer(songs[nowPos])

        if (songs[nowPos].isPlaying) {
            setPlayerStatus(true)
        }
    }

    private fun getPlayingSongPosition(songId: Int): Int {
        for (i in 0 until songs.size) {
            if(songs[i].id == songId) {
                return i
            }
        }
        return 0
    }

    private fun startTimer() {
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release() // 미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null // 미디어 플레이어 해제

        Log.d("SongActivity", "onDestroy()")
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true) : Thread() {
        private var second: Int = songs[nowPos].second
        private var mills: Float = (second * 1000).toFloat()

        private fun reset() {
            second = 0
            mills = 0f
            initMusic()
        }

        override fun run() {
            super.run()

            try {
                while (true) {
                    if (second >= playTime) {
                        reset()
                        if(binding.songRepeat1Iv.visibility == View.VISIBLE) {
                            runOnUiThread { setPlayerStatus(true) }
                        } else {
                            runOnUiThread { setPlayerStatus(false) }
                        }
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }

    private fun initMusic() {
        mediaPlayer?.reset()
        val pop = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this@SongActivity, pop)
    }

    // 사용자가 포커스를 잃었을 때 음악 중지
    override fun onPause() {
        super.onPause()
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime) / 100) / 1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // 내부 저장소에 데이터 저장 -> 앱 종료되어도 후에 다시 꺼낼 수 있음.
        val editor = sharedPreferences.edit() // 에디터
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("songSecond", songs[nowPos].second)
        editor.apply() // apply를 해야 실제 저장공간에 저장이 된다!
        Log.d("SongActivity", "onPause()")
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) // 내부 저장소에 데이터 저장 -> 앱 종료되어도 후에 다시 꺼낼 수 있음.
        val songJson = sharedPreferences.getString("songData", null)
        if (songJson != null) {
            songs[nowPos] = gson.fromJson(songJson, Song::class.java)
        }

        if(songs[nowPos].second != 0 ) {
            mediaPlayer?.seekTo(songs[nowPos].second * 1000)
        }

        setPlayer(songs[nowPos])
        Log.d("SongActivity", "onResume()")
    }

    private fun initPlayList() {
        songDB = SongDatabase.getInstance(this@SongActivity)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun setPlayerStatus(isPlaying: Boolean) {
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if (isPlaying) {
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()

        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }

    fun setRandomPlayerStatus(isRandom: Boolean) {
        if (isRandom) {
            binding.songRandomIv.visibility = View.VISIBLE
            binding.songNotRandomIv.visibility = View.GONE
        } else {
            binding.songRandomIv.visibility = View.GONE
            binding.songNotRandomIv.visibility = View.VISIBLE
        }
    }

    fun setRepeatPlayerStatus(isRepeat: Int) {
        if (isRepeat == 0) {
            binding.songRepeatIv.visibility = View.VISIBLE
            binding.songNotRepeatIv.visibility = View.GONE
            binding.songRepeat1Iv.visibility = View.GONE
        } else if (isRepeat == 1) {
            binding.songRepeatIv.visibility = View.GONE
            binding.songNotRepeatIv.visibility = View.GONE
            binding.songRepeat1Iv.visibility = View.VISIBLE

        } else {
            binding.songRepeatIv.visibility = View.GONE
            binding.songNotRepeatIv.visibility = View.VISIBLE
            binding.songRepeat1Iv.visibility = View.GONE
        }
    }
}