package com.example.playlistmaker.media.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.SongsAdapter
import com.example.playlistmaker.search.presentation.SongsViewHolder

class FavouriteSongsAdapter() : RecyclerView.Adapter<FavoritesSongsViewHolder>() {
    var track = emptyList<FavTracksModel>()
    private var trackListener : onTrackClickListener? = null
    interface onTrackClickListener {
        fun onClicked(position: Int)
    }

    fun setOnTrackClickListener(listener: onTrackClickListener){
        trackListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesSongsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_view, parent, false)
        return (FavoritesSongsViewHolder(view, trackListener))
    }

    override fun getItemCount(): Int {
        return track.size
    }

    override fun onBindViewHolder(holder: FavoritesSongsViewHolder, position: Int) {
        holder.bind(track[position])
    }

}