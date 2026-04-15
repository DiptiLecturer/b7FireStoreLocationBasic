package org.freedu.simplelocationshareb7

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import org.freedu.simplelocationshareb7.Repo.UserRepository
import org.freedu.simplelocationshareb7.databinding.ActivityFriendListBinding
import org.freedu.simplelocationshareb7.viewModels.FriendListViewModel

class FriendListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendListBinding
    private val repo = UserRepository()

    private val viewModel by viewModels<FriendListViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FriendListViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //step 1 = setup the adapter

        val adapter = UserAdapter { selectedUser ->
            Toast.makeText(this@FriendListActivity, selectedUser.email, Toast.LENGTH_SHORT).show()

        }
        binding.userRecycler.layoutManager = LinearLayoutManager(this)
        binding.userRecycler.setHasFixedSize(true)
        binding.userRecycler.adapter = adapter

        //step2 fetch user

        viewModel.fetchUsers()

        //3 observe data and remove current user

        viewModel.userList.observe(this) { list ->
            val currentUid = repo.getCurrentUserId()
            val filteredOut = list.filter { it.userId != currentUid }
            adapter.submitList(filteredOut)
        }

        loadCurrentUser()
        checkLocationPermission()

    }

    @SuppressLint("SetTextI18n")
    fun loadCurrentUser() {
        val uid = repo.getCurrentUserId() ?: return

        repo.getUserById(uid) { user ->

            user?.let {
                binding.tvMyProfileName.text = it.username
                binding.tvMyProfileEmail.text = it.email
                binding.tvMyProfileLat.text = it.latitude?.toString() ?: "No Latitude"
                binding.tvMyProfileLng.text = it.longitude?.toString() ?: "No Longitude"
            } ?: run {

                binding.tvMyProfileName.text = "User not found"
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            updateLocationAutomatically()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateLocationAutomatically()
        }

    }

    private fun updateLocationAutomatically() {
        repo.updateLocationAuto(this) { success ->
            if (success) {
                // Refresh the header so the user sees their new Lat/Lng
                loadCurrentUser()
            } else {
                Toast.makeText(this, "Location update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}