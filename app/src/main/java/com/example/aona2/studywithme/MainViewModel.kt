package com.example.aona2.studywithme

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel (application: Application) : AndroidViewModel(application){
    val userRepo  = UserRepository()
    val allUsers =  userRepo.allUsers

    val roomRepo = RoomRepository()
    val allRooms = roomRepo.allRooms

    val currentStudyInfoRepo = CurrentStudyInfoRepository()
    val currentStudyInfos = currentStudyInfoRepo.currentStudyInfos

    var currentUser: User? = null

    var clickedFriendUid: String? = null

    var myRoom: Room? = null

    val message: SharedFlow<MainMessage>
        get() = _message
    private val _message = MutableSharedFlow<MainMessage>()


    fun checkLogin() = viewModelScope.launch(Dispatchers.IO){
        Log.d("MainViewModel","check login")
        if(!userRepo.checkLogin()) _message.emit(MainMessage.UserIsNotLogin)
    }

    fun getFriendName(): String{
        return userRepo.cashAllUsers[clickedFriendUid]?.userName ?: ""
    }

    sealed class MainMessage{
        object UserIsNotLogin: MainMessage()
        object UserJoinRoom: MainMessage()
    }

    fun startWithFriend(taskName: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("MainViewModel","start with friend")
//        myRoom = roomRepo.addMyInfoToRoom(clickedFriendUid)

    }

    fun startAlone(taskName: String) = viewModelScope.launch(Dispatchers.IO){
        Log.d("MainViewModel","start alone")
        myRoom = roomRepo.makeRoom()
        if(myRoom == null) return@launch
        val isSucceeded = currentStudyInfoRepo.saveCurrentStudyInfo(myRoom!!, taskName)
        if(!isSucceeded) return@launch
        _message.emit(MainMessage.UserJoinRoom)
        Log.d("MainViewModel", "finished")
    }

    fun signOut() = viewModelScope.launch(Dispatchers.IO){
        userRepo.signOut()
        _message.emit(MainMessage.UserIsNotLogin)
    }
}