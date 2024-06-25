package com.example.playlistmaker.player.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.media.domain.models.FavTracksModel
import com.example.playlistmaker.media.presentation.playlist.PlaylistState
import com.example.playlistmaker.media.ui.playlist.NewPlaylistFragment
import com.example.playlistmaker.player.presentation.AudioPlayerPlaylistState
import com.example.playlistmaker.player.presentation.AudioPlayerState
import com.example.playlistmaker.player.presentation.AudioPlayerViewModel
import com.example.playlistmaker.player.presentation.BottomSheetAdapter
import com.example.playlistmaker.player.presentation.FavouriteState
import com.example.playlistmaker.search.ui.CHOSEN_TRACK
import com.example.playlistmaker.search.domain.models.DataSongs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private var url: String = ""
    private val viewModel: AudioPlayerViewModel by viewModel()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val bottomSheetAdapter: BottomSheetAdapter by lazy { BottomSheetAdapter() }
    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var chosenTrack: DataSongs
    private lateinit var pictureSong: ImageView
    private lateinit var extras: Bundle
    private lateinit var timeNum: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //
        extras = intent.extras!!
        val chosenTrackJSON = intent.extras?.getString(CHOSEN_TRACK)
        chosenTrack = Gson().fromJson(chosenTrackJSON, DataSongs::class.java)

        binding.recyclerViewPlaylists.adapter = bottomSheetAdapter
        binding.recyclerViewPlaylists.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        pictureSong = findViewById(R.id.songPicture)
        timeNum = findViewById(R.id.timeNum)
        val radius = resources.getDimensionPixelSize(R.dimen.corner_radius_small)
        Glide.with(this)
            .load(chosenTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(this.pictureSong)

        binding.songName.text = chosenTrack.trackName
        binding.songAuthor.text = chosenTrack.artistName
        binding.timeDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(chosenTrack.trackTimeMillis)
        binding.albumName.text = chosenTrack.collectionName
        binding.yearNum.text = chosenTrack.releaseDate.take(4)
        binding.genreName.text = chosenTrack.primaryGenreName
        binding.countryName.text = chosenTrack.country
        url = chosenTrack.previewUrl
        viewModel.startPreparingPlayer(url)
        viewModel.isFavouriteClick(chosenTrack)
        viewModel.getPlaylists()
        viewModel.state.observe(this){ state ->
            when (state){
                AudioPlayerState.NotReady -> playButtonAvailability(false)
                AudioPlayerState.Ready -> playButtonAvailability(true)
                AudioPlayerState.OnStart -> {
                    binding.buttonPlay.setImageResource(R.drawable.play)
                    updateTrackTimePassed(getString(R.string.time))

                }
                AudioPlayerState.Pause -> binding.buttonPlay.setImageResource(R.drawable.play)
                is AudioPlayerState.Play -> {
                    binding.buttonPlay.setImageResource(R.drawable.pause)
                    updateTrackTimePassed(state.currentPosition)

                }

                else -> {}
            }

        }



        viewModel.favourites.observe(this) {state ->
            when(state) {
                FavouriteState.Liked -> {
                    binding.buttonLike.setImageResource(R.drawable.liked)
                    binding.buttonLike.setOnClickListener {
                        viewModel.deleteFromFav(chosenTrack)
                    }
                }
                FavouriteState.NotLiked -> {
                    binding.buttonLike.setImageResource(R.drawable.like)
                    binding.buttonLike.setOnClickListener {
                        viewModel.addToFavourite(chosenTrack)
                    }
                }
            }
        }

        viewModel.playlistState.observe(this){ state ->
            when(state){
                is PlaylistState.NotEmpty -> {
                    bottomSheetAdapter.track = state.playlists
                    bottomSheetAdapter.notifyDataSetChanged()
                    binding.recyclerViewPlaylists.visibility = View.VISIBLE
                }
                PlaylistState.Empty -> {
                    binding.recyclerViewPlaylists.visibility = View.GONE
                }
            }
        }

        viewModel.audioPlayerPlaylistState.observe(this){ state ->
            when(state){
                is AudioPlayerPlaylistState.InPlaylist -> {
                    val message = "Трек уже добавлен в плейлист ${state.playlistName}"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                        .show()
                }
                is AudioPlayerPlaylistState.NotInPlaylist -> viewModel.addTrackToPlaylist(state.track, state.playlist)
                is AudioPlayerPlaylistState.AddedToPlaylist -> {
                    val message = "Добавлено в плейлист ${state.playlistName}"
                    Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
                        .show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.getPlaylists()
                }
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }


        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.buttonAdd.setOnClickListener{
            binding.overlay.visibility = View.VISIBLE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.buttonNewPlaylist.setOnClickListener{
            val fragmentManager = supportFragmentManager
            val fragment = NewPlaylistFragment()
            fragmentManager.beginTransaction().add(R.id.rootFragmentContainerView, fragment).commit()
            binding.rootFragmentContainerView.visibility = View.VISIBLE
        }

        bottomSheetAdapter.setOnTrackClickListener(object: BottomSheetAdapter.onTrackClickListener {
            override fun onClicked(position: Int) {
                val chosenPlaylist = bottomSheetAdapter.track[position]
                viewModel.stateTrackInPlaylist(chosenTrack, chosenPlaylist)

            }
        })


    }
    private fun updateTrackTimePassed(position: String) {
        timeNum.text = position
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()

    }
    private fun playButtonAvailability(isAvailable: Boolean) {
        binding.buttonPlay.isEnabled = isAvailable
    }


}






