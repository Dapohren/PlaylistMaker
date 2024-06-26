package com.example.playlistmaker.media.ui.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.media.presentation.playlist.PlayListViewModel
import com.example.playlistmaker.media.presentation.playlist.PlaylistAdapter
import com.example.playlistmaker.media.presentation.playlist.PlaylistState
import com.example.playlistmaker.media.presentation.playlist.PlaylistViewHolder
import com.example.playlistmaker.playlist.ui.PlaylistInformationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListFragment : Fragment() {


    private val playListsAdapter: PlaylistAdapter by lazy { PlaylistAdapter() }
    private lateinit var count: TextView

    private var _binding: FragmentPlayListBinding? = null
    private val binding get() = _binding!!
    val viewModel: PlayListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNewPlaylist.visibility = View.VISIBLE
        binding.recyclerViewPlaylists.adapter = playListsAdapter
        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        viewModel.getPlaylists()

        playListsAdapter.setOnTrackClickListener(object : PlaylistAdapter.onTrackClickListener{
            override fun onClicked(position: Int) {
                val chosenPlaylist = playListsAdapter.playlist[position]
                findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistInformationFragment, bundleOf(PlaylistInformationFragment.PLAYLIST_ID to chosenPlaylist.playlistId))
            }
        })

        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is PlaylistState.NotEmpty -> {
                    playListsAdapter.playlist = state.playlists
                    playListsAdapter.notifyDataSetChanged()
                    binding.recyclerViewPlaylists.visibility = View.VISIBLE
                    binding.emptyImage.visibility = View.GONE
                    binding.emptyText.visibility = View.GONE
                    binding.buttonNewPlaylist.visibility = View.VISIBLE

                }
                PlaylistState.Empty -> {
                    binding.recyclerViewPlaylists.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                    binding.emptyImage.visibility = View.VISIBLE
                    binding.buttonNewPlaylist.visibility = View.VISIBLE
                }
            }
        }


        binding.buttonNewPlaylist.visibility = View.VISIBLE

        binding.buttonNewPlaylist.setOnClickListener(){
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_newPlaylistFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}