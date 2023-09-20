package com.example.submission1.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.submission1.R
import com.example.submission1.data.ResultState
import com.example.submission1.databinding.FragmentRegisterBinding
import com.example.submission1.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        binding.btnRegister.setOnClickListener {
            if (binding.btnRegister.isEnabled) makeRegisterObserve()
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun makeRegisterObserve() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        viewModel.makeRegister(name, email, password).observe(viewLifecycleOwner) { result ->
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
                            showSnackbar(getString(R.string.register_success_message))
                        } else {
                            showSnackbar(getString(R.string.register_failed_message))
                        }
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

    private fun setButtonIsEnabled() {
        binding.btnRegister.isEnabled = binding.etEmail.isValid && binding.etPassword.isValid
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.animRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val animRegister = ObjectAnimator.ofFloat(binding.animRegister, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val textRegister = ObjectAnimator.ofFloat(binding.tvRegisterMessage, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        val inputName = ObjectAnimator.ofFloat(binding.layoutName, View.ALPHA, 1f).apply {
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
        val buttonRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).apply {
            duration = 200
            interpolator = AccelerateInterpolator()
        }
        AnimatorSet().apply {
            play(animRegister).before(textRegister)
            play(textRegister).after(animRegister)
            play(inputName).after(textRegister)
            play(inputEmail).after(inputName)
            play(inputPassword).after(inputEmail)
            play(buttonRegister).after(inputPassword)
            start()
        }
    }
}