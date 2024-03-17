package com.example.week1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.AlbumVPAdapter
import com.example.flo.LockerVPAdapter
import com.example.flo.LoginActivity
import com.example.week1.databinding.FragmentLockerBinding
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    private val information = arrayListOf("저장한 곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this@LockerFragment)
        binding.lockerContentVp.adapter = lockerAdapter

        // TabLayout을 ViewPager2와 연결해주는 중재자
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) {
                tab, position ->
            tab.text = information[position]
        }.attach()

        binding.lockerLoginTv.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    // 로그인 및 로그아웃 확인
    private fun getJwt() : Int {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE) // activity?: fragment에서 사용하는 방법
        return spf!!.getInt("jwt", 0) // spf에서 가져온 값이 없다면 0 반환
    }

    // view의 text를 login으로 할 지, logout으로 할 지 결정
    private fun initView() {
        val jwt : Int = getJwt()
        if(jwt == 0) {
            binding.lockerLoginTv.text = "로그인"
            binding.lockerLoginTv.setOnClickListener {
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        } else {
            binding.lockerLoginTv.text = "로그아웃"
            binding.lockerLoginTv.setOnClickListener {
                logout()
                // 로그아웃 시, 메인 화면으로 이동
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }

    private fun logout() {
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)
        val editor = spf!!.edit()
        editor.remove("jwt") // jwt라는 키 값에 저장된 값 삭제
        editor.apply()
    }
}