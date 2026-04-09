package org.freedu.simplelocationshareb7.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.freedu.simplelocationshareb7.Repo.UserRepository

class AuthViewModel(private val repo: UserRepository) : ViewModel() {

    val loginResult = MutableLiveData<Pair<Boolean, String?>>()
    val registerResult = MutableLiveData<Pair<Boolean, String?>>()


    fun login(email: String, password: String) {

        repo.loginUser(email, password) { success, message ->
          loginResult.postValue(success to message)
        }
    }

    fun register(email: String, password: String) {
        repo.registerUser(email, password) { success, message ->
          registerResult.postValue(success to message)
        }

    }

}