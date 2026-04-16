package org.freedu.simplelocationshareb7.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.freedu.simplelocationshareb7.AppUsers
import org.freedu.simplelocationshareb7.Repo.UserRepository


class FriendListViewModel(private val repo: UserRepository)
    : ViewModel() {
    private val _userList = MutableLiveData<List<AppUsers>>()
    val userList: LiveData<List<AppUsers>> get() = _userList

    fun fetchUsers(){
        repo.getAllUsers { list ->
            _userList.postValue(list)
        }
    }
    //logout

    fun logOut(){
        repo.logOut()
    }

}