package com.example.aona2.studywithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.View.HomeActivity
import kotlinx.android.synthetic.main.activity_task_name_input.*
import kotlinx.android.synthetic.main.fragment_task_name_input.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class TaskNameInputFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.message.onEach { onMessage(it) }.launchIn(lifecycleScope)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_name_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //参加する先の友達を表示する
        if(viewModel.clickedFriendUid != null){
            val friendName = viewModel.getFriendName()
            friendName_textView_fragment.text = "$friendName の勉強に参加"
        }

        startTask_btn_fragment.setOnClickListener {
            Log.d("TaskNameInputFragment", "start alone")
            val taskName = taskName_editText_fragment.text.toString()
            if(viewModel.clickedFriendUid == null) viewModel.startAlone(taskName)
            else viewModel.startWithFriend(taskName)
        }
    }

    private fun onMessage(message: MainViewModel.MainMessage) {
        when (message) {
            is MainViewModel.MainMessage.UserJoinRoom -> onMessageUserJoinRoom()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMessageUserJoinRoom(){
        Log.d("TaskNameInputFragment","on message user join room")
        findNavController().navigate(R.id.action_taskNameInputFragment_to_studyFragment)
    }
}