package com.example.aona2.studywithme.new

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.aona2.studywithme.R
import androidx.fragment.app.activityViewModels
import com.example.aona2.studywithme.Model.User
import kotlinx.android.synthetic.main.fragment_register_flagment.*


class RegisterFragment : Fragment() {
    private val viewModel: UserSettingViewModel by activityViewModels()

    private var testNum = 0

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
        insert_btn_registerFragment.setOnClickListener {
            Log.d("ThisIsTest","set on click listener")
            val user = User("Second$testNum","Second$testNum","Second$testNum")
            viewModel.insert(user)
            testNum++
        }
    }
}