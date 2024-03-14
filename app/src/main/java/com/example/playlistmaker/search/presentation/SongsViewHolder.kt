package com.example.playlistmaker.search.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale


class SongsViewHolder(itemView: View, trackListener: TrackOnClickListener?): RecyclerView.ViewHolder(itemView) {
    private val songName: TextView = itemView.findViewById(R.id.song_name)
    private val artistName: TextView = itemView.findViewById(R.id.song_performer)
    private val songIcon: ImageView = itemView.findViewById(R.id.song_image)
    private val songTime: TextView = itemView.findViewById(R.id.song_time)

    init{
        itemView.setOnClickListener {
            trackListener?.onClicked(adapterPosition)
        }
    }
    fun bind(item: DataSongs) {

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(5))
            .into(songIcon)
        songName.text = item.trackName
        artistName.text = item.artistName
        songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)

    }
}