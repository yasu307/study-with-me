package com.example.aona2.studywithme

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
import androidx.navigation.fragment.findNavController
import com.example.aona2.studywithme.new.UserSettingViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment : Fragment() {
    private val viewModel: UserSettingViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allUsers.observe(this, Observer { users ->
            users?.forEach{
                Log.d("LoginFragment","all user is ${it.value.userName}")
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false).also {
            viewModel.message
                .onEach { onMessage(it) }
                .launchIn(lifecycleScope)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login_btn_loginFragment.setOnClickListener {
            performLogin()
        }

        backToRegister__textView_loginFragment.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun performLogin() {
        val email = email_editText_loginFragment.text.toString()
        val password = password_editText_loginFragment.text.toString()

        Log.d("Login", "Email is: $email")
        Log.d("Login", "Password is: $password")

        //emailとpasswordが空だと、ユーザー作成で落ちるので事前に判別
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Please enter text in email/password", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(email, password)
    }


    private fun onMessage(message: UserSettingViewModel.Message) {
        when (message) {
            is UserSettingViewModel.Message.LoginSucceeded -> onMessageSucceeded()
            is UserSettingViewModel.Message.LoginFailed -> onMessageFailed()
        }
    }

    private fun onMessageSucceeded(){
        Log.d("LoginFragment","on message succeeded")
//        val intent = Intent(activity, MainActivity::class.java)
//        startActivity(intent)
    }
    private fun onMessageFailed(){
        Log.d("LoginFragment","on message failed")
    }
}