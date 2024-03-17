package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.databinding.FragmentSongBinding
import com.google.gson.Gson


class SongFragment : Fragment() {
    lateinit var binding: FragmentSongBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSongBinding.inflate(inflater, container, false)

        binding.songMixoffTg.setOnClickListener {
            binding.songMixonTg.visibility = View.VISIBLE
            binding.songMixoffTg.visibility = View.INVISIBLE
        }

        binding.songMixonTg.setOnClickListener {
            binding.songMixonTg.visibility = View.INVISIBLE
            binding.songMixoffTg.visibility = View.VISIBLE
        }

        return binding.root
    }
}