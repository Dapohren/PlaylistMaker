package com.example.playlistmaker.player.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.PlaylistModel

class BottomSheetAdapter : RecyclerView.Adapter<BottomSheetViewHolder>() {
    var track = emptyList<PlaylistModel>()
    private var trackListener : onTrackClickListener? = null
    interface onTrackClickListener {
        fun onClicked(position: Int)
    }

    fun setOnTrackClickListener(listener: onTrackClickListener){
        trackListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bottom_sheet_view, parent, false)
        return (BottomSheetViewHolder(view, trackListener))
    }

    override fun getItemCount(): Int {
        return track.size
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        holder.bind(track[position])
    }

}