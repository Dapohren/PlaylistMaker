package com.example.playlistmaker.playlist.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesSongsBinding
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.databinding.FragmentPlaylistInformationBinding
import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.media.presentation.playlist.PlaylistState
import com.example.playlistmaker.media.ui.playlist.NewPlaylistFragment
import com.example.playlistmaker.playlist.presentation.PlaylistInformationViewModel
import com.example.playlistmaker.playlist.presentation.PlaylistNewStates
import com.example.playlistmaker.playlist.ui.PlaylistInformationFragment.Companion.PLAYLIST_ID
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {


    val viewModelEdit : PlaylistInformationViewModel by viewModel()
    private lateinit var chosenPlaylist: PlaylistModel



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            binding.buttonCreate.isEnabled = text?.toString().orEmpty().isNotBlank()
        }
        binding.playlistDescription.doOnTextChanged { text, _, _, _ ->
            text?.toString().orEmpty()
        }
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                image = uri
                val radius = resources.getDimensionPixelSize(R.dimen.dp_8)
                Glide.with(requireContext())
                    .load(uri)
                    .transform(RoundedCorners(radius))
                    .into(binding.newPlaylistPhoto)
            } else {
                Log.d(requireActivity().getString(R.string.photo_pick), requireActivity().getString(
                    R.string.no_media_selected))
            }
        }
        clickListeners()
        viewModelEdit.getPlaylistById(requireArguments().getInt(PLAYLIST_ID))

        viewModelEdit.states.observe(viewLifecycleOwner){ states ->
            when(states){
                is PlaylistNewStates.Content -> {
                    chosenPlaylist = states.playlist
                    initView()
                }
            }
        }
        }
    private fun initView(){
        val radius = resources.getDimensionPixelSize(R.dimen.dp_8)
        Glide.with(requireContext())
            .load(chosenPlaylist.playlistImage)
            .placeholder(R.drawable.placeholder2)
            .transform(RoundedCorners(radius))
            .into(binding.newPlaylistPhoto)
        binding.playlistName.setText(chosenPlaylist.playlistName)
        binding.playlistDescription.setText("${chosenPlaylist.playlistDescription}")
        binding.buttonCreate.text = getString(R.string.save)
    }

   fun clickListeners() {
        binding.backButton.setOnClickListener{
            findNavController().navigateUp()
        }
        buttonCreateClickListener()
        playlistPhotoClickListener()
    }

    private fun playlistPhotoClickListener() {
        binding.newPlaylistPhoto.setOnClickListener{
            lifecycleScope.launch {
                if(Build.VERSION.SDK_INT >= 33){
                    requester.request(android.Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    requester.request(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.collect { result ->
                    when (result){
                        is PermissionResult.Granted -> pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        is PermissionResult.Denied.NeedsRationale -> {
                            val message = requireActivity().getString(R.string.need_permission)
                            Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_LONG)
                                .show()
                        }
                        is PermissionResult.Denied.DeniedPermanently -> {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.data = Uri.fromParts("package", requireContext().packageName, null)
                            requireContext().startActivity(intent)
                        }
                        PermissionResult.Cancelled -> {
                            return@collect
                        }
                    }
                }
            }
        }
    }

    private fun buttonCreateClickListener(){
        binding.buttonCreate.setOnClickListener{
            if(image != null)
                saveImageToPrivateStorage(image!!)
            else
                file = chosenPlaylist.playlistImage
            viewModelEdit.editPlaylist(
                chosenPlaylist.playlistId,
                binding.playlistName.text.toString(),
                binding.playlistDescription.text.toString(),
                file,
            )
            findNavController().navigateUp()
        }
    }
}