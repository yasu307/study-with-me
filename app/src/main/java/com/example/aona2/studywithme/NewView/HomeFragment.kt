package com.example.aona2.studywithme.NewView

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
import com.example.aona2.studywithme.NewViewModel.MainViewModel
import com.example.aona2.studywithme.NewViewModel.MainViewModel.MainMessage
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.OldView.StudyingFriendListAdapter
import com.example.aona2.studywithme.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeFragment : Fragment(), StudyingFriendListAdapter.Listener {
    private val viewModel: MainViewModel by activityViewModels()

    //RecyclerViewのadapter
    private lateinit var adapter: StudyingFriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewModelから送られてくるmessageを監視する
        viewModel.message.onEach { onMessage(it) }.launchIn(lifecycleScope)
        //OptionsMenuを表示するため
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

        //ログインをチェックする
        viewModel.checkLogin()

        //recyclerViewの設定
        adapter = StudyingFriendListAdapter(activity as Context, this)
        recyclerView_homeFragment.adapter = adapter
        recyclerView_homeFragment.layoutManager = LinearLayoutManager(activity as Context)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(activity as Context, DividerItemDecoration.VERTICAL)
        recyclerView_homeFragment.addItemDecoration(itemDecoration)

        //allUsersを監視する
        viewModel.allUsers.observe(viewLifecycleOwner, Observer { users ->
            adapter.setAllUsers(users)
            users?.forEach{
                Log.d("HomeFragment","all user is ${it.value.userName}")
            }
        })
        //currentStudyInfosを監視する
        viewModel.currentStudyInfos.observe(viewLifecycleOwner, Observer { currentStudyInfos ->
            //adapterのcurrentStudyInfosを更新する
            adapter.setCurrentStudyInfos(currentStudyInfos)
            currentStudyInfos?.forEach{
                Log.d("HomeFragment","current study info is ${it.uid}")
            }
        })
        //allRoomsを監視する
        viewModel.allRooms.observe(viewLifecycleOwner, Observer { allRooms ->
            allRooms?.forEach {
                Log.d("HomeFragment", "all rooms is ${it.roomId}")
                //Room内のinRoomUsers
                it.inRoomUsers.forEach {
                    Log.d("HomeFragment", "in room users is $it")
                }
            }
        })

        //fabをクリックしたときの処理　一人で勉強を開始する
        startStudyAlone_fab_homeFragment.setOnClickListener {
            //一人で開始するときはnull
            viewModel.clickedFriendUid = null
            //Fragmentの遷移
            findNavController().navigate(R.id.action_homeFragment_to_taskNameInputFragment)
        }
    }

    //RecyclerViewのアイテムをクリックしたときの処理
    override fun onItemClicked(index: Int, currentStudyInfo: CurrentStudyInfo) {
        //押した友達のuidを保持
        viewModel.clickedFriendUid = currentStudyInfo.uid
        //Fragmentの遷移
        findNavController().navigate(R.id.action_homeFragment_to_taskNameInputFragment)
    }

    //ViewModelから送られてくるメッセージに対応する処理を呼び出す
    private fun onMessage(message: MainViewModel.MainMessage) {
        when (message) {
            is MainMessage.UserIsNotLogin -> onMessageUserIsNotLogin()
        }
    }

    //メッセージに対応する処理
    //ログインしていなかったとき　UserSettingActivityに遷移
    @Suppress("UNUSED_PARAMETER")
    private fun onMessageUserIsNotLogin(){
        Log.d("HomeFragment","on message user is not login")
        val intent = Intent(activity, UserSettingActivity::class.java)
        startActivity(intent)
    }

    //ログアウトメニューが選択されたときの処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> {
                viewModel.signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //ログアウトメニューを作成
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}