package com.example.flo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.week1.R
import com.example.week1.Song
import com.example.week1.databinding.BottomsheetBinding
import com.example.week1.databinding.FragmentSavesongBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class SavesongFragment : Fragment() {
    lateinit var binding: FragmentSavesongBinding

    private var songDatas = ArrayList<Song>()
    lateinit var savesongAdapter: SavesongRVAdapter
    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavesongBinding.inflate(inflater, container, false)
//
        songDB = SongDatabase.getInstance(requireContext())!!
        initRecyclerView()

        binding.savesongAllChoice.setOnClickListener {
            setAll(true)
            showBottomSheet()
        }

        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    private fun setAll(choice: Boolean) {
        if (choice) {
            binding.savesongAllChoice.text = "선택해제"
            binding.savesongAllChoice.setTextColor(resources.getColor(R.color.select_color))
            binding.savesongAllChoiceIv.setImageResource(R.drawable.btn_playlist_select_on)
            binding.savesongSaveAlbumRv.setBackgroundColor(resources.getColor(R.color.light_gray))
            savesongAdapter.choice = true

            savesongAdapter.notifyDataSetChanged()
        } else {
            binding.savesongAllChoice.text = "전체선택"
            binding.savesongAllChoice.setTextColor(R.color.black)
            binding.savesongAllChoiceIv.setImageResource(R.drawable.btn_playlist_select_off)
            binding.savesongSaveAlbumRv.setBackgroundColor(resources.getColor(R.color.white))
            savesongAdapter.choice = false

            savesongAdapter.notifyDataSetChanged()
        }
    }


    override fun onStart() {
        super.onStart()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        savesongAdapter = SavesongRVAdapter(requireContext())
        binding.savesongSaveAlbumRv.adapter = savesongAdapter
        binding.savesongSaveAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        savesongAdapter.addSongs(songDB.songDao().getLikedSongs(true))
        savesongAdapter.setMyItemClickListener(object : SavesongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false, songId)
            }
        })
    }

    private fun showBottomSheet() {
        val view = layoutInflater.inflate(R.layout.bottomsheet, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())

        view.findViewById<LinearLayout>(R.id.bs_play_lo).setOnClickListener {
            setAll(false)
            bottomSheetDialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.bs_list_lo).setOnClickListener {
            setAll(false)
            bottomSheetDialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.bs_mine_lo).setOnClickListener {
            setAll(false)
            bottomSheetDialog.dismiss()
        }

        view.findViewById<LinearLayout>(R.id.bs_del_lo).setOnClickListener {
            savesongAdapter.removeAll()
            setAll(false)
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setOnCancelListener {
            setAll(false)
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.window?.setGravity(Gravity.CENTER)
        bottomSheetDialog.show()
    }
}