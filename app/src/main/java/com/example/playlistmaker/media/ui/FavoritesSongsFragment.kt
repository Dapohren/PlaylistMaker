package com.example.playlistmaker.media.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.media.presentation.FavoritesSongsViewModel

class FavoritesSongsFragment : Fragment() {

    companion object {
        private const val TEXT = "text"
        fun newInstance(text: String) = FavoritesSongsFragment().apply {
            arguments = Bundle().apply {
                putString(TEXT, text)
            }
        }

    }

    private lateinit var viewModel: FavoritesSongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

   /* override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavoritesSongsViewModel::class.java)
        // TODO: Use the ViewModel
    }*/

}