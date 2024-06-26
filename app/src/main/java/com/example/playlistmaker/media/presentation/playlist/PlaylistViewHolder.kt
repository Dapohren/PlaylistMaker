package com.example.playlistmaker.media.presentation.playlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.media.presentation.FavouriteSongsAdapter
import com.example.playlistmaker.search.presentation.SongsAdapter
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewHolder(itemView: View, listener: PlaylistAdapter.onTrackClickListener?) :
    RecyclerView.ViewHolder(itemView) {
    private var playlistImage: ImageView = itemView.findViewById(R.id.playlist_photo)
    private var playlistName: TextView = itemView.findViewById(R.id.playlist_name)
    private var numberTracks: TextView = itemView.findViewById(R.id.tracks_count)


    init{
        itemView.setOnClickListener {
            listener?.onClicked(adapterPosition)
        }
    }



    fun bind(item: PlaylistModel) {
        playlistName.text = item.playlistName
        val trackCount = "${item.addedTracksNumber} ${itemCounter(item.addedTracksNumber)}"
        
        val trackWordEnding = itemView.resources.getQuantityString(R.plurals.plurals_1, item.addedTracksNumber, item.addedTracksNumber);
        numberTracks.text = trackCount
        val radius = itemView.resources.getDimensionPixelSize(R.dimen.dp_8)
        Glide.with(itemView)
            .load(item.playlistImage)
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(playlistImage)


    }

    private fun itemCounter(countTrack: Int) : String {
        val num = countTrack % 100
        return when {
            num in 10..20 -> "треков"
            num % 10 == 1 -> "трек"
            num % 10 in 2..4 -> "трека"
            else -> "треков"
        }
    }

}