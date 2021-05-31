package com.nitish.privacyindicator.ui.logs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nitish.privacyindicator.R
import com.nitish.privacyindicator.databinding.ActivityAccessLogsBinding
import com.nitish.privacyindicator.db.AccessLogsDatabase
import com.nitish.privacyindicator.repository.AccessLogsRepo
import com.nitish.privacyindicator.ui.adapters.AccessLogsAdapter

class AccessLogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccessLogsBinding

    private lateinit var viewModel: AccessLogsViewModel

    private lateinit var accessLogsAdapter: AccessLogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accessLogsDatabase = AccessLogsDatabase(this)
        val viewModelProviderFactory = LogsViewModelProviderFactory(application, AccessLogsRepo(accessLogsDatabase))
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AccessLogsViewModel::class.java)

        setUpViews()
        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.allAccessLogs.observe(this, { accessLogsList ->
            accessLogsAdapter.differ.submitList(accessLogsList)
            if(accessLogsList.isEmpty()){
                binding.tvEmptyState.visibility = View.VISIBLE
            }else{
                binding.tvEmptyState.visibility = View.GONE
            }
        })
    }

    private fun setUpViews() {
        accessLogsAdapter = AccessLogsAdapter()
        binding.rvAccessLogs.apply {
            adapter = accessLogsAdapter
            layoutManager = LinearLayoutManager(this@AccessLogsActivity)
        }
    }

    private fun setUpListeners() {
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllLogs()
        }
    }
}