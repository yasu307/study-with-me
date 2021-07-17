package com.example.aona2.studywithme.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.StudyInfo
import com.example.aona2.studywithme.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_task_name_input.*
import java.time.LocalDateTime
import java.util.*


class TaskNameInputActivity : AppCompatActivity() {
    private val myUid = Firebase.auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_name_input)

        //HomeActivityからクリックしてきた人のuidとルーム情報を取得
        //一人で勉強を開始したらどちらもnull
        val friendUid = intent.getStringExtra("FRIEND_UID")
        val friendRoom = intent.getParcelableExtra<Room>("FRIEND_ROOM")

        //参加する先の友達を表示する
        if(friendUid != null){
            val friendName = HomeActivity.users[friendUid]?.userName
            clicked_friend_name_textView.text = "$friendName の勉強に参加"
        }

        //スタートボタンが押された場合
        start_task_button.setOnClickListener {
            //スタートボタンを連打したときに二重で動作してしまうので
            //押した後は1秒間入力を無効化
            //本来は処理をしている間だけ無効にしなくてはいけない
            it.isEnabled = false
            Handler().postDelayed(Runnable { it.isEnabled = true }, 1000)

            if(friendRoom == null){ // 一人で勉強を始める場合
                Log.d("TaskNameInputActivity", "start study alone")
                makeRoom()
            }else{ // 友達に参加する場合
                Log.d("TaskNameInputActivity", "start study with friend")
                saveUserToFriendRoom(friendRoom)
            }
        }
    }

    //ルームを作成　
    //一人で勉強を開始するときに使用する
    private fun makeRoom(){
        val roomRef = Firebase.database.getReference("rooms").push()
        if(roomRef.key == null) return

        //自分の勉強情報を作成する
        val now = LocalDateTime.now()
        val nowMillis = Calendar.getInstance().timeInMillis
        val taskName = task_name_edit_text.text.toString()
        val studyInfo = StudyInfo(myUid!!, taskName, now.toString(), roomRef.key!!, nowMillis, "")

        //ルーム情報を保存
        val room = Room(roomRef.key!!, nowMillis, mapOf(myUid to studyInfo))
        roomRef.setValue(room)
                .addOnSuccessListener {
                    Log.d("HomeActivity", "save room to Firebase is success")
                    moveToRoom(room)
                }
                .addOnFailureListener {
                    Log.d("HomeActivity", "save room to Firebase is failure")
                }
    }

    //ルームに自分の勉強情報を追加
    //友達の勉強に参加するときに使用する
    private fun saveUserToFriendRoom(friendRoom: Room){
        val ref = Firebase.database.getReference("rooms/${friendRoom.roomId}/studyInfos/$myUid")

        val now = LocalDateTime.now()
        val nowMillis = Calendar.getInstance().timeInMillis
        val taskName = task_name_edit_text.text.toString()

        val studyInfo = StudyInfo(myUid!!, taskName, now.toString(), friendRoom.roomId, friendRoom.roomStartAt, "")
        ref.setValue(studyInfo).addOnSuccessListener {
            Log.d("TaskNameInputActivity", "save user to friend room is succeeded")
            moveToRoom(friendRoom)
        }
    }

    //StudyActivityに遷移（ルームに移動）
    //intentに参加する部屋の情報を付加する
    private fun moveToRoom(room: Room){
        val intent = Intent(this, StudyActivity::class.java)
        intent.putExtra("ROOM_KEY", room)
        startActivity(intent)
    }
}