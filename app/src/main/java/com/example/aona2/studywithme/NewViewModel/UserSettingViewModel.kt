package com.example.aona2.studywithme.NewViewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.Model.UserSettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

class UserSettingViewModel (application: Application) : AndroidViewModel(application){
    private val repo  = UserSettingRepository()
//    var allUsers =  MutableLiveData<Map<String, User>>()

    //ViewModelからFragmentへのメッセージ
    val message: SharedFlow<Message>
    get() = _message
    private val _message = MutableSharedFlow<Message>()

    //ユーザー登録処理　結果をメッセージで返す
    fun register(email: String, password: String, userName: String, imageUri: Uri) = viewModelScope.launch(Dispatchers.IO){
        val isSucceeded = repo.register(email, password, userName, imageUri)
        if(isSucceeded){
            _message.emit(Message.RegisterSucceeded)
            Log.d("UserSettingViewModel","register is succeeded")
        }
        else{
            _message.emit(Message.RegisterFailed)
            Log.d("UserSettingViewModel","register is failed")
        }
    }

    //ログイン処理　結果をメッセージで返す
    fun login(email: String, password: String) = viewModelScope.launch(Dispatchers.IO){
        val isSucceeded = repo.login(email, password)
        if(isSucceeded){
            Log.d("UserSettingViewModel", "login is succeeded")
            _message.emit(Message.LoginSucceeded)
        }
        else{
            Log.d("UserSettingViewModel", "login is failed")
            _message.emit(Message.LoginFailed)
        }
    }

    //メッセージクラス
    sealed class Message{
        object RegisterSucceeded: Message()
        object RegisterFailed: Message()
        object LoginSucceeded: Message()
        object LoginFailed: Message()
    }
}