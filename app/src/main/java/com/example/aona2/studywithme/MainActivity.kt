package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //ログインされているか判別
//        ログインされていなかったらログインこの画面を表示する

        register_button_register.setOnClickListener {
            val username = username_edittext_register.text.toString()
            val email = email_edittext_register.text.toString()
            //Loginはそのままで大丈夫か？
            val password = password_edittext_register.text.toString()

            Log.d("MainActivity", "Email is: " + email)
            Log.d("MainActivity", "Password is: $password")

            //Firebase Authenticationにてユーザーを作成する

            //HomeActivityへ遷移
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //Login Activityは省略
    }
}