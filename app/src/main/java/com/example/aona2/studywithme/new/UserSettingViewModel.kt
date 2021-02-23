package com.example.aona2.studywithme.new

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.User

class UserSettingViewModel (application: Application) : AndroidViewModel(application){
    private val repo :UserRepository
    var allUsers =  MutableLiveData<Map<String, User>>()

    init {
        Log.d("ThisIsTest","main view model init")
        repo = UserRepository()
        allUsers = repo.allUsers
    }

    fun insert(user: User) {
        Log.d("ThisIsTest", "insert")
        repo.insert(user)
    }
}