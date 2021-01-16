package com.example.aona2.studywithme.Model

//ユーザー情報
data class User(val uid: String, val userName: String, val userImageView: String){
    constructor() : this("","","")
}