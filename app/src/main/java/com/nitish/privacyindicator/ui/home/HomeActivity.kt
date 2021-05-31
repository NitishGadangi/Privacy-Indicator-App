package com.nitish.privacyindicator.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils.SimpleStringSplitter
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nitish.privacyindicator.BuildConfig
import com.nitish.privacyindicator.databinding.ActivityHomeBinding
import com.nitish.privacyindicator.databinding.ContentServiceEnabledBinding
import com.nitish.privacyindicator.helpers.goToActivity
import com.nitish.privacyindicator.helpers.openBrowser
import com.nitish.privacyindicator.helpers.openSharingScreen
import com.nitish.privacyindicator.helpers.setViewTint
import com.nitish.privacyindicator.services.IndicatorService
import com.nitish.privacyindicator.repository.SharedPrefManager
import com.nitish.privacyindicator.ui.logs.AccessLogsActivity


class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding

    lateinit var serviceEnabledBinding: ContentServiceEnabledBinding

    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceEnabledBinding = binding.contentServiceEnabled

        val viewModelProviderFactory = HomeViewModelProviderFactory(application, SharedPrefManager.getInstance(applicationContext))
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(HomeViewModel::class.java)

        setUpObservers()
        setUpListeners()

    }

    private fun setUpObservers() {
        viewModel.cameraIndicatorStatus.observe(this, {
            serviceEnabledBinding.switchCamera.isChecked = it
            binding.indicatorsLayout.ivCam.visibility = if(it==true) View.VISIBLE else View.GONE
        })

        viewModel.microphoneIndicatorStatus.observe(this, {
            serviceEnabledBinding.switchMic.isChecked = it
            binding.indicatorsLayout.ivMic.visibility = if(it==true) View.VISIBLE else View.GONE
        })

        viewModel.locationIndicatorStatus.observe(this, {
            serviceEnabledBinding.switchLocation.isChecked = it
            binding.indicatorsLayout.ivLoc.visibility = if(it==true) View.VISIBLE else View.GONE
        })

        viewModel.vibrationAlertStatus.observe(this, {
            serviceEnabledBinding.switchVibration.isChecked = it
        })

        viewModel.notificationAlertStatus.observe(this, {
            serviceEnabledBinding.switchNotification.isChecked = it
        })

        viewModel.indicatorBackgroundColor.observe(this, {
            binding.indicatorsLayout.llBackground.setBackgroundColor(Color.parseColor(it))
        })

        viewModel.indicatorForegroundColor.observe(this, {
            binding.indicatorsLayout.ivCam.setViewTint(it)
            binding.indicatorsLayout.ivMic.setViewTint(it)
            binding.indicatorsLayout.ivLoc.setViewTint(it)
        })
    }

    private fun setUpListeners() {
        binding.mainSwitch.setOnCheckedChangeListener { button, isEnabled ->
            if(isEnabled){
               if(isAccessibilityServiceEnabled(applicationContext)) 
                   serviceEnabled()
                else
                    openAccessibilitySettingsPage(isEnabled)
            }else {
                if(!isAccessibilityServiceEnabled(applicationContext))
                    serviceDisabled()
                else
                    openAccessibilitySettingsPage(isEnabled)
            }
        }

        serviceEnabledBinding.switchCamera.setOnCheckedChangeListener { button, isEnabled ->
            viewModel.setCameraIndicatorStatus(isEnabled)
        }

        serviceEnabledBinding.switchMic.setOnCheckedChangeListener { button, isEnabled ->
            viewModel.setMicrophoneIndicatorStatus(isEnabled)
        }

        serviceEnabledBinding.switchLocation.setOnCheckedChangeListener { button, isEnabled ->
            viewModel.setLocationIndicatorStatus(isEnabled)
        }

        serviceEnabledBinding.switchVibration.setOnCheckedChangeListener { button, isEnabled ->
            viewModel.setVibrationAlertStatus(isEnabled)
        }

        serviceEnabledBinding.switchNotification.setOnCheckedChangeListener { button, isEnabled ->
            viewModel.setNotificationAlertStatus(isEnabled)
        }

        serviceEnabledBinding.settingsText.setOnClickListener {
            openCustomizationScreen()
        }

        serviceEnabledBinding.switchSettings.setOnClickListener {
            openCustomizationScreen()
        }

        serviceEnabledBinding.logsText.setOnClickListener {
            openAccessLogsScreen()
        }

        serviceEnabledBinding.switchLogs.setOnClickListener {
            openAccessLogsScreen()
        }

        serviceEnabledBinding.shareText.setOnClickListener {
            this.openSharingScreen(HomeViewModel.SHARING_TEXT)
        }

        serviceEnabledBinding.issueText.setOnClickListener {
            this.openBrowser(HomeViewModel.GITHUB_REPO)
        }

        binding.contentCredits.imgGithub.setOnClickListener {
            this.openBrowser(HomeViewModel.GITHUB_PROFILE)
        }

        binding.contentCredits.imgLinkedIn.setOnClickListener {
            this.openBrowser(HomeViewModel.LINKEDIN)
        }

        binding.contentCredits.tvFossHacks.setOnClickListener {
            this.openBrowser(HomeViewModel.FOSSHACKS)
        }
    }

    private fun openAccessibilitySettingsPage(isServiceDisabled:Boolean) {
        if (isServiceDisabled){
            Toast.makeText(this, "Turn On Privacy Indicators", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this, "Turn Off Privacy Indicators", Toast.LENGTH_LONG).show()
        }
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    private fun openCustomizationScreen() {
        val customizationFragment = CustomizationFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(binding.homeFragmentContainer.id, customizationFragment)
            addToBackStack("CUSTOMIZATION_SCREEN")
            commit()
        }
    }

    private fun openAccessLogsScreen() {
        this.goToActivity(AccessLogsActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (isAccessibilityServiceEnabled(applicationContext)) serviceEnabled() else serviceDisabled()
    }

    private fun isAccessibilityServiceEnabled(mContext: Context): Boolean {
        if(BuildConfig.IN_APP_TESTING_TOGGLE) return true
        val APPLICATION_ID = BuildConfig.APPLICATION_ID
        val ACCESSIBILITY_SERVICE = IndicatorService::class.java.canonicalName
        var accessibilityEnabled = 0
        val service = "$APPLICATION_ID/$ACCESSIBILITY_SERVICE"
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {

        }
        val mStringColonSplitter = SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                    mContext.applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun serviceEnabled() {
        binding.mainSwitch.isChecked = true
        binding.mainSwitch.text = "Enabled"
        binding.contentServiceDisabled.root.visibility = View.GONE
        serviceEnabledBinding.root.visibility = View.VISIBLE
        binding.contentCredits.root.visibility = View.VISIBLE
        binding.indicatorsLayout.root.visibility = View.VISIBLE
    }

    private fun serviceDisabled() {
        binding.mainSwitch.isChecked = false
        binding.mainSwitch.text = "Disabled"
        binding.contentServiceDisabled.root.visibility = View.VISIBLE
        serviceEnabledBinding.root.visibility = View.GONE
        binding.contentCredits.root.visibility = View.GONE
        binding.indicatorsLayout.root.visibility = View.GONE
    }
}