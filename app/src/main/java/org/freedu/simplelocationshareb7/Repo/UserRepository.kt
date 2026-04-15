package org.freedu.simplelocationshareb7.Repo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.freedu.simplelocationshareb7.AppUsers


class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    fun registerUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                val userName = email.substringBefore("@")
                val user = AppUsers(
                    userId = userId,
                    username = userName,
                    email = email
                )
                db.collection("users").document(userId).set(user)
                    .addOnSuccessListener {
                        onComplete(true, null)
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, e.message)
                    }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }


    }

    fun getAllUsers(onComplete: (List<AppUsers>) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(AppUsers::class.java)
                }
                onComplete(list)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun getCurrentUserEmail(): String? = auth.currentUser?.email

    fun getUserById(userId: String, callback: (AppUsers?) -> Unit) {

        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(AppUsers::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }

    }

    fun updateLocation(
        userId: String,
        lat: Double,
        lng: Double,
        onComplete: (Boolean) -> Unit
    ) {
        db.collection("users").document(userId).update(
            mapOf(
                "latitude" to lat,
                "longitude" to lng

            )
        ).addOnSuccessListener {
            onComplete.invoke(true)
        }
            .addOnFailureListener { e ->
                onComplete.invoke(false)
            }

    }

    fun updateLocationAuto(context: Context, onComplete: (Boolean) -> Unit) {

        val fused = LocationServices.getFusedLocationProviderClient(context)

        val userId = getCurrentUserId() ?: return

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onComplete(false)
            return
        }
        fused.lastLocation.addOnSuccessListener {loc->
            if (loc!=null){
                updateLocation(userId,loc.latitude,loc.longitude,onComplete)
                onComplete(true)
            }else{
                onComplete(false)
            }
        }
    }


}
