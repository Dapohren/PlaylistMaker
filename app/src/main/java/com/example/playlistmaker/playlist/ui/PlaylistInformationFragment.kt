package com.example.playlistmaker.playlist.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.databinding.FragmentPlaylistInformationBinding
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.player.ui.AudioPlayerActivity
import com.example.playlistmaker.playlist.presentation.PlaylistInformationViewModel
import com.example.playlistmaker.playlist.presentation.PlaylistNewStates
import com.example.playlistmaker.playlist.presentation.PlaylistTracksStates
import com.example.playlistmaker.search.domain.models.DataSongs
import com.example.playlistmaker.search.presentation.SongsAdapter
import com.example.playlistmaker.search.ui.CLICK_DEBOUNCE_DELAY
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInformationFragment : Fragment() {
    private var _binding: FragmentPlaylistInformationBinding? = null
    private val binding get() = _binding!!
    val viewModel : PlaylistInformationViewModel by viewModel()
    private lateinit var recycleView: RecyclerView
    private val songAdapter = SongsAdapter()
    private var trackWordEnding = ""
    private var isClickAllowed = true
    private lateinit var chosenPlaylist: PlaylistModel
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomNavigationViewLine: View? = null
    private lateinit var bottomSheetFixed: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetMenu: BottomSheetBehavior<LinearLayout>
    private lateinit var confirmDialogDeleteTrack: MaterialAlertDialogBuilder
    private lateinit var confirmDialogDeletePlaylist: MaterialAlertDialogBuilder
    private lateinit var trackTimeSumEnding: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Скрываем bottomNav
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavigationViewLine = requireActivity().findViewById(R.id.bottomNavigationViewLine)
        bottomNavigationView!!.visibility = View.GONE
        bottomNavigationViewLine!!.visibility = View.GONE
        // Получаем список треков
        viewModel.getPlaylistById(requireArguments().getInt(PLAYLIST_ID))
        viewModel.states.observe(viewLifecycleOwner){ state ->
            when(state){
                is PlaylistNewStates.Content -> {
                    chosenPlaylist = state.playlist
                    viewModel.getTracksFromPlaylist(chosenPlaylist.addedTracksId)
                }
            }
        }
        // Инициализируем адаптер
        recycleView = binding.recyclerViewSongs
        recycleView.adapter = songAdapter
        recycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        // Устанавливаем поведение BottomSheet
        setUpBottomSheetBehavior()
        // Узнаем есть ли треки и даем описание и значения
        viewModel.state.observe(viewLifecycleOwner) {state ->
            when(state) {
                PlaylistTracksStates.Empty -> {
                    binding.recyclerViewSongs.visibility = View.GONE
                    songAdapter.track = arrayListOf()
                    songAdapter.notifyDataSetChanged()
                    showNoTracksToast()
                    binding.tracksTime.text = getString(R.string.no_tracks)
                    binding.tracksCount.text = getString(R.string.no_songs)
                }
                is PlaylistTracksStates.NotEmpty -> {
                    songAdapter.track = state.tracks as ArrayList<DataSongs>
                    songAdapter.notifyDataSetChanged()
                    Glide.with(requireContext())
                        .load(chosenPlaylist.playlistImage)
                        .placeholder(R.drawable.placeholder2)
                        .into(binding.imageAlbum)

                    binding.albumName.text = chosenPlaylist.playlistName
                    binding.albumDescription.text = chosenPlaylist.playlistDescription

                    var trackTimeSumSeconds: Long = 0
                    songAdapter.track.forEach { trackTimeSumSeconds += it.trackTimeMillis/1000 }
                    val trackTimeSumMin = (trackTimeSumSeconds/60).toInt()

                    if(trackTimeSumMin in 10..20) {
                        trackTimeSumEnding = "$trackTimeSumMin минут"
                    } else if(trackTimeSumMin % 10 == 1) {
                        trackTimeSumEnding = "$trackTimeSumMin минута"
                    } else if(trackTimeSumMin % 10 in 2..4) {
                        trackTimeSumEnding = "$trackTimeSumMin минуты"
                    } else {
                        trackTimeSumEnding = "$trackTimeSumMin минут"
                    }

                    binding.tracksTime.text = trackTimeSumEnding

                    trackWordEnding = resources.getQuantityString(R.plurals.plurals_1, chosenPlaylist.addedTracksNumber, chosenPlaylist.addedTracksNumber)
                    binding.tracksCount.text = trackWordEnding
                }
            }
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonShare.setOnClickListener {
            sharePlaylist()
        }
        binding.three.setOnClickListener {
            openEditorBottomSheet()
            initBottomSheetMenu()
        }
        songAdapter.setOnTrackClickListener(object : SongsAdapter.onTrackClickListener {
            override fun onClicked(position: Int) {
                if (clickDebounce()) {
                    val chosenTrack = songAdapter.track[position]
                    router(chosenTrack)
                }
            }
        })

        songAdapter.setOnLongClickListener(object : SongsAdapter.OnLongTrackClickListener{
            override fun onLongTrackClick(position: Int) {
                setConfirmDialogDeleteTrack(songAdapter.track[position])
                confirmDialogDeleteTrack.show()
            }

        })


    }
    override fun onDestroyView() {
        super.onDestroyView()
        bottomNavigationView!!.visibility = View.VISIBLE
        bottomNavigationViewLine!!.visibility = View.VISIBLE
        _binding = null
    }

    private fun openEditorBottomSheet() {
        bottomSheetMenu.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.menuBottomSheet.visibility = View.VISIBLE

        binding.bottomSheetButtonShare.setOnClickListener {
            sharePlaylist()
        }
        binding.bottomSheetButtonDeletePlaylist.setOnClickListener {
            setConfirmDialogDeletePlaylist(chosenPlaylist)
            confirmDialogDeletePlaylist.show()
        }
        binding.bottomSheetButtonChangeInfo.setOnClickListener {
            findNavController().navigate(R.id.action_playlistInformationFragment_to_editPlaylistFragment, bundleOf(
                PLAYLIST_ID to chosenPlaylist.playlistId)
            )
        }
    }
    private fun initBottomSheetMenu(){
        val radius = resources.getDimensionPixelSize(R.dimen.dp_2)
        Glide.with(requireContext())
            .load(chosenPlaylist.playlistImage)
            .centerInside()
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(radius))
            .into(binding.bottomSheetPlaylistPhoto)

        binding.bottomSheetPlaylistName.text = chosenPlaylist.playlistName
        binding.bottomSheetTracksNumber.text = trackWordEnding
    }

    private fun sharePlaylist() {

        var number = 1
        var tracksTable = ""
        while(number <= chosenPlaylist.addedTracksNumber){
            tracksTable += "$number. ${songAdapter.track[number - 1].artistName} - ${songAdapter.track[number - 1].trackName} - ${SimpleDateFormat("mm:ss", Locale.getDefault()).format(songAdapter.track[number - 1].trackTimeMillis)}\n"
            number++
        }
        val tracksInfo = "${chosenPlaylist.playlistName}\n${chosenPlaylist.playlistDescription}\n$trackWordEnding\n$tracksTable"
        if(songAdapter.track.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_SEND).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, tracksInfo)
            context?.startActivity(intent)
        }
    }

    private fun setConfirmDialogDeleteTrack(chosenTrack: DataSongs){
        confirmDialogDeleteTrack = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireActivity().getString(R.string.delete_track))
            .setNegativeButton(requireActivity().getString(R.string.no)) { _, _ ->

            }.setPositiveButton(requireActivity().getString(R.string.yes)) { _, _ ->
                viewModel.deleteTrackFromPlaylist(chosenTrack, chosenPlaylist)
                viewModel.getTracksFromPlaylist(chosenPlaylist.addedTracksId)
            }
    }
    private fun setConfirmDialogDeletePlaylist(chosenPlaylist: PlaylistModel){
        confirmDialogDeletePlaylist = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить плейлист \"${chosenPlaylist.playlistName}\"?")
            .setNegativeButton(requireActivity().getString(R.string.no)) { _, _ ->

            }.setPositiveButton(requireActivity().getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist(chosenPlaylist.playlistId)
                findNavController().navigateUp()
            }
    }
    private fun router(chosenTrack: DataSongs) {
        val displayAudioPlayer = Intent(requireContext(), AudioPlayerActivity::class.java)
        displayAudioPlayer.putExtra("chosen_track", Gson().toJson(chosenTrack))
        this.startActivity(displayAudioPlayer)
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
    private fun showNoTracksToast(){
        val message = requireActivity().getString(R.string.no_tracks_in_playlist)
        val toast = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        )
        val snackbarView = toast.view
        snackbarView.setBackgroundResource(R.color.night_or_light)
        toast.show()
    }
    private fun setUpBottomSheetBehavior(){
       bottomSheetFixed = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val bottomSheetPeekHeight = (resources.displayMetrics.heightPixels*0.3).toInt()
        bottomSheetFixed.peekHeight = bottomSheetPeekHeight

        bottomSheetMenu = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetMenu.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay2.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay2.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }



    companion object {
        const val PLAYLIST_ID = "playlist_id"
    }

}