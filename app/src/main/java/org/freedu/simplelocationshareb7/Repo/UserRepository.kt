package org.freedu.simplelocationshareb7.Repo

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


}
