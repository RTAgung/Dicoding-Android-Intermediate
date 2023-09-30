package com.example.submission2.ui.addstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.submission2.R
import com.example.submission2.data.ResultState
import com.example.submission2.databinding.FragmentAddStoryBinding
import com.example.submission2.ui.ViewModelFactory
import com.example.submission2.utils.Helper
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddStoryViewModel

    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        checkSession()
        viewModel.isReadyToLoadData.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                setAppBar()
                setLocation()
                setView()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocation() {
        val mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        if (Helper.Location.checkPermissions(requireActivity())) {
            if (Helper.Location.isLocationEnabled(requireActivity())) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        viewModel.lat = location.latitude
                        viewModel.lon = location.longitude
                        Helper.Location.generateLocation(
                            requireActivity(),
                            location.latitude,
                            location.longitude
                        ) { cityName ->
                            binding.tvStoryLocation.text =
                                getString(R.string.from_city_location_text, cityName)
                        }
                    }
                }
            } else {
                showSnackbar(getString(R.string.location_service_is_not_active))
            }
        } else {
            requestLocationPermissions()
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showSnackbar(getString(R.string.permission_granted))
                setLocation()
            } else {
                showSnackbar(getString(R.string.permission_denied))
            }
        }

    private fun requestLocationPermissions() {
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun setView() {
        if (!isCameraPermissionsGranted()) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnOpenCamera.setOnClickListener { openCamera() }
        binding.btnOpenGallery.setOnClickListener { openGallery() }
        binding.btnUpload.setOnClickListener { uploadProcess() }
    }

    private fun uploadProcess() {
        currentImageUri?.let { uri ->
            val imageFile = Helper.ImageFile.uriToFile(uri, requireActivity())
            val description = binding.etDesc.text.toString()

            viewModel.uploadImage(imageFile, description).observe(requireActivity()) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showLoading(false)
                            val isSuccess = result.data
                            if (isSuccess) {
                                findNavController().navigateUp()
                            } else {
                                showSnackbar(getString(R.string.upload_story_failed_message))
                            }
                        }

                        is ResultState.Error -> {
                            showLoading(false)
                            showSnackbar(result.error)
                        }
                    }
                }
            }
        } ?: showSnackbar(getString(R.string.no_media_message))
    }

    private fun openGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showSnackbar(getString(R.string.no_media_message))
        }
    }

    private fun openCamera() {
        currentImageUri = Helper.ImageFile.getImageUri(requireActivity())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showSnackbar(getString(R.string.permission_granted))
            } else {
                showSnackbar(getString(R.string.permission_denied))
                findNavController().navigateUp()
            }
        }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivStoryPhoto.setImageURI(it)
        }
    }

    private fun isCameraPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun setAppBar() {
        val toolbar: Toolbar = binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.title = getString(R.string.add_story_text)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> findNavController().navigateUp()
                }
                return true
            }
        }, viewLifecycleOwner, androidx.lifecycle.Lifecycle.State.RESUMED)
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
        findNavController().navigate(R.id.action_addStoryFragment_to_loginFragment)
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: AddStoryViewModel by viewModels { factory }
        viewModel = initViewModel
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
}