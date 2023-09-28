package com.example.submission2.ui.welcome

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.submission2.R
import com.example.submission2.databinding.FragmentWelcomeBinding
import com.example.submission2.ui.ViewModelFactory
import com.example.submission2.utils.Constant

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            getSession()
        }, Constant.WELCOME_PAGE_DURATION)
    }

    private fun getSession() {
        viewModel.getUserSession().observe(viewLifecycleOwner) { user ->
            if (user == null)
                findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            else
                findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: WelcomeViewModel by viewModels { factory }
        viewModel = initViewModel
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.animWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        ObjectAnimator.ofFloat(binding.tvWelcomeMessage, View.ALPHA, 1f).apply {
            duration = 2000
            interpolator = AccelerateInterpolator()
        }.start()
    }
}