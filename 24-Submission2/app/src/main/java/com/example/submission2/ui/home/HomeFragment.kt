package com.example.submission2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission2.R
import com.example.submission2.databinding.FragmentHomeBinding
import com.example.submission2.ui.ViewModelFactory
import com.example.submission2.ui.adapter.LoadingStateAdapter
import com.example.submission2.ui.adapter.StoryAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        checkSession()
        initAdapter()
        viewModel.isReadyToLoadData.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                setAppBar()
                setFab()
                setData()
            }
        }
    }

    private fun setFab() {
        binding.fabAddStory.setOnClickListener {
            moveToAddStory()
        }
    }

    private fun moveToAddStory() {
        findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
    }

    private fun initAdapter() {
        storyAdapter = StoryAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun setData() {
        viewModel.getStory().observe(viewLifecycleOwner) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun checkSession() {
        viewModel.getUserSession().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.user = user
                viewModel.setReadyToLoadData(true)
            } else {
                logoutProcess()
                viewModel.setReadyToLoadData(false)
            }
        }
    }

    private fun logoutProcess() {
        viewModel.clearUserSession()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }

    private fun setAppBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_item_logout -> openLogoutDialog()
                    R.id.menu_item_map -> moveToMap()
                }
                return true
            }
        }, viewLifecycleOwner, androidx.lifecycle.Lifecycle.State.RESUMED)
    }

    private fun moveToMap() {
        findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
    }

    private fun openLogoutDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.confirmation_logout_message))
            .setNegativeButton(getString(R.string.stay)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.logout)) { dialog, _ ->
                logoutProcess()
                dialog.dismiss()
            }
            .show()
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: HomeViewModel by viewModels { factory }
        viewModel = initViewModel
    }
}