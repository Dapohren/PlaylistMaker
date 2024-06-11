package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.debounce
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.SearchActivityViewModel
import com.example.playlistmaker.search.presentation.SearchStates
import com.example.playlistmaker.search.presentation.SongsAdapter
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

const val SONGS_PREFERENCES = "songs_preferences"
private const val CLICK_DEBOUNCE_DELAY = 1000L
private const val SEARCH_DEBOUNCE_DELAY = 2000L
const val CHOSEN_TRACK = "chosen_track"
const val SONGS_LIST_KEY = "songs_list_key"

class SearchFragment: Fragment() {

    private lateinit var editText: EditText
    private val viewModel: SearchActivityViewModel by viewModel()
    private lateinit var recycleView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var isClickAllowed = true
    private lateinit var searchHistory: RecyclerView
    private lateinit var layoutHistory: LinearLayout


    private var textString: String = ""

    private val songAdapter = SongsAdapter()
    private val songHistoryAdapter = SongsAdapter()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackSearchDebounce: (String) -> Unit



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editText = binding.searchButton2
        recycleView = binding.ourRecycle
        progressBar = binding.progressBar
        searchHistory = binding.recycleHistory
        layoutHistory = binding.layoutHistory
        recycleView.adapter = songAdapter
        trackSearchDebounce = debounce<String>(SEARCH_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, true) { changedText ->
            loadTracks(changedText)
        }

        recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchHistory.adapter = songHistoryAdapter
        searchHistory.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                SearchStates.Empty -> showEmptyResult()
                SearchStates.Error -> showTracksError()
                SearchStates.Loading -> showLoading()
                is SearchStates.Tracks -> showTracks(state.tracks)
                SearchStates.ClearTracks -> {
                    clearSearchText()
                    hideKeyboard()
                    hideTracks()
                    recycleView.visibility = View.GONE
                    binding.placeholderNothingF.visibility = View.GONE
                    binding.placehText.visibility = View.GONE
                    binding.ourRecycle.visibility = View.GONE
                }

                is SearchStates.History -> {
                    songHistoryAdapter.track = state.history
                    songHistoryAdapter.notifyDataSetChanged()
                    if (state.isShown and songHistoryAdapter.track.isNotEmpty()) {
                        searchHistory.visibility = View.VISIBLE
                        layoutHistory.visibility = View.VISIBLE
                        hideNoInternetNothingFoundViews()
                        binding.cleanHistory.visibility = View.VISIBLE
                        recycleView.visibility = View.GONE
                    } else {
                        searchHistory.visibility = View.GONE
                        binding.cleanHistory.visibility = View.GONE
                        recycleView.visibility = View.GONE
                    }
                }
                else -> {
                    searchHistory.visibility = View.GONE
                    binding.cleanHistory.visibility = View.GONE
                    recycleView.visibility = View.GONE
                }
            }
        }
        binding.refButton.setOnClickListener {
            viewModel.searchTracks(editText.text.toString())
        }

        binding.clearIcon.setOnClickListener {
            viewModel.searchTextClearClicked()
        }

        binding.cleanHistory.setOnClickListener {
            viewModel.onClearHistoryClicked()
            layoutHistory.visibility = View.GONE
        }


        val textWatch = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val changedText = binding.searchButton2.text.toString()
                searchDebounce(changedText)
                layoutHistory.visibility = View.GONE
                searchHistory.visibility = View.GONE
                binding.placeholderNothingF.visibility = View.GONE
                binding.placehText.visibility = View.GONE
                clearButtonVisibility(s?.isNotEmpty() ?: false)
                viewModel.showHistoryTracksEditTextOnFocus(editText)

            }

            override fun afterTextChanged(s: Editable?) {}
        }
        editText.addTextChangedListener(textWatch)

        songAdapter.setOnTrackClickListener(object : SongsAdapter.onTrackClickListener {
            override fun onClicked(position: Int) {
                if (clickDebounce()) {
                    val chosenTrack = songAdapter.track[position]
                    showSearchHistory(chosenTrack)
                    router(chosenTrack)
                }
            }
        })
        songHistoryAdapter.setOnTrackClickListener(object : SongsAdapter.onTrackClickListener {
            override fun onClicked(position: Int) {
                val trackChosen = songHistoryAdapter.track[position]
                addTrackOnTopSearchHistory(trackChosen, position)
                router(trackChosen)
            }
        })

        editText.setOnFocusChangeListener { _, _ ->
            viewModel.showHistoryTracksEditTextOnFocus(editText)
        }
    }



private fun loadTracks(changedText: String){
    viewModel.searchTracks(changedText)
}

private fun router(chosenTrack: DataSongs) {
    val displayAudioPlayer = Intent(requireContext(), AudioPlayerActivity::class.java)
    displayAudioPlayer.putExtra("chosen_track", Gson().toJson(chosenTrack))
    this.startActivity(displayAudioPlayer)
}


private fun searchDebounce(changedText: String) {
    trackSearchDebounce(changedText)
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
    layoutHistory.visibility = View.GONE
    searchHistory.visibility = View.GONE
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
    val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
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
    layoutHistory.visibility = View.GONE
    binding.placehText.visibility = View.GONE
    progressBar.visibility = View.VISIBLE
}

private fun clearSearchHistory(){
    songHistoryAdapter.track.clear()
    songAdapter.notifyDataSetChanged()
    viewModel.writeToSharedPreferences(songHistoryAdapter.track)
    searchHistory.visibility = View.GONE
}

private fun clearButtonVisibility(isVisible: Boolean) {
    binding.clearIcon.visibility = if (isVisible) View.VISIBLE else View.GONE
}

companion object {
    private  const val SEARCH = "TEXT"
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(SEARCH, textString)
}
/*override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    textString = savedInstanceState.getString(SEARCH).toString()
    editText.setText(textString)
}*/



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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}