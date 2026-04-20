package org.freedu.simplelocationshareb7

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
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

    private var isMenuOpen = false   // ✅ USED for FAB menu toggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ================= STEP 1: RecyclerView =================
        val adapter = UserAdapter { selectedUser ->
            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra("uid", selectedUser.userId)   // ✅ correct key
            })
        }

        binding.userRecycler.layoutManager = LinearLayoutManager(this)
        binding.userRecycler.setHasFixedSize(true)
        binding.userRecycler.adapter = adapter

        // ================= STEP 2: Fetch users =================
        viewModel.fetchUsers()

        // ================= STEP 3: Observe users =================
        viewModel.userList.observe(this) { list ->
            val currentUid = repo.getCurrentUserId()

            // remove current user
            adapter.submitList(list.filter { it.userId != currentUid })
        }

        // ================= STEP 4: Load profile =================
        loadCurrentUser()

        // ================= STEP 5: Location =================
        checkLocationPermission()

        // ================= STEP 6: PROFILE CLICK =================
        binding.layoutMyProfile.setOnClickListener {
            val uid = repo.getCurrentUserId() ?: return@setOnClickListener

            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra("uid", uid)   // ✅ show ONLY my location
            })
        }

        // ================= FAB MAIN =================
        binding.fabMain.setOnClickListener {
            if (isMenuOpen) closeMenu() else openMenu()
        }

        // ================= FAB PROFILE =================
        binding.fabProfile.setOnClickListener {
            val uid = repo.getCurrentUserId() ?: return@setOnClickListener
            val email = repo.getCurrentUserEmail() ?: ""

            startActivity(Intent(this, MyProfileActivity::class.java).apply {
                putExtra("uid", uid)
                putExtra("email", email)
            })

            closeMenu()
        }

        // ================= FAB MAP =================
        binding.fabShowMap.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java).apply {
                putExtra("showAll", true)
            })
            closeMenu()
        }

        // ================= FAB LOGOUT =================
        binding.fabLogout.setOnClickListener {
            viewModel.logOut()   // ⚠️ TODO: make sure ViewModel has this function

            startActivity(Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    // ================= HEADER =================
    @SuppressLint("SetTextI18n")
    private fun loadCurrentUser() {
        val uid = repo.getCurrentUserId() ?: return

        repo.getUserById(uid) { user ->
            user?.let {
                binding.tvMyProfileName.text = it.username.ifEmpty { "No Name" }
                binding.tvMyProfileEmail.text = it.email

                binding.tvMyProfileLat.text = "Lat: ${it.latitude ?: 0.0}"
                binding.tvMyProfileLng.text = "Lng: ${it.longitude ?: 0.0}"
            } ?: run {
                binding.tvMyProfileName.text = "User not found"

                // ⚠️ TODO: optionally show Toast or log error
            }
        }
    }

    // ================= LOCATION =================
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {

            // ⚠️ TODO: handle "permission denied permanently" case (important for real apps)

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
        } else {
            updateLocationAutomatically()
        }
    }

    // ================= PERMISSION RESULT =================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            updateLocationAutomatically()
        } else {

            // ⚠️ TODO: show message if user denies permission
            // Example: Toast or dialog explaining why location is needed
        }
    }

    // ================= AUTO LOCATION =================
    private fun updateLocationAutomatically() {
        repo.updateLocationAuto(this) { success ->
            if (success) {
                loadCurrentUser()
            } else {
                Toast.makeText(this, "Location update failed", Toast.LENGTH_SHORT).show()

                // ⚠️ TODO: handle location null case (GPS off)
            }
        }
    }

    // ================= FAB MENU =================
    private fun openMenu() {
        binding.fabProfile.visibility = View.VISIBLE
        binding.fabShowMap.visibility = View.VISIBLE
        binding.fabLogout.visibility = View.VISIBLE

        // ⚠️ TODO: add animation (rotation or scale)
        // Example: fabMain.animate().rotation(45f)

        isMenuOpen = true
    }

    private fun closeMenu() {
        binding.fabProfile.visibility = View.GONE
        binding.fabShowMap.visibility = View.GONE
        binding.fabLogout.visibility = View.GONE


        isMenuOpen = false
    }

    override fun onResume() {
        super.onResume()
        isMenuOpen = false
        closeMenu()
        viewModel.fetchUsers()
        loadCurrentUser()
    }
}
