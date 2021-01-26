package com.example.aona2.studywithme.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.StudyInfo
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), StudyingFriendListAdapter.Listener {

    //ユーザー情報を保持しておく　他アクティビティから使用される
    companion object{
        var users = mutableMapOf<String, User>()
        var currentStudyInfos = mutableMapOf<String, CurrentStudyInfo>()
    }

    private lateinit var adapter: StudyingFriendListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        testFirebase()

//        //ログインしているかチェック
//        checkLogin()
//
//        //recyclerViewの設定
//        adapter = StudyingFriendListAdapter(this, this)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        //recyclerViewに枠線をつける
//        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        recyclerView.addItemDecoration(itemDecoration)
//
//        //Firebaseの情報を取得し、recyclerViewに渡す
////        fetchRooms()
//        fetchCurrentStudyInfos()
//        fetchUsers()
//
//        //fabがクリックされたら一人で勉強を開始
//        //何も付加情報なしでTaskNameInputActivityに遷移
//        start_study_alone_fab_homeActivity.setOnClickListener {
//            val intent = Intent(this, TaskNameInputActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun testFirebase(){
        val ref = Firebase.database.getReference("/test").push()
        val mutableStudyInfos = mutableMapOf<String, StudyInfo>()
        mutableStudyInfos["testUid"] = StudyInfo("testUid", "testTaskName", 1)
        mutableStudyInfos["testUid2"] = StudyInfo("testUid2", "testTaskName2", 2)
        val studyInfos: Map<String, StudyInfo> = mutableStudyInfos
        val room = Room(ref.key!!, 1, studyInfos)
        ref.setValue(room).addOnSuccessListener {
            Log.d("HomeActivity", "test firebase set value is succeeded")
            fetchTestFirebase()
        }
    }

    private val tests = mutableMapOf<String, Room>()
    private fun fetchTestFirebase(){
        val ref = Firebase.database.getReference("/test")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("HomeActivity", "fetch test firebase")
                val test = snapshot.getValue(Room::class.java)
                if (test != null) tests[test.roomId] = test
                showTest()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun showTest(){
        Log.d("HomeActivity", "show Test!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        tests.forEach {
            Log.d("HomeActivity", "room id is ${it.value.roomId}")
            Log.d("HomeActivity", "room start at is ${it.value.roomStartAt}")
            it.value.inRoomsUsers.forEach {
                Log.d("HomeActivity", "uid is ${it.value.uid}")
                Log.d("HomeActivity", "task name is ${it.value.taskName}")
                Log.d("HomeActivity", "study stat at is ${it.value.studyStartAt}")
            }
        }
    }



//    //resumeのときrecyclerViewを更新
//    override fun onResume() {
//        super.onResume()
//        adapter.notifyDataSetChanged()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> { //ログアウトメニュー
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
        val currentUser = Firebase.auth.currentUser
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
        val currentStudyInfosRef = FirebaseDatabase.getInstance().getReference("/CurrentStudyInfos")
        currentStudyInfosRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("HomeActivity", "fetch current study infos")
                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
                if (currentStudyInfo != null) currentStudyInfos[currentStudyInfo.uid] = currentStudyInfo
                adapter.setCurrentStudyInfos(currentStudyInfos)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("HomeActivity", "fetch current study infos")
                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
                if (currentStudyInfo != null) currentStudyInfos[currentStudyInfo.uid] = currentStudyInfo
                adapter.setCurrentStudyInfos(currentStudyInfos)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("HomeActivity", "fetch current study infos")
                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
                if (currentStudyInfo != null) currentStudyInfos.remove(currentStudyInfo.uid)
                adapter.setCurrentStudyInfos(currentStudyInfos)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

//    //Firebaseから部屋情報を取得する
//    //自動で更新するように変更
//    private fun fetchRooms(){
//        val roomsRef = FirebaseDatabase.getInstance().getReference("/rooms")
//        roomsRef.addChildEventListener(object : ChildEventListener {
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("HomeActivity", "fetch rooms")
//                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
//                if (currentStudyInfo != null) currentStudyInfos[currentStudyInfo.uid] = currentStudyInfo
//                adapter.setCurrentStudyInfos(currentStudyInfos)
//            }
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("HomeActivity", "fetch current study infos")
//                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
//                if (currentStudyInfo != null) currentStudyInfos[currentStudyInfo.uid] = currentStudyInfo
//                adapter.setCurrentStudyInfos(currentStudyInfos)
//            }
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//
//            }
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//                Log.d("HomeActivity", "fetch current study infos")
//                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
//                if (currentStudyInfo != null) currentStudyInfos.remove(currentStudyInfo.uid)
//                adapter.setCurrentStudyInfos(currentStudyInfos)
//            }
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
//    }

    //Firebaseから全ユーザーの情報を取得し、companion objectのusersにて保持
    //自動で更新するように変更する必要ある？
    private fun fetchUsers(){
        val usersRef = FirebaseDatabase.getInstance().getReference("/users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user != null) users[it.key!!] = user
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}