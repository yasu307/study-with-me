package com.example.aona2.studywithme.new

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository {
    //Map<uid, user>
    val allUsers = MutableLiveData<Map<String, User>>()

    init{
        getAllUsers()
    }

    fun getAllUsers(): LiveData<Map<String, User>>{
        loadAllUsers()
        return allUsers
    }

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
                allUsers.value = users
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("UserRepository","on data cancelled")
            }
        })
    }

    fun insert(user: User){
        val ref = Firebase.database.getReference("/users/${user.uid}")
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("UserRepository", "set user value is success")
            }
            .addOnFailureListener {
                Log.d("UserRepository", "set user value is failure")
            }
    }
}