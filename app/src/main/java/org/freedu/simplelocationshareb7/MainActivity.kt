package org.freedu.simplelocationshareb7

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import org.freedu.simplelocationshareb7.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentLat = 0.0
    private var currentLng = 0.0

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        getLocation()

        binding.btnGetLocation.setOnClickListener {
            getLocation()
        }

        binding.btnSaveLocation.setOnClickListener {
            saveLocation()
        }
        binding.btnShowMap.setOnClickListener {

            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("latitude", currentLat)
            intent.putExtra("longitude", currentLng)
            startActivity(intent)

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
            getLocation()
        }
    }

    private fun saveLocation() {
        val data = hashMapOf(
            "latitude" to currentLat,
            "longitude" to currentLng
        )
        db.collection("locations")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save location", Toast.LENGTH_SHORT).show()
            }
    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        // ✅ Permission granted → get location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude
                currentLng = location.longitude
                Toast.makeText(this, "Lat: $currentLat, Lng: $currentLng", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }

        }
    }
}