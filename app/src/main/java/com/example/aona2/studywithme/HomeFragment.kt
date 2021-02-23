package com.example.aona2.studywithme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aona2.studywithme.MainViewModel.MainMessage
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.View.RegisterActivity
import com.example.aona2.studywithme.View.StudyingFriendListAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeFragment : Fragment(), StudyingFriendListAdapter.Listener {
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var adapter: StudyingFriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.message.onEach { onMessage(it) }.launchIn(lifecycleScope)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.checkLogin()

        //recyclerViewの設定
        adapter = StudyingFriendListAdapter(activity as Context, this)
        recyclerView_homeFragment.adapter = adapter
        recyclerView_homeFragment.layoutManager = LinearLayoutManager(activity as Context)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(activity as Context, DividerItemDecoration.VERTICAL)
        recyclerView_homeFragment.addItemDecoration(itemDecoration)

        viewModel.allUsers.observe(viewLifecycleOwner, Observer { users ->
            adapter.setAllUsers(users)
            users?.forEach{
                Log.d("HomeFragment","all user is ${it.value.userName}")
            }
        })
        viewModel.currentStudyInfos.observe(viewLifecycleOwner, Observer { currentStudyInfos ->
            adapter.setCurrentStudyInfos(currentStudyInfos)
            currentStudyInfos?.forEach{
                Log.d("HomeFragment","current study info is ${it.uid}")
            }
        })
        viewModel.allRooms.observe(viewLifecycleOwner, Observer { allRooms ->
            allRooms?.forEach {
                Log.d("HomeFragment", "all rooms is ${it.roomId}")
                it.inRoomsUsers.forEach {
                    Log.d("HomeFragment", "in room users is $it")
                }
            }
        })

        startStudyAlone_fab_homeFragment.setOnClickListener {
            viewModel.clickedFriendUid = null
            findNavController().navigate(R.id.action_homeFragment_to_taskNameInputFragment)
        }
    }

    override fun onItemClicked(index: Int, currentStudyInfo: CurrentStudyInfo) {
        viewModel.clickedFriendUid = currentStudyInfo.uid
        findNavController().navigate(R.id.action_homeFragment_to_taskNameInputFragment)
    }

    private fun onMessage(message: MainViewModel.MainMessage) {
        when (message) {
            is MainMessage.UserIsNotLogin -> onMessageUserIsNotLogin()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onMessageUserIsNotLogin(){
        Log.d("HomeFragment","on message user is not login")
        val intent = Intent(activity, UserSettingActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> {
                viewModel.signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}