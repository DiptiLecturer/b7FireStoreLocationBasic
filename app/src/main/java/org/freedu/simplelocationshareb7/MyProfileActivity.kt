package org.freedu.simplelocationshareb7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.freedu.simplelocationshareb7.Repo.UserRepository
import org.freedu.simplelocationshareb7.databinding.ActivityMyProfileBinding
import org.freedu.simplelocationshareb7.viewModels.MyProfileViewModel

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private val repo = UserRepository()

    private val viewModel by viewModels<MyProfileViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MyProfileViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val uid = intent.getStringExtra("uid") ?: return
        val email = intent.getStringExtra("email") ?: ""

        binding.email.text = email
        binding.btnUpdateUsername.setOnClickListener {
            val newName = binding.edtUsername.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(this, "Enter username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateUsername(uid, newName) { success ->
                if (success) {
                    Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
                    finish() // go back to FriendList
                } else {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 🔹 Load user
        viewModel.loadUser(uid)

        viewModel.user.observe(this) { user ->
            user?.let {
                binding.edtUsername.setText(it.username)
            }
        }
    }
}