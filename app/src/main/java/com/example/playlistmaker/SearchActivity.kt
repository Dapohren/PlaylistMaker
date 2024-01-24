package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

// Константы
const val SONGS_PREFERENCES = "songs_preferences"
const val SONGS_LIST_KEY = "songs_list_key"
const val CHOSEN_TRACK = "chosen_track"

class SearchActivity : AppCompatActivity() {

    //Кнопки истории

    private lateinit var youSearch: TextView
    private lateinit var searchHistory: RecyclerView
    private lateinit var clearHistory: Button
    private lateinit var layoutHistory: LinearLayout
    // кнопки экрана и экрана ошибки

    private lateinit var placeholderMessage: TextView
    private lateinit var editText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var backButton: Button
    private var textString: String = ""
    private lateinit var iconNothingFound: ImageView
    private lateinit var iconNoInternet: ImageView
    private lateinit var buttonRefresh: Button
    private val baseURL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    // инициализация адаптер

    private val imdbService = retrofit.create(iTunesApi::class.java)
    private val songAdapter = SongsAdapter()
    private val songHistoryAdapter = SongsAdapter()
    private lateinit var sharedPreferences : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        editText = findViewById(R.id.search_button2)
        clearIcon = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.back_button2)

        youSearch = findViewById(R.id.text_you_search)
        searchHistory = findViewById(R.id.recycle_history)
        clearHistory = findViewById(R.id.clean_history)
        layoutHistory = findViewById(R.id.layout_history)

        iconNothingFound = findViewById(R.id.placeholder_nothing_f)
        iconNoInternet = findViewById(R.id.placeholder_no_inet)
        buttonRefresh = findViewById(R.id.ref_button)
        placeholderMessage = findViewById(R.id.placeh_text)
        // сохранение
        sharedPreferences = getSharedPreferences(SONGS_PREFERENCES, MODE_PRIVATE)
        songHistoryAdapter.track = readSharedPref(sharedPreferences)


        //Кнопочка назад
        backButton.setOnClickListener {
            finish()
        }

        //Поиск
        buttonRefresh.setOnClickListener {
            search()
        }

        //Отчистить строку
        clearIcon.setOnClickListener {
            editText.text.clear()
            val inputMeth =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMeth?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        val textWatch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButtonVisibility(s?.isNotEmpty() ?: false)
                textString = editText.text.toString()
                layoutHistory.visibility = if (editText.hasFocus() && editText.text.isEmpty() && songHistoryAdapter.track.isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        //
        val recycleView = findViewById<RecyclerView>(R.id.our_recycle)
        editText.addTextChangedListener(textWatch)
        recycleView.adapter = songAdapter
        recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistory.adapter = songHistoryAdapter
        searchHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // нажатие на элемент
        songAdapter.onTrackClickListener(object: TrackOnClickListener{
            override fun onClicked(track: Int) {
                showSearchHistory(track)
                val trackChosen = songAdapter.track[track]
                openChosenTrackActivity(trackChosen)
            }
        })
        songHistoryAdapter.onTrackClickListener(object: TrackOnClickListener{
            override fun onClicked(track: Int) {
                val trackChosen = songAdapter.track[track]
                openChosenTrackActivity(trackChosen)
                songHistoryAdapter.track = readSharedPref(sharedPreferences)
                songHistoryAdapter.track.add(0, trackChosen)
                songHistoryAdapter.track.removeAt(track + 1)
                songHistoryAdapter.notifyDataSetChanged()
                addToList(sharedPreferences, songHistoryAdapter.track)
            }
        })

        editText.setOnFocusChangeListener { _, isFocused ->
            if(isFocused && songHistoryAdapter.track.isNotEmpty() && editText.text.isEmpty()) {
                layoutHistory.visibility = View.VISIBLE
            } else {
                layoutHistory.visibility = View.GONE
            }
        }
        // отчистить историю
        clearHistory.setOnClickListener {
            songHistoryAdapter.track.clear()
            songHistoryAdapter.notifyDataSetChanged()
            addToList(sharedPreferences, songHistoryAdapter.track)
            layoutHistory.visibility = View.GONE
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
    }

    // отчистка поиска
    private fun clearButtonVisibility(isVisible: Boolean) {
        clearIcon.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

   companion object {
      private  const val SEARCH = "TEXT"
    }
    // показать текст ошибки
    private fun showMessage(text: String, additionalMessage: String) {
        if(text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            songAdapter.track.clear()
            songAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if(additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }
    // показать картинку
    private fun showPlaceHolder(text: String)  {
        when(text) {
            getString(R.string.no_conection) -> {
                iconNothingFound.visibility = View.GONE
                iconNoInternet.visibility = View.VISIBLE
                buttonRefresh.visibility = View.VISIBLE
            }
            getString(R.string.nothing_found) -> {
                iconNothingFound.visibility = View.VISIBLE
                iconNoInternet.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
            }
            else -> {
                iconNothingFound.visibility = View.GONE
                iconNoInternet.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
            }

        }
    }
    private fun openChosenTrackActivity(addedSong: DataSongs) {
        val audioPlayer = Intent(this, AudioPlayerActivity::class.java)
        audioPlayer.putExtra(CHOSEN_TRACK, Gson().toJson(addedSong))
        startActivity(audioPlayer)
    }


    // поиск трека
    private fun search() {
        if(editText.text.isNotEmpty()) {
            imdbService.search(editText.text.toString()).enqueue(object : Callback<TrackResponse>{
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if(response.code() == 200) {
                        songAdapter.track.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            songAdapter.track.addAll(response.body()?.results!!)
                            songAdapter.notifyDataSetChanged()
                        }
                        if(songAdapter.track.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                            showPlaceHolder(getString(R.string.nothing_found))
                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(getString(R.string.no_conection), response.code().toString())
                        showPlaceHolder(getString(R.string.no_conection))
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.no_conection), t.message.toString())
                    showPlaceHolder(getString(R.string.no_conection))
                }
            })
        }
    }

    // функции для истории
    // хранение данных
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH, textString)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textString = savedInstanceState.getString(SEARCH).toString()
        editText.setText(textString)
    }
    private fun readSharedPref(sharedPreferences: SharedPreferences?): ArrayList<DataSongs> {
        val songsSh = sharedPreferences?.getString(SONGS_LIST_KEY, null) ?: return ArrayList()
        return Gson().fromJson(songsSh, object : TypeToken<ArrayList<DataSongs>>() {}.type)
    }
    private fun addToList(sharedPreferences: SharedPreferences, songList: ArrayList<DataSongs>) {
        sharedPreferences.edit()
            .putString(SONGS_LIST_KEY, Gson().toJson(songList))
            .apply()
    }
    private fun findSong(position: Int): DataSongs {
        return songAdapter.track[position]
    }
    private fun showSearchHistory(position: Int) {
        songHistoryAdapter.track = readSharedPref(sharedPreferences)
        val chosenTrack = findSong(position)
        val historyTrack = songHistoryAdapter.track

        if(historyTrack.size < 10) {
            if(historyTrack.isNotEmpty()) {
                if (historyTrack.contains(chosenTrack)) {
                    historyTrack.remove(chosenTrack)
                }
                historyTrack.add(0, chosenTrack)
            } else {
                historyTrack.add(chosenTrack)
            }
        } else {
            if(historyTrack.contains(chosenTrack)) {
                historyTrack.remove(chosenTrack)
                historyTrack.add(0, chosenTrack)
            } else {
                for (i in 9 downTo 1) {
                    historyTrack[i] = historyTrack[i - 1]
                }
                historyTrack[0] = chosenTrack
            }
        }
        songHistoryAdapter.notifyDataSetChanged()
        addToList(sharedPreferences, historyTrack)
    }
}