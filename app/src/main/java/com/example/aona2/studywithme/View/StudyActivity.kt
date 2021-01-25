package com.example.aona2.studywithme.View

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.TimeManage.CalcRemainTime
import com.example.aona2.studywithme.TimeManage.MyCountDownTimer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_study.*
import java.text.SimpleDateFormat


class StudyActivity : AppCompatActivity() {
    private var startRoomAtMillis: Long? = null
    private var myCountDownTimer: MyCountDownTimer? = null

    private lateinit var adapter: InRoomFriendListAdapter

    private var room: Room? = null

    private var inRoomUsersList: MutableList<User> = mutableListOf<User>()

    //ログで時間を表示するときのフォーマット
    private val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss.SSS秒")

    //ステータスを無理やり切り替えるためにフィールドにした
    private lateinit var remainTime: Pair<Long, Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        room = intent.getParcelableExtra<Room>("ROOM_KEY")
        Log.d("StudyActivity", "room id is ${room?.roomId}")

        startRoomAtMillis = room?.roomStartAt
        Log.d("StudyActivity", "start room at  ${simpleDateFormat.format(startRoomAtMillis)}")

        //recyclerviewの設定
        adapter = InRoomFriendListAdapter(this)
        inRoomFriend_recyclerView_studyActivity.adapter = adapter
        inRoomFriend_recyclerView_studyActivity.layoutManager = LinearLayoutManager(this)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        inRoomFriend_recyclerView_studyActivity.addItemDecoration(itemDecoration)

        //ルームにいる人のリストを作成 adapterに適用する
        makeInRoomUsersList()

        startTimer()

        //ステータスを無理やり切り替えるためのボタン
        nextMode_btn_studyActivity.setOnClickListener {
            //最初のタイマー起動の場合はキャンセルしない
            if(myCountDownTimer != null)  myCountDownTimer?.cancel()

            //スタータスのみを反転させる
            remainTime = Pair(remainTime.first, !remainTime.second)

            //タイマーをセット、開始
            myCountDownTimer = MyCountDownTimer(remainTime.first, 1000, remainTime.second, remain_time_textView, remain_time_progressBar, this)
            myCountDownTimer?.start()
            changeViewFromStatus()
        }
    }

    private fun changeSimpleRoomFriendView(){
        simpleRoomFriend_linear_studyActivity.removeAllViews()
        inRoomUsersList.forEach { user->
            val circleImageView = de.hdodenhof.circleimageview.CircleImageView(this)
            LinearLayout.LayoutParams(
                    convertDpToPx(60), convertDpToPx(60)).let {
                it.setMargins(convertDpToPx(5))
                circleImageView.layoutParams = it
            }
            circleImageView.borderWidth = convertDpToPx(2)
            Picasso.get().load(user.userImageView).into(circleImageView)
            simpleRoomFriend_linear_studyActivity.addView(circleImageView)
        }
    }

    private fun changeViewFromStatus(){
        if(remainTime.second){ //勉強中
            //ユーザーの詳細表示
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).let {
                inRoomFriend_recyclerView_studyActivity.layoutParams = it
            }
            //ユーザーの簡易表示
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0).let {
                simpleRoomFriend_linear_studyActivity.layoutParams = it
            }
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0).let {
                chat_recyclerView_studyActivity.layoutParams = it
            }
        }else { //休憩中
            //ユーザーの詳細表示
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0).let {
                inRoomFriend_recyclerView_studyActivity.layoutParams = it
            }
            //ユーザーの簡易表示
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).let {
                simpleRoomFriend_linear_studyActivity.layoutParams = it
            }
            LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).let {
                chat_recyclerView_studyActivity.layoutParams = it
            }
        }
    }

    //Firebaseから現在の勉強情報を取得する
    //自動で更新するように変更
    private fun makeInRoomUsersList(){
        Log.d("StudyActivity","make in room users")
        val inRoomUsersRef = Firebase.database.getReference("rooms/${room?.roomId}/in_room_users")
        inRoomUsersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.getValue()
                inRoomUsersList.add(HomeActivity.users[uid] as User)
                adapter.setInRoomUsers(inRoomUsersList)
                changeSimpleRoomFriendView()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.getValue()
                inRoomUsersList.add(HomeActivity.users[uid] as User)
                adapter.setInRoomUsers(inRoomUsersList)
                changeSimpleRoomFriendView()
            }
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val uid = snapshot.getValue()
                inRoomUsersList.remove(HomeActivity.users[uid] as User)
                adapter.setInRoomUsers(inRoomUsersList)
                changeSimpleRoomFriendView()
            }
        })
    }

    //タイマーの起動
    fun startTimer() {
        //最初のタイマー起動の場合はキャンセルしない
        if(myCountDownTimer != null)  myCountDownTimer?.cancel()

        //残り時間の計算
        remainTime = CalcRemainTime(startRoomAtMillis?:return).calcRemainTime()

        //タイマーをセット、開始
        myCountDownTimer = MyCountDownTimer(remainTime.first, 1000, remainTime.second, remain_time_textView, remain_time_progressBar, this)
        myCountDownTimer?.start()
        changeViewFromStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("StudyActivity", "onDestroy")
        //一応タイマーをキャンセルしておく
        if(myCountDownTimer != null)  myCountDownTimer?.cancel()

        //Firebase上の退出処理
        val currentUser = Firebase.auth.currentUser

        //もしRoomに自分しかいなかったらRoomを削除
        if(inRoomUsersList.size == 1){
            val roomRef = Firebase.database.getReference("rooms/${room?.roomId}")
            roomRef.removeValue().addOnSuccessListener {
                Log.d("StudyActivity","delete room is success")
            }.addOnFailureListener {
                Log.d("StudyActivity","delete room is failure")
            }
        }else{ //そうじゃなかったらRoomから自分のuidのみを削除
            val inRoomUsersRef = Firebase.database.getReference("rooms/${room?.roomId}/in_room_users/${currentUser?.uid}")
            inRoomUsersRef.removeValue().addOnSuccessListener {
                Log.d("StudyActivity","delete my uid is success")
            }.addOnFailureListener {
                Log.d("StudyActivity","delete my uid is failure")
            }
        }

        //自分の勉強情報を削除
        val currentStudyInfoRef = Firebase.database.getReference("CurrentStudyInfos/${currentUser?.uid}")
        currentStudyInfoRef.removeValue().addOnSuccessListener{
            Log.d("StudyActivity","delete my current study info is success")
        }.addOnFailureListener {
            Log.d("StudyActivity","delete my current study info is failure")
        }
    }

    //dpをpxに置換
    private fun convertDpToPx(dp: Int): Int {
        val d: Float = this.resources.displayMetrics.density
        return (dp * d + 0.5).toInt()
    }
}