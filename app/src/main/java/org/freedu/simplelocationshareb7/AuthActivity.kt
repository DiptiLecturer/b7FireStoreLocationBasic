package org.freedu.simplelocationshareb7

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.freedu.simplelocationshareb7.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}