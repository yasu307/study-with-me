package com.example.aona2.studywithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.aona2.studywithme.MainViewModel.MainMessage
import com.example.aona2.studywithme.new.UserSettingViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.allUsers.observe(this, Observer { users ->
            users?.forEach{
                Log.d("HomeFragment","all user is ${it.value.userName}")
            }
        })
        viewModel.currentStudyInfos.observe(this, Observer { currentStudyInfos ->
            currentStudyInfos?.forEach{
                Log.d("HomeFragment","current study info is ${it.uid}")
            }
        })

        viewModel.message.onEach { onMessage(it) }.launchIn(lifecycleScope)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun onMessage(message: MainViewModel.MainMessage) {
        when (message) {
            is MainMessage -> onMessageUserIsNotLogin()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMessageUserIsNotLogin(){
        Log.d("HomeFragment","on message user is not login")
        val intent = Intent(activity, UserSettingActivity::class.java)
        startActivity(intent)
    }
}