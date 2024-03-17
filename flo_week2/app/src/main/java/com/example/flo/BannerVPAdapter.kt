package com.example.flo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment:Fragment): FragmentStateAdapter(fragment) {

    private val fragmentlist : ArrayList<Fragment> = ArrayList()

    // 클래스에서 연결된 뷰페이저에게 데이터를 전달할 때 몇 개를 전달할 지 쓰는 메소드
    override fun getItemCount(): Int = fragmentlist.size

    // fragmentlist 안의 item(fragment)들을 생성하는 메소드
    override fun createFragment(position: Int): Fragment = fragmentlist[position]

    fun addFragment(fragment: Fragment) {
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size - 1) // list안에 새로운 데이터가 추가되었을 때, 뷰페이저에게 알려주는 메소드 호출
    }
}