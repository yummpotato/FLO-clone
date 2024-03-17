package com.example.roomdb

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.roomdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var list = ArrayList<Profile>()
    lateinit var customAdapter: CustomAdapter
    lateinit var db: ProfileDatabase

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ProfileDatabase.getInstance(this@MainActivity)!!

        Thread{
            val savedContacts = db.profileDao().getAll()
            if(savedContacts.isNotEmpty()) {
                list.addAll(savedContacts)
            }
        }.start()

        binding.button.setOnClickListener {
            Thread{
                list.add(Profile("박감자", "24","010-2000-1026"))
                db.profileDao().insert((Profile("박감자", "24", "010-2000-1026")))

                val list = db.profileDao().getAll()
                Log.d("Inserted Primary Key", list[list.size - 1].id.toString())
            }.start()
            customAdapter.notifyDataSetChanged()
        }

        customAdapter = CustomAdapter(list, this@MainActivity)
        binding.mainProfileLv.adapter = customAdapter
    }
}