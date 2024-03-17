package com.example.week1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.week1.databinding.FragmentLookBinding

class LookFragment : Fragment() {

    lateinit var binding: FragmentLookBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLookBinding.inflate(inflater, container, false)

        // 차트 버튼 눌렀을 때, 차트 부분으로 스트롤 이동
        binding.lookBtn1Lo.setOnClickListener {
            binding.lookContentSv.scrollTo(0, binding.lookBtn1ContentLo.scrollY)
        }

        return binding.root
    }
}