package com.example.playlistmaker.media.ui.playlist

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.RootActivity
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding


import com.example.playlistmaker.media.domain.models.PlaylistModel
import com.example.playlistmaker.media.presentation.playlist.NewPlaylistViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment : Fragment() {
    val viewModel: NewPlaylistViewModel by viewModel()
    private var _binding: FragmentNewPlaylistBinding? = null
    val binding get() = _binding!!
    var image: Uri? = null
    var file: String? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var bottomNavigationViewLine: View? = null
    private lateinit var dialog: MaterialAlertDialogBuilder
    lateinit var pickMedia : ActivityResultLauncher<PickVisualMediaRequest>
    val requester = PermissionRequester.instance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistName.doOnTextChanged { text, _, _, _ ->
            binding.buttonCreate.isEnabled = text?.toString().orEmpty().isNotBlank()
        }
        binding.playlistDescription.doOnTextChanged { text, _, _, _ ->
            text?.toString().orEmpty()
        }


        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(requireActivity().getString(R.string.finish_creating))
            .setMessage(requireActivity().getString(R.string.all_will_be_lost))
            .setNegativeButton(requireActivity().getString(R.string.cancel)) { _, _ ->

            }.setPositiveButton(requireActivity().getString(R.string.finish)) { _, _ ->
                navigateBackTo()
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
                Log.d(requireActivity().getString(R.string.photo_pick), requireActivity().getString(R.string.no_media_selected))
            }
        }

        if(activity is RootActivity){
            bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
            bottomNavigationViewLine = requireActivity().findViewById(R.id.bottomNavigationViewLine)
            bottomNavigationView!!.visibility = View.GONE
            bottomNavigationViewLine!!.visibility = View.GONE
        }

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

        binding.backButton.setOnClickListener{
            backButton()
        }

        binding.buttonCreate.setOnClickListener {
            if (image != null) {
                saveImageToPrivateStorage(image!!)
            }
            viewModel.createPlaylist(
                PlaylistModel(
                    playlistId = null,
                    playlistName = binding.playlistName.text.toString(),
                    playlistDescription = binding.playlistDescription.text.toString(),
                    playlistImage = file,
                    addedTracksId = ArrayList(),
                    addedTracksNumber = 0,
                )
            )
            var playlistName = binding.playlistName.text.toString()

            showToast("Плейлист $playlistName успешно создан")

            navigateBackTo()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backButton()

            }
        })
    }
    fun showToast(message: String) {
        val toast = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_SHORT
        )
        val snackbarView = toast.view
        snackbarView.setBackgroundResource(R.color.night_or_light)
        toast.show()
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (image != null || binding.playlistName.text!!.isNotEmpty() || binding.playlistDescription.text!!.isNotEmpty())
                    dialog.show()
                else
                    navigateBackTo()
            }
        })
        if (activity is RootActivity) {
            bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
            bottomNavigationViewLine = requireActivity().findViewById(R.id.bottomNavigationViewLine)
            bottomNavigationView!!.visibility = View.GONE
            bottomNavigationViewLine!!.visibility = View.GONE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (activity is RootActivity) {
            bottomNavigationView!!.visibility = View.VISIBLE
            bottomNavigationViewLine!!.visibility = View.VISIBLE
        }
        _binding = null
    }

    fun backButton() {
        if(image != null || binding.playlistName.text!!.isNotEmpty() || binding.playlistDescription.text!!.isNotEmpty())
            dialog.show()
        else
            navigateBackTo()
    }

    fun navigateBackTo() {
        image = null
        binding.playlistName.setText("")
        binding.playlistDescription.setText("")
        if (activity is RootActivity){
            findNavController().navigateUp()
        } else {
            val audioplayerFragmentContainer = requireActivity().findViewById<FragmentContainerView>(R.id.rootFragmentContainerView)
            if(audioplayerFragmentContainer.visibility == View.GONE){
                requireActivity().finish()
            }
            audioplayerFragmentContainer.visibility = View.GONE
        }
    }

    fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), requireActivity().getString(R.string.Newalbum))
        if (!filePath.exists()){
            filePath.mkdirs()
        }
        var number = 1
        var fileF = File(filePath, "cover_$number.jpg")
        while(fileF.exists()){
            number++
            fileF = File(filePath, "cover_$number.jpg")
        }
        file = fileF.toString()
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(fileF)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}



