package com.example.aona2.studywithme.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), StudyingFriendListAdapter.Listener {

    //ユーザー情報を保持しておく　他アクティビティから使用される
    companion object{
        //Map<uid, User>
        var users = mutableMapOf<String, User>()
    }
    //Map<roomId, Room>
    private var rooms = mutableMapOf<String, Room>()

    private lateinit var adapter: StudyingFriendListAdapter

    private var menuUserIcon: MenuItem? = null
    private var userIcon: CircleImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //ログインしているかチェック
        checkLogin()

        //recyclerViewの設定
        adapter = StudyingFriendListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        //Firebaseの情報を取得し、recyclerViewに渡す
        //fetchUsers()が成功したらfetchRooms(),setUserIconを呼び出す
        //こうしないとHomeActivityを表示したときにusersがnullでユーザー情報が表示されない可能性がある
        fetchUsers()

        //1分ごとにRecyclerViewを更新する
        val handler = Handler()
        var runnable = Runnable {  }
        runnable = Runnable {
            adapter.notifyDataSetChanged()
            handler.postDelayed(runnable, 60 * 1000)
            Log.d("HomeActivity", "refresh by handler")
        }
        handler.post(runnable)

        //fabがクリックされたら一人で勉強を開始
        //何も付加情報なしでTaskNameInputActivityに遷移
        start_study_alone_fab_homeActivity.setOnClickListener {
            val intent = Intent(this, TaskNameInputActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeActivity","on start")
        invalidateOptionsMenu()
    }

    //resumeのときrecyclerViewを更新
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_logout -> { //ログアウトメニュー
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.user_icon ->{
                Log.d("HomeActivity","user icon was tapped")
                invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d("HomeActivity","on create options menu")
        menuInflater.inflate(R.menu.nav_menu, menu)
        menuUserIcon = menu?.findItem(R.id.user_icon)
        menuUserIcon?.icon = userIcon?.drawable
        return super.onCreateOptionsMenu(menu)
    }

    //recyclerViewのアイテムがクリックされた場合呼び出される
    //クリックされたユーザーのuidとルーム情報を付加する
    override fun onItemClicked(index: Int, friendUid: String, friendRoom: Room) {
        val intent = Intent(this, TaskNameInputActivity::class.java)
        //intent先にユーザー情報を送る
        intent.putExtra("FRIEND_UID", friendUid)
        intent.putExtra("FRIEND_ROOM", friendRoom)
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

    //Firebaseから部屋情報を取得する
    //自動で更新するように変更
    private fun fetchRooms(){
        val roomsRef = FirebaseDatabase.getInstance().getReference("/rooms")
        roomsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("HomeActivity", "fetch rooms")
                val room = snapshot.getValue(Room::class.java)
                if (room != null) rooms[room.roomId] = room
                adapter.setRooms(rooms)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("HomeActivity", "fetch rooms")
                val room = snapshot.getValue(Room::class.java)
                if (room != null) rooms[room.roomId] = room
                adapter.setRooms(rooms)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("HomeActivity", "fetch rooms")
                val room = snapshot.getValue(Room::class.java)
                if (room != null) rooms.remove(room.roomId)
                adapter.setRooms(rooms)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

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
                setUserIcon()
                fetchRooms()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setUserIcon(){
        val uid = Firebase.auth.currentUser?.uid ?:return
        val userImageView = users[uid]?.userImageView ?:return
        userIcon = CircleImageView(this).apply {
            circleBackgroundColor = resources.getColor(android.R.color.white)
            Picasso.get().load(userImageView).into(this)
        }
        invalidateOptionsMenu()
//        val bitmap = actionbarIcon.drawable.toBitmap()
//        val actionBar = this.supportActionBar ?:return
//        actionBar.customView = actionbarIcon
//        actionBar.customView.right
//        actionBar.setDisplayShowCustomEnabled(true)
    }

}