package com.example.flo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.week1.R
import com.example.week1.SongActivity
import kotlin.concurrent.thread

class Foreground : Service() {
    private val CHANNEL_ID = "PHJ1026"
    private val NOTI_ID = 1026
    private val PROGRESS_MAX = 100
    private val progressIncrement = 1
    private var notificationManager: NotificationManager? = null

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(CHANNEL_ID, "FOREGROUND", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        val notification = createNotification(0)
        startForeground(NOTI_ID, notification)

        thread(start = true) {
            var progress = 0
            while(progress < PROGRESS_MAX) {
                progress += progressIncrement
                val notification = createNotification(progress)
                notificationManager?.notify(NOTI_ID, notification)
                Thread.sleep(1000)
            }
            stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotification(progress: Int): Notification {
        val intent = Intent(this@Foreground, SongActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@Foreground, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this@Foreground, CHANNEL_ID)
            .setContentTitle("FLO")
            .setSmallIcon(R.drawable.ic_my_like_on)
            .setContentText("FLO가 실행 중 입니다.")
            .setContentIntent(pendingIntent)
            .setProgress(PROGRESS_MAX, progress, false)
            .build()
    }

    fun runBackground() {
        thread(start = true) {
            for(i in 0..100) {
                Thread.sleep(1000)
                Log.d("FLO - Foreground Service", "Count ===> $i")
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return Binder()
    }
}