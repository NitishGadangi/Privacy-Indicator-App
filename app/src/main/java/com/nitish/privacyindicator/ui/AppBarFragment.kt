package com.nitish.privacyindicator.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nitish.privacyindicator.R
import com.nitish.privacyindicator.databinding.AppBarBinding
import com.nitish.privacyindicator.models.AccessLog
import com.nitish.privacyindicator.ui.home.HomeActivity
import com.nitish.privacyindicator.ui.logs.AccessLogsActivity

class AppBarFragment: Fragment(R.layout.app_bar) {

    private lateinit var binding: AppBarBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AppBarBinding.bind(view)

        setUpViews()
        setUpListeners()
    }

    private fun setUpViews() {
        binding.tvBarHeader.text = when(activity){
            is HomeActivity -> "Customize Indicators"
            is AccessLogsActivity -> "Indicator Logs"
            else -> ""
        }
    }

    private fun setUpListeners() {
        binding.ivBackButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}