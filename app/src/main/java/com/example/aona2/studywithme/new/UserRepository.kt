package com.example.aona2.studywithme.new

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.View.HomeActivity
import com.example.aona2.studywithme.View.MainActivity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.FirebaseCommonRegistrar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.tasks.await
import java.util.*

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

    suspend fun register(email: String, password: String, userName: String, photoUri: Uri) :Boolean{
        Firebase.auth.signOut()

        val uid = createUser(email, password) ?: return false
        val imageReference = uploadUserImage(photoUri) ?: return false
        val userImageUrl = getUserImageUrl(imageReference) ?: return false
        return saveUser(uid, userName, userImageUrl)
    }

    private suspend fun createUser(email: String, password: String): String?{
        var uid: String? = null
        Firebase.auth.signOut()
        Log.d("UserRepository","create user")
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            uid = Firebase.auth.currentUser?.uid
        }.await()
        Log.d("UserRepository","this is end of create user")
        return uid
    }

    private suspend fun uploadUserImage(imageUri: Uri) :StorageReference?{
        var imageRef: StorageReference? = null
        Log.d("UserRepository","upload image to firebase")

        //ユニークなファイル名を作成
        val filename = UUID.randomUUID().toString()

        val storageRef = Firebase.storage.reference
        val uploadImageRef = storageRef.child(filename)
        uploadImageRef.putFile(imageUri)
            .addOnSuccessListener {
                Log.d("UserRepository", "Image upload is success: ${it.metadata?.path}")
                //アップロードしたファイルのreferenceを返す
                imageRef = uploadImageRef
            }.await()
        Log.d("UserRepository","this is the end of upload image")
        return imageRef
    }

    private suspend fun getUserImageUrl(imageRef: StorageReference) :String?{
        var userImageUrl: String? = null
        imageRef.downloadUrl.addOnSuccessListener {
            userImageUrl = it.toString()
        }.await()
        return userImageUrl
    }

    private suspend fun saveUser(uid: String, userName: String, userImageUrl: String): Boolean{
        var isSucceeded: Boolean = false
        val ref = Firebase.database.getReference("users/$uid")

        val user = User(uid , userName, userImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                isSucceeded = true
            }.await()
        return isSucceeded
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