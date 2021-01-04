package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

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
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Log.d(TAG, "uid is ${user?.uid}")
                } else {
                    // サインイン失敗
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "サインイン失敗",
                        Toast.LENGTH_SHORT).show()
                }
            }

//        //HomeActivityへ遷移
//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
    }
}