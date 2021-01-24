package com.example.aona2.studywithme.Model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository {
    //Map<uid, user>
    val allUsers = MutableLiveData<Map<String, User>>()
    //Mapとして使用するためのキャッシュ
    var cashAllUsers = mapOf<String, User>()

    init{
        getAllUsers()
    }

    //すべてのユーザーをFirebaseから取得する　分ける必要はないので後にまとめる
    private fun getAllUsers(){
        loadAllUsers()
    }

    //すべてのユーザーをFirebaseから取得する
    private fun loadAllUsers(){
        val ref = Firebase.database.getReference("/users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("UserRepository","on data change")
                val users = mutableMapOf<String, User>()
                for(userSnapShot in snapshot.children){
                    val user = userSnapShot.getValue(User::class.java) ?: continue
                    users[user.uid] = user
                }
                //LiveDataを更新
                allUsers.value = users
                //キャッシュを更新
                cashAllUsers = users
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("UserRepository","on data cancelled")
            }
        })
    }

    //ログインしているかチェック ログインしているか？を返す
    fun checkLogin(): Boolean{
        val currentUser = Firebase.auth.currentUser
        if(currentUser != null){
            Log.d("UserRepository","current user is ${currentUser.uid}")
            return true
        }
        return false
    }

    //サインアウトする
    fun signOut(){
        Firebase.auth.signOut()
    }
}