package com.nitish.privacyindicator.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nitish.privacyindicator.R
import com.nitish.privacyindicator.databinding.AppBarBinding

class AppBarFragment: Fragment(R.layout.app_bar) {

    private lateinit var binding: AppBarBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AppBarBinding.bind(view)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.ivBackButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}