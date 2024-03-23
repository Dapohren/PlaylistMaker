package com.example.playlistmaker.search.presentation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.R

import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.util.Creator
import com.google.gson.Gson


const val SONGS_PREFERENCES = "songs_preferences"



private const val CLICK_DEBOUNCE_DELAY = 1000L
private const val SEARCH_DEBOUNCE_DELAY = 2000L
const val CHOSEN_TRACK = "chosen_track"
const val SONGS_LIST_KEY = "songs_list_key"

class SearchActivity : AppCompatActivity() {
    private val searchRunnable = Runnable { loadTracks() }
    private lateinit var editText: EditText
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchActivityViewModel
    //private val searchRunnable = Runnable { search() }
    private lateinit var recycleView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    //Кнопки истории

   // private lateinit var youSearch: TextView
    private lateinit var searchHistory: RecyclerView
    private lateinit var layoutHistory: LinearLayout
    // кнопки экрана и экрана ошибки

    private var textString: String = ""

    // инициализация адаптер

    private val songAdapter = SongsAdapter()
    private val songHistoryAdapter = SongsAdapter()
    private lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        editText = findViewById(R.id.search_button2)
        recycleView = findViewById(R.id.our_recycle)
        progressBar = findViewById(R.id.progressBar)
        searchHistory = findViewById(R.id.recycle_history)
        layoutHistory = findViewById(R.id.layout_history)
        recycleView.adapter = songAdapter
        recycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistory.adapter = songHistoryAdapter
        searchHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // сохранение
        sharedPreferences = getSharedPreferences(SONGS_PREFERENCES, MODE_PRIVATE)
      //  songHistoryAdapter.track = readSharedPref(sharedPreferences)

        val searchInteractor = Creator.provideTrackInteractor(sharedPreferences)
        // поиск фильма на выбор
        viewModel = ViewModelProvider(this, SearchViewModelFactory(searchInteractor))[SearchActivityViewModel::class.java]
        viewModel.state.observe(this) {state ->
            when(state) {
                SearchStates.Empty -> showEmptyResult()
                SearchStates.Error -> showTracksError()
                SearchStates.Loading -> showLoading()
                is SearchStates.Tracks -> showTracks(state.tracks)
                SearchStates.ClearTracks -> {
                    clearSearchText()
                    hideKeyboard()
                    hideTracks()
                }
                is SearchStates.History -> {
                    songHistoryAdapter.track = state.history
                    songHistoryAdapter.notifyDataSetChanged()
                    if(state.isShown and songHistoryAdapter.track.isNotEmpty()){
                        searchHistory.visibility = View.VISIBLE
                        layoutHistory.visibility = View.VISIBLE
                        hideNoInternetNothingFoundViews()
                    } else {
                        layoutHistory.visibility = View.GONE
                        searchHistory.visibility = View.GONE

                    }
                }
                SearchStates.ClearHistory -> clearSearchHistory()
                //else -> {}
            }

        }

        binding.backButton2.setOnClickListener {
            finish()
        }
        binding.refButton.setOnClickListener {
            viewModel.searchTracks(editText.text.toString())
        }

        binding.clearIcon.setOnClickListener {
            viewModel.searchTextClearClicked()
        }

        binding.cleanHistory.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        val textWatch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
                clearButtonVisibility(s?.isNotEmpty() ?: false)
                viewModel.showHistoryTracksEditTextOnFocus(editText)
                //layoutHistory.visibility = if (editText.hasFocus() && editText.text.isEmpty() && songHistoryAdapter.track.isNotEmpty()) View.VISIBLE else View.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        //
        editText.addTextChangedListener(textWatch)


        // нажатие на элемент
        songAdapter.setOnTrackClickListener(object: SongsAdapter.onTrackClickListener {
            override fun onClicked(position: Int) {
                if(clickDebounce()) {
                    val chosenTrack = songAdapter.track[position]
                    showSearchHistory(chosenTrack)
                    router(chosenTrack)
                }
            }
        })
        songHistoryAdapter.setOnTrackClickListener(object: SongsAdapter.onTrackClickListener  {
            override fun onClicked(position: Int) {
                val trackChosen = songHistoryAdapter.track[position]
                addTrackOnTopSearchHistory(trackChosen, position)
                router(trackChosen)
            }
        })

        editText.setOnFocusChangeListener { _, _ ->
            viewModel.showHistoryTracksEditTextOnFocus(editText)
        }
        // отчистить историю


    }

    private fun loadTracks(){
        viewModel.searchTracks(editText.text.toString())
    }

    private fun router(chosenTrack: DataSongs) {
        val displayAudioPlayer = Intent(this, AudioPlayerActivity::class.java)
        displayAudioPlayer.putExtra("chosen_track", Gson().toJson(chosenTrack))
        this.startActivity(displayAudioPlayer)
    }



    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun addTrackOnTopSearchHistory(trackChosen: DataSongs, position: Int){
        songHistoryAdapter.track.add(0, trackChosen)
        songHistoryAdapter.track.removeAt(position + 1)
        songHistoryAdapter.notifyDataSetChanged()
        viewModel.writeToSharedPreferences(songHistoryAdapter.track)
    }

    private fun showEmptyResult(){
        progressBar.visibility = View.GONE
        binding.placeholderNothingF.visibility = View.VISIBLE
        binding.placehText.visibility = View.VISIBLE
        binding.placehText.text = getString(R.string.nothing_found)
    }
    private fun showTracks(tracks: List<DataSongs>){
        progressBar.visibility = View.GONE
        recycleView.visibility = View.VISIBLE
        songAdapter.track.clear()
        songAdapter.track.addAll(tracks)
        songAdapter.notifyDataSetChanged()
    }

    private fun showTracksError(){
        progressBar.visibility = View.GONE
        binding.placeholderNoInet.visibility = View.VISIBLE
        binding.refButton.visibility = View.VISIBLE
        binding.placehText.visibility = View.VISIBLE
        binding.placehText.text = getString(R.string.no_conection)
    }

    private fun clearSearchText() {
        editText.setText("")
    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    private fun hideTracks() {
        songAdapter.track.clear()
        songAdapter.notifyDataSetChanged()
    }

    private fun hideNoInternetNothingFoundViews(){
        binding.placeholderNoInet.visibility = View.GONE
        binding.refButton.visibility = View.GONE
        binding.placeholderNothingF.visibility = View.GONE
        binding.placehText.visibility = View.GONE
    }

    private fun showLoading() {
        songAdapter.track.clear()
        songAdapter.notifyDataSetChanged()
        recycleView.visibility = View.GONE
        binding.placeholderNoInet.visibility = View.GONE
        binding.refButton.visibility = View.GONE
        binding.placeholderNothingF.visibility = View.GONE
        searchHistory.visibility = View.GONE
        binding.placehText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun clearSearchHistory(){
        songHistoryAdapter.track.clear()
        songAdapter.notifyDataSetChanged()
        viewModel.writeToSharedPreferences(songHistoryAdapter.track)
        searchHistory.visibility = View.GONE
    }

    // отчистка поиска
    private fun clearButtonVisibility(isVisible: Boolean) {
        binding.clearIcon.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

   companion object {
      private  const val SEARCH = "TEXT"
    }
    // показать текст ошибки

    // показать картинку



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

    private fun showSearchHistory(chosenTrack: DataSongs) {
        if(songHistoryAdapter.track.size < 10) {
            if(songHistoryAdapter.track.isNotEmpty()) {
                if (songHistoryAdapter.track.contains(chosenTrack)) {
                    songHistoryAdapter.track.remove(chosenTrack)
                }
                songHistoryAdapter.track.add(0, chosenTrack)
            } else {
                songHistoryAdapter.track.add(chosenTrack)
            }
        } else {
            if(songHistoryAdapter.track.contains(chosenTrack)) {
                songHistoryAdapter.track.remove(chosenTrack)
                songHistoryAdapter.track.add(0, chosenTrack)
            } else {
                for (i in 9 downTo 1) {
                    songHistoryAdapter.track[i] = songHistoryAdapter.track[i - 1]
                }
                songHistoryAdapter.track[0] = chosenTrack
            }
        }
        songHistoryAdapter.notifyDataSetChanged()
        viewModel.writeToSharedPreferences(songHistoryAdapter.track)
    }
}