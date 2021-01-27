package com.example.aona2.studywithme.View

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aona2.studywithme.Model.ChatMessage
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.StudyInfo
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

    private lateinit var inRoomFriendListAdapter: InRoomFriendListAdapter
    private lateinit var chatLogAdapter: ChatLogAdapter

    private var room: Room? = null

    private var chatLog = mutableListOf<ChatMessage>()

    //ルームにいる人の勉強情報
    private var studyInfoList = mutableListOf<StudyInfo>()

    //ログで時間を表示するときのフォーマット
    private val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss.SSS秒")

    //ステータスを無理やり切り替えるためにフィールドにした
    private lateinit var remainTime: Pair<Long, Boolean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        room = intent.getParcelableExtra<Room>("ROOM_KEY")
        Log.d("StudyActivity", "room id is ${room?.roomId}")

        startRoomAtMillis = room?.roomStartAt ?: -1
        Log.d("StudyActivity", "start room at  ${simpleDateFormat.format(startRoomAtMillis)}")

        //ルームにいる人のrecyclerviewの設定
        inRoomFriendListAdapter = InRoomFriendListAdapter(this)
        inRoomFriend_recyclerView_studyActivity.adapter = inRoomFriendListAdapter
        inRoomFriend_recyclerView_studyActivity.layoutManager = LinearLayoutManager(this)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        inRoomFriend_recyclerView_studyActivity.addItemDecoration(itemDecoration)

        //chatLogのrecyclerviewの設定
        chatLogAdapter = ChatLogAdapter(this)
        chat_recyclerView_studyActivity.adapter = chatLogAdapter
        chat_recyclerView_studyActivity.layoutManager = LinearLayoutManager(this)

        startTimer()

        //ルームにいる人の勉強情報のリストを作成する adapterに適用する
        makeStudyInfoList()

        //ルーム内のチャットログを取得
        makeChatLog()

        //送信ボタンが押されたとき
        send_btn_studyActivity.setOnClickListener {
            sendMessage()
        }

        //ステータスを無理やり切り替えるためのボタン
        nextMode_btn_studyActivity.setOnClickListener {
            //最初のタイマー起動の場合はキャンセルしない
            if (myCountDownTimer != null) myCountDownTimer?.cancel()

            //スタータスのみを反転させる
            remainTime = Pair(remainTime.first, !remainTime.second)

            //タイマーをセット、開始
            myCountDownTimer = MyCountDownTimer(remainTime.first, 1000, remainTime.second, remain_time_textView, remain_time_progressBar, this)
            myCountDownTimer?.start()
            changeViewFromStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }

    //ルーム内のチャットログを取得
    private fun makeChatLog() {
        val roomId = room?.roomId ?: return
        val ref = Firebase.database.getReference("/messages/$roomId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                chatLog.add(chatMessage)
                chatLogAdapter.setChatLog(chatLog)
                chat_recyclerView_studyActivity.scrollToPosition(chatLogAdapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                chatLog.add(chatMessage)
                chatLogAdapter.setChatLog(chatLog)
                chat_recyclerView_studyActivity.scrollToPosition(chatLogAdapter.itemCount - 1)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                chatLog.remove(chatMessage)
                chatLogAdapter.setChatLog(chatLog)
                chat_recyclerView_studyActivity.scrollToPosition(chatLogAdapter.itemCount - 1)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    //入力されているメッセージを送信する
    private fun sendMessage() {
        val uid = Firebase.auth.currentUser?.uid ?: return
        val roomId = room?.roomId ?: return
        val message = messageInput_editText_studyActivity.text.toString()
        if (message.isEmpty()) return
        //メッセージを取得したらeditTextをクリア
        messageInput_editText_studyActivity.text.clear()

        val ref = Firebase.database.getReference("/messages/$roomId").push()
        if (ref.key == null) return
        val chatMessage = ChatMessage(ref.key!!, message, roomId, uid, System.currentTimeMillis())

        ref.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("StudyActivity", "send message is succeeded")
                }
    }


    //休憩中に表示されるユーザーアイコンの一覧を更新する
    private fun refreshSimpleRoomFriendView() {
        simpleRoomFriend_linear_studyActivity.removeAllViews()
        studyInfoList.forEach { studyInfo ->
            val user = HomeActivity.users[studyInfo.uid] ?: return@forEach
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

    //ステータスによって表示するViewを変える
    private fun changeViewFromStatus() {
        if (remainTime.second) { //勉強中
            //勉強中のViewを見えるように
            inRoomFriend_recyclerView_studyActivity.visibility = View.VISIBLE
            studyStatus_imageView_studyActivity.visibility = View.VISIBLE
            question_fab_studyActivity.visibility = View.VISIBLE
            //休憩中のViewを見えないように
            simpleRoomFriend_linear_studyActivity.visibility = View.GONE
            chat_recyclerView_studyActivity.visibility = View.GONE
            messageInput_constraint_studyActivity.visibility = View.GONE
            breaktimeStatus_imageView_studyActivity.visibility = View.GONE
        } else { //休憩中
            //勉強中のViewを見えないように
            inRoomFriend_recyclerView_studyActivity.visibility = View.GONE
            studyStatus_imageView_studyActivity.visibility = View.GONE
            question_fab_studyActivity.visibility = View.GONE
            //休憩中のViewを見えるように
            simpleRoomFriend_linear_studyActivity.visibility = View.VISIBLE
            messageInput_constraint_studyActivity.visibility = View.VISIBLE
            chat_recyclerView_studyActivity.visibility = View.VISIBLE
            breaktimeStatus_imageView_studyActivity.visibility = View.VISIBLE
        }
    }

    //ルームにいるユーザーの勉強情報を取得
    //勉強中のユーザー一覧表示と休憩中のユーザーアイコンの一覧表示を更新
    private fun makeStudyInfoList() {
        val studyInfosRef = Firebase.database.getReference("rooms/${room?.roomId}/studyInfos")
        studyInfosRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val studyInfo = snapshot.getValue(StudyInfo::class.java) ?: return
                studyInfoList.add(studyInfo)
                inRoomFriendListAdapter.setStudyInfos(studyInfoList)
                refreshSimpleRoomFriendView()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val studyInfo = snapshot.getValue(StudyInfo::class.java) ?: return
                studyInfoList.add(studyInfo)
                inRoomFriendListAdapter.setStudyInfos(studyInfoList)
                refreshSimpleRoomFriendView()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val studyInfo = snapshot.getValue(StudyInfo::class.java) ?: return
                studyInfoList.remove(studyInfo)
                inRoomFriendListAdapter.setStudyInfos(studyInfoList)
                refreshSimpleRoomFriendView()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //タイマーの起動
    fun startTimer() {
        //最初のタイマー起動の場合はキャンセルしない
        if (myCountDownTimer != null) myCountDownTimer?.cancel()

        //残り時間の計算
        remainTime = CalcRemainTime(startRoomAtMillis ?: return).calcRemainTime()

        //タイマーをセット、開始
        myCountDownTimer = MyCountDownTimer(remainTime.first, 1000, remainTime.second, remain_time_textView, remain_time_progressBar, this)
        myCountDownTimer?.start()
        changeViewFromStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("StudyActivity", "onDestroy")
        //一応タイマーをキャンセルしておく
        if (myCountDownTimer != null) myCountDownTimer?.cancel()

        //Firebase上の退出処理
        val currentUser = Firebase.auth.currentUser

        //もしRoomに自分しかいなかったらRoomを削除
        if (studyInfoList.size == 1) {
            val roomRef = Firebase.database.getReference("rooms/${room?.roomId}")
            roomRef.removeValue().addOnSuccessListener {
                Log.d("StudyActivity", "delete room is success")
            }.addOnFailureListener {
                Log.d("StudyActivity", "delete room is failure")
            }
        } else { //そうじゃなかったらRoomから自分のStudyInfoのみを削除
            val inRoomUsersRef = Firebase.database.getReference("rooms/${room?.roomId}/studyInfos/${currentUser?.uid}")
            inRoomUsersRef.removeValue().addOnSuccessListener {
                Log.d("StudyActivity", "delete my study info is success")
            }.addOnFailureListener {
                Log.d("StudyActivity", "delete my study info is failure")
            }
        }
    }

    //dpをpxに置換
    //refreshSimpleRoomFriendView()にて使用
    private fun convertDpToPx(dp: Int): Int {
        val d: Float = this.resources.displayMetrics.density
        return (dp * d + 0.5).toInt()
    }
}