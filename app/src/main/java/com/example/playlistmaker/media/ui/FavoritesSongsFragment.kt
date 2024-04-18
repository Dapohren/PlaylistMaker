package com.example.playlistmaker.media.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.media.presentation.FavoritesSongsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesSongsFragment : Fragment() {

    companion object {
        private const val TEXT = "text"
        fun newInstance(text: String) = FavoritesSongsFragment().apply {
            arguments = Bundle().apply {
                putString(TEXT, text)
            }
        }

    }

    private val viewModel: FavoritesSongsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}