package com.example.aona2.studywithme.NewView

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.NewViewModel.UserSettingViewModel
import com.example.aona2.studywithme.NewViewModel.UserSettingViewModel.Message
import kotlinx.android.synthetic.main.fragment_register_flagment.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterFragment : Fragment() {
    private val viewModel: UserSettingViewModel by activityViewModels()

    private val pickPhotoRequestCode = 2

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel.allUsers.observe(this, Observer { users ->
//            users?.forEach{
//                Log.d("RegisterFragment","all user is ${it.value.userName}")
//            }
//        })

        //ViewModelから送られてくるmessageを監視する
        viewModel.message.onEach { onMessage(it) }.launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_flagment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //登録ボタンが押されたとき
        register_button_registerFragment.setOnClickListener {
            performRegister()
        }

        //アカウントを持っていますか？が押されたとき
        haveAccount_textView_registerFragment.setOnClickListener {
            //Fragmentの遷移
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        //アイコンを選択ボタン　が押されたとき
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
                //performRegister()にて使用するため変数に代入しておく
                imageUri = it
                //選択した写真をViewに表示
                val contentResolver = requireActivity().contentResolver
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                selectPhoto_imageView_registerFragment.setImageBitmap(bitmap)
                //写真選択ボタンを透明に
                selectPhoto_btn_registerFragment.alpha = 0.0f
            }
        }
    }

    //ユーザーを登録
    private fun performRegister(){
        val userName = username_edittext_registerFragment.text.toString()
        val email = email_edittext_registerFragment.text.toString()
        val password = password_edittext_registerFragment.text.toString()

        //email,password,userName,imageUriが空かチェック
        //ViewModelに移したほうがよさそう
        if (email.isEmpty() || password.isEmpty() || userName.isEmpty() || imageUri == null) {
            Toast.makeText(activity, "入力内容を埋めてください", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(email, password, userName, imageUri!!)
    }

    //ViewModelから送られてくるメッセージに対応する処理を呼び出す
    private fun onMessage(message: Message) {
        when (message) {
            is Message.RegisterSucceeded -> onMessageSucceeded()
            is Message.RegisterFailed -> onMessageFailed()
        }
    }

    //メッセージに対応する処理
    //ユーザー登録が成功したとき　MainActivityに遷移
    @Suppress("UNUSED_PARAMETER")
    private fun onMessageSucceeded(){
        Log.d("RegisterFragment","on message succeeded")
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }
    //ユーザー登録が失敗したとき　Toastを表示する処理を追加
    @Suppress("UNUSED_PARAMETER")
    private fun onMessageFailed(){
        Log.d("RegisterFragment","on message failed")
    }
}