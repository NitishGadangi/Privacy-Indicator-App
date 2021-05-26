package com.nitish.privacyindicator.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.nitish.privacyindicator.R
import com.nitish.privacyindicator.databinding.FragmentCustomizationBinding

class CustomizationFragment : Fragment(R.layout.fragment_customization) {

    lateinit var binding: FragmentCustomizationBinding

    lateinit var viewModel: HomeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCustomizationBinding.bind(view)

        viewModel = (activity as HomeActivity).viewModel


    }
}