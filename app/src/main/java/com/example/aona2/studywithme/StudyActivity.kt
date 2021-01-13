package com.example.aona2.studywithme

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_study.*
import java.text.SimpleDateFormat
import java.util.*

class StudyActivity : AppCompatActivity() {
    private var startRoomAtMillis: Long? = null
    private var myCountDownTimer: MyCountDownTimer? = null

    private lateinit var adapter: InRoomFriendListAdapter

    //ログで時間を表示するときのフォーマット
    private val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss.SSS秒")

    //一周の時間
    private val oneRoopMin = 30
    //休憩の時間
    //つまり　勉強時間 = oneRoopMin - breaktimeMin
    private val breaktimeMin = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        Log.d("StudyActivity", "onCreate")
        val room = intent.getParcelableExtra<Room>("ROOM_KEY")
        Log.d("StudyActivity", "room id is ${room?.roomId}")

        //recyclerviewの設定
        adapter = InRoomFriendListAdapter(this)
        in_room_friend_recyclerview.adapter = adapter
        in_room_friend_recyclerview.layoutManager = LinearLayoutManager(this)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        in_room_friend_recyclerview.addItemDecoration(itemDecoration)

        makeInRoomUsers(room?:return)

        //ダミーのルーム開始時間をセット
        val startRoomAtCalendar = Calendar.getInstance()
        startRoomAtCalendar.set(2021,0,12,10,10,0)
        startRoomAtMillis = startRoomAtCalendar.timeInMillis
        Log.d("StudyActivity", "start room at  ${simpleDateFormat.format(startRoomAtMillis)}")
        //Log.d("StudyActivity", "start room at millis is ${startRoomAtMillis} ")

        startTimer()
    }

    //Firebaseから現在の勉強情報を取得する
    //自動で更新するように変更
    private fun makeInRoomUsers(room: Room){
        Log.d("StudyActivity","make in room users")
        var inRoomUsersList: MutableList<User> = mutableListOf<User>()
        val inRoomUsersRef = Firebase.database.getReference("rooms/${room.roomId}/in_room_users")
        inRoomUsersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val uid = snapshot.getValue()
                Log.d("StudyActivity", "in room user id is ${uid}")
                inRoomUsersList.add(HomeActivity.users[uid] as User)
                adapter.setInRoomUsers(inRoomUsersList)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }

    //現在実行中のモードの残り時間を計算　返り値　Pair<残り時間（ミリ秒）,勉強中か？>
    //もっと簡素にできないか？
    //MyCountDownTImerに移行すべき？
    private fun calcRemainTime(): Pair<Long, Boolean> {
        if (startRoomAtMillis == null) return Pair(0,true)

        //現在の時間
        val currentTimeMillis = Calendar.getInstance().timeInMillis
        Log.d("StudyActivity", "current time is ${simpleDateFormat.format(currentTimeMillis)} ")
        //Log.d("StudyActivity", "current time millis is ${currentTimeMillis} ")
        //経過時間
        val elapsedTimeMillis = currentTimeMillis - startRoomAtMillis!!
        Log.d("StudyActivity", "elapsed time millis is $elapsedTimeMillis")
        //残り時間
        val remainTimeMillis = ((oneRoopMin * 60 * 1000) - (elapsedTimeMillis % (oneRoopMin * 60 * 1000)))
        Log.d("StudyActivity", "remain time is $remainTimeMillis")

        //残り時間が5分以上なら勉強中、そうでないなら休憩中
        if (remainTimeMillis >= breaktimeMin * 60 * 1000) return Pair(remainTimeMillis - breaktimeMin * 60 * 1000, true)
        else return Pair(remainTimeMillis, false)
    }

    //タイマーの起動
    fun startTimer() {
        //最初のタイマー起動の場合はキャンセルしない
        if(myCountDownTimer != null)  myCountDownTimer?.cancel()

        //タイマーに設定する残り時間を計算
        val remainTime:Pair<Long, Boolean> = calcRemainTime()

        //タイマーをセット、開始
        myCountDownTimer = MyCountDownTimer(remainTime.first, 1000, remainTime.second, remain_time_textView, remain_time_progressBar, this)
        myCountDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("StudyActivity", "onDestroy")
        //一応タイマーをキャンセルしておく
        if(myCountDownTimer != null)  myCountDownTimer?.cancel()

        //Firebase上の退出処理をする
        //Roomから自分のuidを削除する

    }
}