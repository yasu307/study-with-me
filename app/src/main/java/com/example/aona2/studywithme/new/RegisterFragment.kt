package com.example.aona2.studywithme.new

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aona2.studywithme.MainActivity
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.new.UserSettingViewModel.Message
import kotlinx.android.synthetic.main.fragment_register_flagment.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class RegisterFragment : Fragment() {
    private val viewModel: UserSettingViewModel by activityViewModels()

    private val pickPhotoRequestCode = 2

    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allUsers.observe(this, Observer { users ->
            users?.forEach{
                Log.d("RegisterFragment","all user is ${it.value.userName}")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_flagment, container, false).also {
            viewModel.message
                .onEach { onMessage(it) }
                .launchIn(lifecycleScope)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_button_registerFragment.setOnClickListener {
            performRegister()
        }

        haveAccount_textView_registerFragment.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        selectPhoto_btn_registerFragment.setOnClickListener {
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
            Log.d("RegisterFragment", "photo was selected")
            data?.data?.let {
                //uploadImageToFirebase()にて使用するため変数に代入しておく
                photoUri = it
                //選択した写真をViewに表示
                val contentResolver = requireActivity().contentResolver

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                selectPhoto_imageView_registerFragment.setImageBitmap(bitmap)
                //写真選択ボタンを透明に
                selectPhoto_btn_registerFragment.alpha = 0.0f
            }
        }
    }

    private fun performRegister(){
        val userName = username_edittext_registerFragment.text.toString()
        val email = email_edittext_registerFragment.text.toString()
        val password = password_edittext_registerFragment.text.toString()

        if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || photoUri == null) {
            Toast.makeText(activity, "入力内容を埋めてください", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(email, password, userName, photoUri!!)
    }

    private fun onMessage(message: Message) {
        when (message) {
            is Message.Succeeded -> onMessageSucceeded()
            is Message.Failed -> onMessageFailed()
        }
    }

    private fun onMessageSucceeded(){
        Log.d("RegisterFragment","on message succeeded")
//        val intent = Intent(activity, MainActivity::class.java)
//        startActivity(intent)
    }
    private fun onMessageFailed(){
        Log.d("RegisterFragment","on message failed")
    }
}