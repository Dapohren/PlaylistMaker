package com.example.playlistmaker.media.presentation.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.media.presentation.FavoritesSongsViewHolder
import com.example.playlistmaker.media.presentation.FavouriteSongsAdapter

class PlaylistAdapter() : RecyclerView.Adapter<PlaylistViewHolder>() {
    var playlist = emptyList<PlaylistModel>()
    private var trackListener : onTrackClickListener? = null
    interface onTrackClickListener {
        fun onClicked(position: Int)
    }

    fun setOnTrackClickListener(listener: onTrackClickListener){
        trackListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media_view, parent, false)
        return (PlaylistViewHolder(view, trackListener))
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlist[position])
    }
}