package com.example.playlistmaker

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var pictureSong: ImageView
    private lateinit var songName: TextView
    private lateinit var authorName: TextView
    private lateinit var duration: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country:TextView
    private lateinit var songTime: TextView
    private lateinit var url: String
    private lateinit var play: ImageView
    private var mediaPlayer = MediaPlayer()
    private lateinit var handler: Handler
    private lateinit var timePassedRunnable: Runnable
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 1000L
    }

    private var playerState = STATE_DEFAULT
    private var isPlayerUsed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        backButton = findViewById(R.id.backButton)
        pictureSong = findViewById(R.id.songPicture)
        songName = findViewById(R.id.songName)
        authorName = findViewById(R.id.songAuthor)
        duration = findViewById(R.id.timeDuration)
        album = findViewById(R.id.albumName)
        year = findViewById(R.id.yearNum)
        genre = findViewById(R.id.genreName)
        country = findViewById(R.id.countryName)
        songTime = findViewById(R.id.timeNum)
        play = findViewById(R.id.buttonPlay)
        lateinit var chosenTrack : DataSongs
        val extras: Bundle? = intent.extras
        handler = Handler(Looper.getMainLooper())


        backButton.setOnClickListener {
            finish()
        }
        if(extras != null) {
            val chosenTrackJSON = intent.extras?.getString(CHOSEN_TRACK)
            chosenTrack = Gson().fromJson(chosenTrackJSON, DataSongs::class.java)
        }

        val radius = resources.getDimensionPixelSize(R.dimen.corner_radius_small)
        Glide.with(applicationContext)
            .load(chosenTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(pictureSong)
        songName.text = chosenTrack.trackName
        authorName.text = chosenTrack.artistName
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(chosenTrack.trackTimeMillis)
        album.text = chosenTrack.collectionName
        year.text = chosenTrack.releaseDate.take(4)
        genre.text = chosenTrack.primaryGenreName
        country.text = chosenTrack.country
        url = chosenTrack.previewUrl


        play.setOnClickListener {
            playbackControl()
        }

        preparePlayer()
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
        if(isPlayerUsed){
            handler.removeCallbacks(timePassedRunnable)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        if(isPlayerUsed){
            handler.removeCallbacks(timePassedRunnable)
        }
    }
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
            if(isPlayerUsed){
                handler.removeCallbacks(timePassedRunnable)
            }
            songTime.text = getString(R.string.time)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            play.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play, theme))
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        play.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.pause, theme))
        if(!isPlayerUsed){
            timePassedRunnable = createTimePassedTask()
        }
        handler.post(timePassedRunnable)
        isPlayerUsed = true
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        play.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play, theme))
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    private fun createTimePassedTask(): Runnable {
        return object : Runnable {
            override fun run() {
                songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                handler.postDelayed(this, DELAY)
            }
        }
    }
}


