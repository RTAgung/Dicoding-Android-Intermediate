package com.example.submission1.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.submission1.R
import com.example.submission1.databinding.FragmentLoginBinding
import com.example.submission1.ui.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        playAnimation()
        setView()
    }

    private fun initViewModel() {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val initViewModel: AuthViewModel by viewModels { factory }
        viewModel = initViewModel
    }

    private fun setView() {
        binding.etEmail.doOnTextChanged { _, _, _, _ ->
            setButtonIsEnabled()
        }
        binding.etPassword.doOnTextChanged { _, _, _, _ ->
            setButtonIsEnabled()
        }
        binding.btnLogin.setOnClickListener {
            makeLoginObserve()
        }
        binding.btnRegister.setOnClickListener {
            moveToRegister()
        }
    }

    private fun moveToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun makeLoginObserve() {
//        TODO("Not yet implemented")
    }

    private fun setButtonIsEnabled() {
        binding.btnLogin.isEnabled = binding.etEmail.isValid && binding.etPassword.isValid
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.animLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val animLogin = ObjectAnimator.ofFloat(binding.animLogin, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val textLogin = ObjectAnimator.ofFloat(binding.tvLoginMessage, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val inputEmail = ObjectAnimator.ofFloat(binding.layoutEmail, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val inputPassword = ObjectAnimator.ofFloat(binding.layoutPassword, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val buttonLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val buttonRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        AnimatorSet().apply {
            play(animLogin).before(textLogin)
            play(textLogin).after(animLogin)
            play(inputEmail).after(textLogin)
            play(inputPassword).after(inputEmail)
            play(buttonLogin).after(inputPassword)
            play(buttonRegister).after(buttonLogin)
            start()
        }
    }
}