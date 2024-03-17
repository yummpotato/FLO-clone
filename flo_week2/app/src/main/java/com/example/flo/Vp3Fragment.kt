package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.week1.databinding.FragmentVp3Binding

class Vp3Fragment : Fragment() {
    lateinit var binding: FragmentVp3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVp3Binding.inflate(inflater, container, false)

        return binding.root
    }
}