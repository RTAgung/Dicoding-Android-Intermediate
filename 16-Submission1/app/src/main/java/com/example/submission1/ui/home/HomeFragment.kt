package com.example.submission1.ui.home

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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.submission1.R
import com.example.submission1.data.ResultState
import com.example.submission1.databinding.FragmentHomeBinding
import com.example.submission1.ui.ViewModelFactory
import com.example.submission1.ui.adapter.StoryAdapter
import com.google.android.material.snackbar.Snackbar


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
            layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
            adapter = storyAdapter
        }
    }

    private fun setData() {
        viewModel.getStory().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        val listStory = result.data
                        if (listStory.isNotEmpty()) storyAdapter.submitList(listStory)
                        else showSnackbar(getString(R.string.no_available_data_message))
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        showSnackbar(result.error)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            binding.incLoading.loading.visibility = View.VISIBLE
        else
            binding.incLoading.loading.visibility = View.GONE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
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
                    R.id.menu_item_logout -> logoutProcess()
                }
                return true
            }
        }, viewLifecycleOwner, androidx.lifecycle.Lifecycle.State.RESUMED)
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: HomeViewModel by viewModels { factory }
        viewModel = initViewModel
    }
}