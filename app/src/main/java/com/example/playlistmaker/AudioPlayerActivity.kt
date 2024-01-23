package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var songTime: TextView
    private lateinit var duration: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        backButton = findViewById(R.id.backButton)
        pictureSong = findViewById(R.id.songPicture)
        songName = findViewById(R.id.songName)
        authorName = findViewById(R.id.songAuthor)
        songTime = findViewById(R.id.timeNum)
        duration = findViewById(R.id.timeDuration)
        album = findViewById(R.id.albumName)
        year = findViewById(R.id.yearNum)
        genre = findViewById(R.id.genreName)
        country = findViewById(R.id.countryName)
        lateinit var chosenTrack : DataSongs

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backButton.setOnClickListener {
            finish()
        }
        val chosenTrackJSON = intent.extras?.getString("chosen_track")
        chosenTrack = Gson().fromJson(chosenTrackJSON, DataSongs::class.java)

        val radius = resources.getDimensionPixelSize(R.dimen.corner_radius_small)
        Glide.with(applicationContext)
            .load(chosenTrack.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(pictureSong)
        songName.text = chosenTrack.trackName
        authorName.text = chosenTrack.artistName
        songTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(chosenTrack.trackTimeMillis)
        album.text = chosenTrack.collectionName
        year.text = chosenTrack.releaseDate
        genre.text = chosenTrack.primaryGenreName
        country.text = chosenTrack.country
        duration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(chosenTrack.trackTimeMillis)

    }
}

