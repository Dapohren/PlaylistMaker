package com.example.playlistmaker.settings.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var shareApp: Button
    private lateinit var supportButton: Button
    private lateinit var agreeButton: Button
    private lateinit var backButton: Button
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shareApp = binding.share
        supportButton = binding.help
        agreeButton = binding.agree


        supportButton.setOnClickListener {
            viewModel.openSupport(
                email = getString(R.string.recipientEt),
                subject = getString(R.string.supportTitle),
                text = getString(R.string.supportText)
            )
        }

        shareApp.setOnClickListener {
            viewModel.shareApp(
                url = getString(R.string.appID),
                title = getString(R.string.appID)

            )
        }

        agreeButton.setOnClickListener {
            viewModel.openTerms(
                url = getString(R.string.agree)
            )
        }

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES){
            binding.switchOff.isChecked = true
        }

        binding.switchOff.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateThemeSettings(isChecked)
        }



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}