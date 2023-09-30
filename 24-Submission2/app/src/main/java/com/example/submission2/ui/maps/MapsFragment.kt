package com.example.submission2.ui.maps

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.submission2.R
import com.example.submission2.databinding.FragmentMapsBinding
import com.example.submission2.ui.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapsViewModel

    private lateinit var googleMap: GoogleMap

    private val callback = OnMapReadyCallback {
        googleMap = it
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()

        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun setMapStyle() {
        var resJsonStyle = R.raw.map_day_style
        val nightModeFlags = requireActivity().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> resJsonStyle = R.raw.map_night_style
            Configuration.UI_MODE_NIGHT_NO -> resJsonStyle = R.raw.map_day_style
            Configuration.UI_MODE_NIGHT_UNDEFINED -> resJsonStyle = R.raw.map_day_style
        }
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(),
                resJsonStyle
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        checkSession()
        viewModel.isReadyToLoadData.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                setAppBar()
                setMap()
            }
        }
    }

    private fun setAppBar() {
        binding.btnMapBack.setOnClickListener { findNavController().navigateUp() }
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
        findNavController().navigate(R.id.action_mapsFragment_to_loginFragment)
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: MapsViewModel by viewModels { factory }
        viewModel = initViewModel
    }

    private fun setMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}