package org.freedu.simplelocationshareb7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.freedu.simplelocationshareb7.Repo.UserRepository
import org.freedu.simplelocationshareb7.databinding.ActivityAuthBinding
import org.freedu.simplelocationshareb7.viewModels.AuthViewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    private val repo = UserRepository()

    private val viewModel: AuthViewModel by viewModels<AuthViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo) as T
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                return@setOnClickListener
            }
            viewModel.login(email, password)

        }
        binding.btnRegister.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                return@setOnClickListener
            }

            viewModel.register(email, password)
        }
        viewModel.registerResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Registration Filed", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.loginResult.observe(this) { (success, message) ->
            if (success) {
                Toast.makeText(this, "Logged In Successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Logged In Filed", Toast.LENGTH_SHORT).show()
            }
        }


    }
}