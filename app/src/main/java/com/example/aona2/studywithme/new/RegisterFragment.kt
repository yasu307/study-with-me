package com.example.aona2.studywithme.new

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.aona2.studywithme.R
import androidx.fragment.app.activityViewModels
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.View.LoginActivity
import com.example.aona2.studywithme.View.MainActivity
import kotlinx.android.synthetic.main.fragment_register_flagment.*
import kotlinx.android.synthetic.main.fragment_register_flagment.haveAccount_textView_registerFragment
import kotlinx.android.synthetic.main.fragment_register_flagment.register_button_registerFragment
import kotlinx.android.synthetic.main.fragment_register_flagment.selectPhoto_btn_registerFragment


class RegisterFragment : Fragment() {
    private val viewModel: UserSettingViewModel by activityViewModels()

    private var testNum = 0

    private val pickPhotoRequestCode = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allUsers.observe(this, Observer { users ->
            users?.forEach{
                Log.d("ThisIsTest","all user is ${it.value.userName}")
            }
        })
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
        register_button_registerFragment.setOnClickListener {
//            performRegister()
        }

//        haveAccount_textView_registerFragment.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        }

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
            Log.d(MainActivity.TAG, "photo was selected")
            data?.data?.let {
//                //uploadImageToFirebase()にて使用するため変数に代入しておく
//                photoUri = it
                //選択した写真をViewに表示
                val contentResolver = requireActivity().contentResolver

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                selectPhoto_imageView_registerFragment.setImageBitmap(bitmap)
                //写真選択ボタンを透明に
                selectPhoto_btn_registerFragment.alpha = 0.0f
            }
        }
    }
}