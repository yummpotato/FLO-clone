package com.example.thread

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.thread.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val imageList = arrayListOf<Int>()

        var handler = Handler(Looper.getMainLooper())

        imageList.add(R.drawable.img_bear)
        imageList.add(R.drawable.img_monkey)
        imageList.add(R.drawable.img_frog)
        imageList.add(R.drawable.img_bear)
        imageList.add(R.drawable.img_monkey)
        imageList.add(R.drawable.img_frog)
        imageList.add(R.drawable.img_bear)
        imageList.add(R.drawable.img_monkey)
        imageList.add(R.drawable.img_frog)

        Thread{
            while(true) {
                for(image in imageList) {
                    handler.post {
                        binding.iv.setImageResource(image)
                    }
                    Thread.sleep(2000)
                }
            }

        }.start()

//        val a = A()
//        val b = B()
//
//        a.start()
//        a.join()
//        b.start()
    }

//    class A : Thread() {
//        override fun run() {
//            super.run()
//            for(i in 1..1000) {
//                Log.d("test", "first: $i")
//            }
//        }
//    }
//
//    class B : Thread() {
//        override fun run() {
//            super.run()
//            for(i in 1000 downTo 1) {
//                Log.d("test", "second: $i")
//            }
//        }
//    }
}