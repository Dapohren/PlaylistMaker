package com.example.playlistmaker.media.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.SongsAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class FavoritesSongsViewHolder(itemView: View, listener: FavouriteSongsAdapter.onTrackClickListener?) : RecyclerView.ViewHolder(itemView) {
    private val songName: TextView = itemView.findViewById(R.id.song_name)
    private val artistName: TextView = itemView.findViewById(R.id.song_performer)
    private val songIcon: ImageView = itemView.findViewById(R.id.song_image)
    private val songTime: TextView = itemView.findViewById(R.id.song_time)

    init{
        itemView.setOnClickListener {
            listener?.onClicked(adapterPosition)
        }
    }
    fun bind(item: FavTracksModel) {

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