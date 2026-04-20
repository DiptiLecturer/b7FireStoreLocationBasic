package org.freedu.simplelocationshareb7.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.freedu.simplelocationshareb7.AppUsers
import org.freedu.simplelocationshareb7.Repo.UserRepository

class MapsViewModel(private val repo: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<AppUsers?>()
    val user: LiveData<AppUsers?> = _user

    private val _userList = MutableLiveData<List<AppUsers>>()
    val userList: LiveData<List<AppUsers>> = _userList

    fun loadSingleUser(userId: String) {
        repo.getUserById(userId) {
            _user.postValue(it)
        }
    }

    fun loadAllUsers() {
        repo.getAllUsers {
            _userList.postValue(it)
        }
    }
}