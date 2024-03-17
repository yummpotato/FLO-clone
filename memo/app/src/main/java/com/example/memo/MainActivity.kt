package com.example.memo

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.memo.databinding.ActivityMainBinding

var text : String = ""
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBtn.setOnClickListener {
            var data = binding.mainEt.text.toString()
            var intent = Intent(this@MainActivity, SecondActivity::class.java)
            intent.putExtra("text", data)

            startActivity(intent)
        }
        Log.d("MainActivity", "onCreate()")
    }

    override fun onPause() {
        super.onPause()
        text = binding.mainEt.text.toString()

        Log.d("MainActivity", "onPause()")
    }

    override fun onResume() {
        super.onResume()
        var textResume = binding.mainEt.text.toString()
        text = textResume
        Log.d("MainActivity", "onResume()")
    }

    override fun onRestart() {
        super.onRestart()

        if(binding.mainEt.text.toString() == "") {
        } else {
            var builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("Memo")
            builder.setMessage("이어서 작성하시겠습니까?")
            builder.setPositiveButton("CANCEL", DialogInterface.OnClickListener{ dialog, id ->
                binding.mainEt.setText(null)
            })
            builder.setNegativeButton("YES", DialogInterface.OnClickListener{ dialog, id ->

            })
            builder.create()
            builder.show()
        }
        Log.d("MainActivity", "onRestart()")
    }
}