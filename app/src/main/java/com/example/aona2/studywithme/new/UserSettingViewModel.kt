package com.example.aona2.studywithme.new

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aona2.studywithme.Model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.*

class UserSettingViewModel (application: Application) : AndroidViewModel(application){
    private val repo  = UserRepository()
    var allUsers =  MutableLiveData<Map<String, User>>()

    val message: SharedFlow<Message>
    get() = _message
    private val _message = MutableSharedFlow<Message>()

    init {
        allUsers = repo.allUsers
    }

    fun register(email: String, password: String, userName: String, photoUri: Uri) = viewModelScope.launch(Dispatchers.IO){
        val isSucceeded = repo.register(email, password, userName, photoUri)
        if(isSucceeded){
            _message.emit(Message.Succeeded)
            Log.d("UserSettingViewModel","register is succeeded")
        }
        else{
            _message.emit(Message.Failed)
            Log.d("UserSettingViewModel","register is failed")
        }
    }

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

    sealed class Message{
        object Succeeded: Message()
        object Failed: Message()
        object LoginSucceeded: Message()
        object LoginFailed: Message()
    }
}