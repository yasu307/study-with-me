package com.example.aona2.studywithme.View

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val pickPhotoRequestCode = 2

    private var imageUri: Uri? = null

    companion object {
        //デバッグ時のコメント用
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FirebaseAuthのインスタンスを取得
        auth = Firebase.auth

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        select_photo_button_register.setOnClickListener {
            //写真選択用のintentに遷移する
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, pickPhotoRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //写真選択のintentから帰ってきたときの処理
        if (requestCode == pickPhotoRequestCode && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "photo was selected")
            data?.data?.let {
                //uploadImageToFirebase()にて使用するため変数に代入しておく
                imageUri = it
                //選択した写真をViewに表示
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                select_photo_imageView_register.setImageBitmap(bitmap)
                //写真選択ボタンを透明に
                select_photo_button_register.alpha = 0.0f
            }
        }
    }

    //FirebaseAuthにてユーザーを作成する
    private fun performRegister(){
        val userName = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d(TAG, "Email is: $email")
        Log.d(TAG, "Password is: $password")

        //emailとpasswordが空だと、ユーザー作成で落ちるので事前に判別
        //ついでにUsernameとアイコンもチェック
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || imageUri == null) {
            Toast.makeText(this, "入力内容を埋めてください", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase Authenticationにてユーザーを作成する
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "ユーザー作成成功")
                    //成功したら次のステップ（アイコンの保存）へ
                    uploadImageToFirebase()
                } else {
                    // サインイン失敗
                    Log.w(TAG, "ユーザー作成失敗", task.exception)
                    Toast.makeText(baseContext, "ユーザー作成失敗", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //ユーザーアイコンをFirebaseStorageに保存
    private fun uploadImageToFirebase(){
        //ユニークなファイル名を作成
        val filename = UUID.randomUUID().toString()

        val storageRef = Firebase.storage.reference
        val uploadImageRef = storageRef.child(filename)
        if(imageUri == null) return
        uploadImageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Image upload is success: ${it.metadata?.path}")
                    //アップロードしたファイルをダウンロードするUriを渡す
                    uploadImageRef.downloadUrl.addOnSuccessListener {
                        Log.d(TAG,"Image download uri is: ${it.toString()}")
                        //成功したら次のステップ（ユーザー情報の保存）へ
                        saveUserInfoToFirebase(it.toString())
                    }
                }
                .addOnFailureListener{
                    Log.d(TAG, "Image upload is failure")
                    Toast.makeText(baseContext, "画像アップロード失敗", Toast.LENGTH_SHORT).show()
                }
    }

    //ユーザー情報をFirebaseに保存
    private fun saveUserInfoToFirebase(userImageView: String){
        val uid = auth.currentUser?.uid

        val database = Firebase.database
        val ref = database.getReference("users/$uid")

        val user = User(uid ?: "", username_edittext_register.text.toString(), userImageView)
        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d(TAG, "save user to Firebase is success")
                    //成功したらHomeActivityへ遷移する
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "save user to Firebase is failure")
                    Toast.makeText(baseContext, "ユーザー保存失敗", Toast.LENGTH_SHORT).show()
                }
    }
}