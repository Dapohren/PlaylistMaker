package com.example.playlistmaker.media.ui

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesSongsBinding
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.media.presentation.FavouriteSongsAdapter
import com.example.playlistmaker.media.presentation.FavouriteSongsViewModel
import com.example.playlistmaker.media.presentation.FavouriteStates
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.DataSongs
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val CLICK_DEBOUNCE_DELAY = 1000L

class FavoritesSongsFragment : Fragment() {
    private val viewModel: FavouriteSongsViewModel by viewModel()
    private var isClickAllowed = true
    private var _binding: FragmentFavoritesSongsBinding? = null
    private val favouriteTracksAdapter = FavouriteSongsAdapter()
    private lateinit var historyList: ArrayList<DataSongs>
    private lateinit var recycle: RecyclerView
    private lateinit var emptyFavImage: ImageView
    private lateinit var emptyFavText: TextView

    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycle = binding.favTracksList
        recycle.adapter = favouriteTracksAdapter
        recycle.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewModel.getFavouriteTracks()
        emptyFavImage = binding.emptyFavImage
        emptyFavText = binding.emptyFavText
        viewModel.state.observe(viewLifecycleOwner) {state ->
            when(state) {
                is FavouriteStates.NotEmpty -> {
                    favouriteTracksAdapter.track = state.tracks
                    favouriteTracksAdapter.notifyDataSetChanged()
                    showFavTracks()
                    hidePlaceHolder()
                }
                FavouriteStates.Empty -> {
                    showPlaceHolder()
                    hideFavTracks()
                }
                else -> showPlaceHolder()

            }

        }

        favouriteTracksAdapter.setOnTrackClickListener(object: FavouriteSongsAdapter.onTrackClickListener{
            override fun onClicked(position: Int) {
                if(clickDebounce()){
                    val chosenTrack = mapTrackDomainFromMediaLibraryToSearch(favouriteTracksAdapter.track[position])
                    addTrackToHistory(chosenTrack)
                    val displayAudioPlayer = Intent(requireContext(), AudioPlayerActivity::class.java)
                    displayAudioPlayer.putExtra("chosen_track", Gson().toJson(chosenTrack))
                    startActivity(displayAudioPlayer)
                }
            }
        }
        )
    }

    private fun mapTrackDomainFromMediaLibraryToSearch(track: FavTracksModel): DataSongs {
        return DataSongs(
            trackId = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    private fun addTrackToHistory(chosenTrack: DataSongs) {
        historyList = viewModel.readFromSharedPreferences()
        if (historyList.size < 10){
            if(historyList.isNotEmpty()){
                if(historyList.contains(chosenTrack)){
                    historyList.remove(chosenTrack)
                }
                historyList.add(0, chosenTrack)
            } else {
                historyList.add(chosenTrack)
            }
        } else {
            if(historyList.contains(chosenTrack)){
                historyList.remove(chosenTrack)
                historyList.add(0, chosenTrack)
            } else {
                for(i in 9 downTo 1){
                    historyList[i] = historyList[i-1]
                }
                historyList[0] = chosenTrack
            }
        }
        viewModel.writeToSharedPreferences(historyList)
    }
    private fun hideFavTracks() {
        recycle.visibility = View.GONE
    }

    private fun hidePlaceHolder() {
        emptyFavImage.visibility = View.GONE
        emptyFavText.visibility = View.GONE
    }

    private fun showFavTracks() {
        recycle.visibility = View.VISIBLE
    }
    private fun showPlaceHolder() {
        emptyFavImage.visibility = View.VISIBLE
        emptyFavText.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavouriteTracks()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

}