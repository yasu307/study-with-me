package com.example.aona2.studywithme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aona2.studywithme.Model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel (application: Application) : AndroidViewModel(application){
    private val userRepo  = UserRepository()
    val allUsers =  userRepo.allUsers
    private val roomRepo = RoomRepository()

    private val currentStudyInfoRepo = CurrentStudyInfoRepository()
    val currentStudyInfos = currentStudyInfoRepo.currentStudyInfos

    var currentUser: User? = null

    val message: SharedFlow<MainMessage>
        get() = _message
    private val _message = MutableSharedFlow<MainMessage>()

    init {
        checkLogin()
    }

    private fun checkLogin() = viewModelScope.launch(Dispatchers.IO){
        if(!userRepo.checkLogin()) _message.emit(MainMessage.UserIsNotLogin)
    }

    sealed class MainMessage{
        object UserIsNotLogin: MainMessage()
    }
}