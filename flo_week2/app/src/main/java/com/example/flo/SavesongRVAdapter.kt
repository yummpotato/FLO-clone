package com.example.flo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.R
import com.example.week1.Song
import com.example.week1.databinding.ItemSavesongBinding
import com.example.week1.databinding.ItemSongBinding

class SavesongRVAdapter(var context: Context): RecyclerView.Adapter<SavesongRVAdapter.ViewHolder>() {
    private val songDatas: ArrayList<Song> = ArrayList()
    var choice: Boolean = false

    interface MyItemClickListener {
        fun onRemoveSong(songId: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SavesongRVAdapter.ViewHolder {
        val binding: ItemSavesongBinding = ItemSavesongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: SavesongRVAdapter.ViewHolder, position: Int) {
        holder.bind(songDatas[position])
        holder.binding.itemLockerPlayMoreIv.setOnClickListener {
            mItemClickListener.onRemoveSong(songDatas[position].id)
            removeSong(position)
        }

        holder.binding.itemLockerPlayImgIv.setOnClickListener {
            songDatas[position].isPlaying = true
            holder.binding.itemLockerPauseImgIv.visibility = View.VISIBLE
            holder.binding.itemLockerPlayImgIv.visibility = View.GONE
        }

        holder.binding.itemLockerPauseImgIv.setOnClickListener {
            songDatas[position].isPlaying = false
            holder.binding.itemLockerPauseImgIv.visibility = View.GONE
            holder.binding.itemLockerPlayImgIv.visibility = View.VISIBLE
        }

        if(choice) {
            holder.binding.itemLockerPlayImgIv.setBackgroundColor(R.color.gray_color)
            holder.binding.itemLockerPlayMoreIv.setBackgroundColor(R.color.gray_color)
        } else {
            holder.binding.itemLockerPlayImgIv.setBackgroundColor(R.color.white)
            holder.binding.itemLockerPlayMoreIv.setBackgroundColor(R.color.white)
        }
    }

    override fun getItemCount(): Int = songDatas.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: List<Song>) {
        songDatas.clear()
        songDatas.addAll(songs)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeSong(position: Int) {
        songDatas.removeAt(position)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAll() {
        songDatas.clear()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSavesongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.itemLockerCoverImgIv.setImageResource(song.albumImg!!)
            binding.itemLockerTitleTv.text = song.title
            binding.itemLockerSingerTv.text = song.singer
        }
    }
}