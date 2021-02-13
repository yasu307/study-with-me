package com.example.aona2.studywithme.NewView

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.aona2.studywithme.NewViewModel.UserSettingViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.example.aona2.studywithme.NewViewModel.UserSettingViewModel.Message
import com.example.aona2.studywithme.R

class LoginFragment : Fragment() {
    private val viewModel: UserSettingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel.allUsers.observe(this, Observer { users ->
//            users?.forEach{
//                Log.d("LoginFragment","all user is ${it.value.userName}")
//            }
//        })

        //ViewModelから送られてくるmessageを監視する
        viewModel.message.onEach { onMessage(it) }.launchIn(lifecycleScope)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ログインボタンが押されたとき　
        login_btn_loginFragment.setOnClickListener {
            performLogin()
        }

        //登録画面に戻る　が押されたとき　戻るボタンと同じ処理を追加する
//        backToRegister__textView_loginFragment.setOnClickListener {
//        }
    }

    //ログイン処理
    private fun performLogin() {
        val email = email_editText_loginFragment.text.toString()
        val password = password_editText_loginFragment.text.toString()

        Log.d("Login", "Email is: $email")
        Log.d("Login", "Password is: $password")

        //emailとpasswordが空だと、ユーザー作成で落ちるので事前に判別
        //ViewModelに移したほうがよさそう
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(email, password)
    }

    //ViewModelから送られてくるメッセージに対応する処理を呼び出す
    private fun onMessage(message: Message) {
        when (message) {
            is Message.LoginSucceeded -> onMessageSucceeded()
            is Message.LoginFailed -> onMessageFailed()
        }
    }

    //メッセージに対応する処理
    //ログインが成功したとき　MainActivityに遷移
    @Suppress("UNUSED_PARAMETER")
    private fun onMessageSucceeded(){
        Log.d("LoginFragment","on message succeeded")
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
    //ログインが失敗したとき　Toastを表示する処理を追加
    @Suppress("UNUSED_PARAMETER")
    private fun onMessageFailed(){
        Log.d("LoginFragment","on message failed")
    }
}