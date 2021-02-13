package com.example.aona2.studywithme.Model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.User
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*

//RegisterとLogin時に使用する
class UserSettingRepository {
    //ログインする　ログイン成功したか？を返す
    suspend fun login(email: String, password: String): Boolean{
        var isSucceeded = false
        try{
            Firebase.auth.signInWithEmailAndPassword(email,password).await()
            isSucceeded = true
        }catch (e: FirebaseAuthException){
            return false
        }
        return isSucceeded
    }

    //ユーザー登録時の処理をそれぞれ順番に呼び出す 登録成功したか？を返す
    suspend fun register(email: String, password: String, userName: String, imageUri: Uri) :Boolean{
        Firebase.auth.signOut()

        val uid = createUser(email, password) ?: return false
        val imageReference = uploadUserImage(imageUri) ?: return false
        val userImageUrl = getUserImageUrl(imageReference) ?: return false
        return saveUser(uid, userName, userImageUrl)
    }

    //emailとpasswordからユーザーを作成する
    private suspend fun createUser(email: String, password: String): String?{
        var uid: String? = null
        Firebase.auth.signOut()
        Log.d("UserRepository","create user")
        try{
            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                uid = Firebase.auth.currentUser?.uid
            }.await()
        }catch (e: FirebaseAuthException){
            Log.d("UserRepository","catch exception")
            return null
        }
        Log.d("UserRepository","this is end of create user")
        return uid
    }

    //UserImageをFirebaseStorageに保存する
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

    //保存したUserImageを取得するUrlを取得する
    private suspend fun getUserImageUrl(imageRef: StorageReference) :String?{
        var userImageUrl: String? = null
        imageRef.downloadUrl.addOnSuccessListener {
            userImageUrl = it.toString()
        }.await()
        return userImageUrl
    }

    //RTDBにユーザーを保存する
    private suspend fun saveUser(uid: String, userName: String, userImageUrl: String): Boolean{
        var isSucceeded = false
        val ref = Firebase.database.getReference("users/$uid")

        val user = User(uid , userName, userImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                isSucceeded = true
            }.await()
        return isSucceeded
    }
}