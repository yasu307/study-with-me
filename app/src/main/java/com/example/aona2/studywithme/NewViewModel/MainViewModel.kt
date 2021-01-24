package com.example.aona2.studywithme.NewViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aona2.studywithme.Model.*
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

//    var currentUser: User? = null

    //HomeFragmentにて押した友達のuid 一人で開始したときはnull
    var clickedFriendUid: String? = null

    //自分が参加するルーム
    var myRoom: Room? = null

    //ViewModelからFragmentへのメッセージ
    val message: SharedFlow<MainMessage>
        get() = _message
    private val _message = MutableSharedFlow<MainMessage>()

    //ログインしているかチェック　結果をメッセージで返す
    fun checkLogin() = viewModelScope.launch(Dispatchers.IO){
        Log.d("MainViewModel","check login")
        if(!userRepo.checkLogin()) _message.emit(MainMessage.UserIsNotLogin)
    }

    //clickedFriendUidから名前を取得する
    fun getFriendName(): String{
        return userRepo.cashAllUsers[clickedFriendUid]?.userName ?: ""
    }

    //メッセージクラス　
    sealed class MainMessage{
        object UserIsNotLogin: MainMessage()
        object UserJoinRoom: MainMessage()
    }

    //友達の勉強に参加する 途中!!!!!
    fun startWithFriend(taskName: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.d("MainViewModel","start with friend")
//        myRoom = roomRepo.addMyInfoToRoom(clickedFriendUid)

    }

    //一人で勉強を開始する 結果をメッセージで返す
    fun startAlone(taskName: String) = viewModelScope.launch(Dispatchers.IO){
        Log.d("MainViewModel","start alone")
        myRoom = roomRepo.makeRoom()
        if(myRoom == null) return@launch
        val isSucceeded = currentStudyInfoRepo.saveCurrentStudyInfo(myRoom!!, taskName)
        if(!isSucceeded) return@launch
        _message.emit(MainMessage.UserJoinRoom)
        Log.d("MainViewModel", "finished")
    }

    //ログアウト処理　結果をメッセージで返す
    fun signOut() = viewModelScope.launch(Dispatchers.IO){
        userRepo.signOut()
        _message.emit(MainMessage.UserIsNotLogin)
    }
}