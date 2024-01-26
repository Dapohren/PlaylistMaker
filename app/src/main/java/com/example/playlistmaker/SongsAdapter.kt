package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SongsAdapter() : RecyclerView.Adapter<SongsViewHolder>() {
    var track = ArrayList<DataSongs>()
    private var trackListener : TrackOnClickListener? = null
    fun onTrackClickListener(listener: TrackOnClickListener) {
        trackListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_view, parent, false)
        return (SongsViewHolder(view, trackListener))
    }

    override fun onBindViewHolder(holder: SongsViewHolder, position: Int) {
        holder.bind(track[position])

    }

    override fun getItemCount(): Int {
        return track.size
    }
}