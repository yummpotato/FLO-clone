package com.example.memo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memo.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("text")) {
            binding.secondTv.text = intent.getStringExtra("text")!!

            if(binding.secondTv.text == "") {
                Toast.makeText(this@SecondActivity, "작성한 text가 없습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("SecondActivity", "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d("SecondActivity", "onPause()")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("SecondActivity", "onRestart()")
    }
}