package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), StudyingFriendListAdapter.Listener {

    companion object{
        var users: MutableMap<String, User> = mutableMapOf<String, User>()
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: StudyingFriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //ログイン周り
        auth = Firebase.auth
        checkLogin()

        //recyclerViewの設定
        adapter = StudyingFriendListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        //Firebaseの情報をrecyclerViewに渡す
        fetchCurrentStudyInfos()
        fetchUsers()

        //fabがクリックされたら何も付加情報なしでTaskNameInputActivityに遷移
        start_study_alone_fab_homeActivity.setOnClickListener {
            val intent = Intent(this, TaskNameInputActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    //recyclerViewのアイテムがクリックされた場合呼び出される
    //クリックされたユーザーの勉強情報を引数にとる
    override fun onItemClicked(index: Int, currentStudyInfo: CurrentStudyInfo) {
        val intent = Intent(this, TaskNameInputActivity::class.java)
        //intent先にユーザー情報を送る
        intent.putExtra("STUDY_INFO", currentStudyInfo)
        startActivity(intent)
    }

    //ログインしているか確認
    //していなかったらMainActivityに遷移
    private fun checkLogin(){
        val currentUser = auth.currentUser
        if(currentUser == null){
            Log.d(MainActivity.TAG,"user is not login")
            //HomeActivityへ遷移する
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }
    }

    //Firebaseから現在の勉強情報を取得する
    //自動で更新するように変更
    private fun fetchCurrentStudyInfos(){
        val currentStudyInfos = mutableListOf<CurrentStudyInfo>()
        val currentStudyInfosRef = FirebaseDatabase.getInstance().getReference("/CurrentStudyInfos")
        currentStudyInfosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val currentStudyInfo = it.getValue(CurrentStudyInfo::class.java)
                    if(currentStudyInfo != null) currentStudyInfos.add(currentStudyInfo)
                    currentStudyInfos.forEach {
                        Log.d("mutable list of current study infos", it.uid.toString())
                    }
                }
                adapter.setCurrentStudyInfos(currentStudyInfos)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //Firebaseから全ユーザーの情報を取得する
    //自動で更新するように変更する必要ある？
    private fun fetchUsers(){
        val usersRef = FirebaseDatabase.getInstance().getReference("/users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    Log.d("HomeActivity",user?.userName?:"")
                    if(user != null) users[it.key!!] = user
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}