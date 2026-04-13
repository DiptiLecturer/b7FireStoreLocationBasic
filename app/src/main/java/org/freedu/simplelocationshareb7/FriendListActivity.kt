package org.freedu.simplelocationshareb7

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.freedu.simplelocationshareb7.Repo.UserRepository
import org.freedu.simplelocationshareb7.databinding.ActivityFriendListBinding

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private val repo = UserRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}