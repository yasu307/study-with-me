package com.example.aona2.studywithme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val pickPhotoRequestCode = 2

    private var photoUri: Uri? = null

    companion object {
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
                photoUri = it
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                select_phot_imageView_register.setImageBitmap(bitmap)

                select_photo_button_register.alpha = 0.0f
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // ログインしているかチェック
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d(TAG,"user is already login")
            //HomeActivityへ遷移する

      }
    }

    private fun performRegister(){
        val username = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d(TAG, "User name is: $username")
        Log.d(TAG, "Email is: $email")
        Log.d(TAG, "Password is: $password")

        //emailとpasswordが空だと、ユーザー作成で落ちるので事前に判別
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase Authenticationにてユーザーを作成する
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // サインイン成功
                    Log.d(TAG, "ユーザー作成成功")
                    val user = auth.currentUser
                    Log.d(TAG, "uid is ${user?.uid}")

                    uploadImageToFirebase()
                } else {
                    // サインイン失敗
                    Log.w(TAG, "ユーザー作成失敗", task.exception)
                    Toast.makeText(baseContext, "ユーザー作成失敗",
                        Toast.LENGTH_SHORT).show()
                }
            }

//        //HomeActivityへ遷移
//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
    }

    private fun uploadImageToFirebase(){
        val storage = Firebase.storage
        val storageRef = storage.reference

        //ユニークなファイル名を作成
        val filename = UUID.randomUUID().toString()

        val uploadImageRef = storageRef.child(filename)
        if(photoUri == null) return
        uploadImageRef.putFile(photoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG, "Image upload is success: ${it.metadata?.path}")
                    //ユーザー情報をデータベースに保存する
                    //アップロードしたファイルをダウンロードするUriを渡す
                    uploadImageRef.downloadUrl.addOnSuccessListener {
                        Log.d(TAG,"Image download uri is: ${it.toString()}")
                        saveUserInfoToFirebase(it.toString())
                    }
                }
                .addOnFailureListener{
                    Log.d(TAG, "Image upload is failure")
                }
    }

    private fun saveUserInfoToFirebase(imageUriToString: String){

    }
}