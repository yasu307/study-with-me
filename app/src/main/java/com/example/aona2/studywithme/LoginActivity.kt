package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //FirebaseAuthのインスタンスを取得
        auth = Firebase.auth

        login_button_login_activity.setOnClickListener {
            performLogin()
        }

        back_to_register_textView.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // ログインしているかチェック
        val currentUser = auth.currentUser
        if(currentUser != null){
            Log.d("Login","user is already login")
            //HomeActivityへ遷移する

        }
    }

    private fun performLogin(){
        val email = email_edittext_login_activity.text.toString()
        val password = password_edittext__login_activity.text.toString()

        Log.d("Login", "Email is: $email")
        Log.d("Login", "Password is: $password")

        //emailとpasswordが空だと、ユーザー作成で落ちるので事前に判別
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase認証
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // サインイン成功
                    Log.d("Login", "ログイン成功")
                    val user = auth.currentUser
                    Log.d("Login","uid is: ${user?.uid}")
                } else {
                    // サインイン失敗
                    Log.w("Login", "ログイン失敗", task.exception)
                    Toast.makeText(baseContext, "ログイン失敗",
                        Toast.LENGTH_SHORT).show()
                }
            }

//        //HomeActivityへ遷移
//        val intent = Intent(this, HomeActivity::class.java)
//        startActivity(intent)
    }
}