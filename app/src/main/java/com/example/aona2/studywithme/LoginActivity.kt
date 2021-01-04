package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login_activity.setOnClickListener {
            val email = email_edittext_login_activity.text.toString()
            val password = password_edittext__login_activity.text.toString()

            Log.d("Login", "Email is: $email")
            Log.d("Login", "Password is: $password")

            //Firebase認証

            //HomeActivityへ遷移
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        back_to_register_textView.setOnClickListener {
            finish()
        }
    }
}