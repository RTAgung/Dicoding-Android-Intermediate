package com.example.submission1.ui.welcome

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
import androidx.navigation.fragment.findNavController
import com.example.submission1.R
import com.example.submission1.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private val welcomeDuration = 5000L

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }, welcomeDuration)
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