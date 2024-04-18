package com.example.playlistmaker.player.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.presentation.AudioPlayerState
import com.example.playlistmaker.player.presentation.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.CHOSEN_TRACK
import com.example.playlistmaker.search.domain.models.DataSongs
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private var url: String = ""
    private val viewModel: AudioPlayerViewModel by viewModel()
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

        extras = intent.extras!!
        val chosenTrackJSON = intent.extras?.getString(CHOSEN_TRACK)
        chosenTrack = Gson().fromJson(chosenTrackJSON, DataSongs::class.java)

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
        viewModel.startPlayer(url)

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
        binding.backButton.setOnClickListener {
            finish()
        }


        binding.buttonPlay.setOnClickListener {
            viewModel.playbackControl()
        }


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






