package org.freedu.simplelocationshareb7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.freedu.simplelocationshareb7.Repo.UserRepository
import org.freedu.simplelocationshareb7.databinding.ActivityMapsBinding
import org.freedu.simplelocationshareb7.viewModels.MapsViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private val repo = UserRepository()

    private lateinit var map: GoogleMap
    private val viewModel by viewModels<MapsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MapsViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this) ?: run {
            Toast.makeText(this, "Map not found", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val showAll = intent.getBooleanExtra("showAll", false)
        val userId = intent.getStringExtra("uid")

        if (showAll) {
            viewModel.loadAllUsers()
        } else if (userId != null) {
            viewModel.loadSingleUser(userId)
        }

        observeData()
    }
    private fun observeData() {

        viewModel.user.observe(this) { user ->
            user?.let {
                if (it.latitude != null && it.longitude != null) {
                    val pos = LatLng(it.latitude, it.longitude)

                    map.clear()
                    map.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(it.username.ifEmpty { it.email })
                    )

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
                }
            }
        }

        viewModel.userList.observe(this) { list ->
            map.clear()

            list.forEach {
                if (it.latitude != null && it.longitude != null) {
                    val pos = LatLng(it.latitude, it.longitude)

                    map.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(it.username.ifEmpty { it.email })
                    )
                }
            }

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(23.7548, 90.3765), 15f)
            )
        }
    }
    private fun loadSingleUser(userId: String) {
        repo.getUserById(userId) { user ->
            user?.let {
                val lat = it.latitude
                val lng = it.longitude

                if (lat != null && lng != null) {
                    val pos = com.google.android.gms.maps.model.LatLng(lat, lng)
                    map.clear()
                    map.addMarker(
                        com.google.android.gms.maps.model.MarkerOptions()
                            .position(pos)
                            .title(it.username.ifEmpty { it.email })
                    )
                    map.moveCamera(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(pos, 15f)
                    )
                }
            }

        }
    }

    private fun loadAllUsers() {
        repo.getAllUsers { list ->
            map.clear()

            list.forEach { users ->
                if (users.latitude != null && users.longitude != null) {
                    val pos = LatLng(users.latitude, users.longitude)
                    map.addMarker(
                        com.google.android.gms.maps.model.MarkerOptions()
                            .position(pos)
                            .title(users.username.ifEmpty { users.email })
                    )
                }


            }
            map.moveCamera(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(LatLng(23.7548,90.3765), 15f)
            )

        }
    }

}