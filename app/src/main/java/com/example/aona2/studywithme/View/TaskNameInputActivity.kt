package com.example.aona2.studywithme.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_task_name_input.*
import java.util.*

class TaskNameInputActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_name_input)

        //HomeActivityからクリックしてきた人の勉強情報を取得する
        val friendCurrentStudyInfo = intent.getParcelableExtra<CurrentStudyInfo>("STUDY_INFO")

        //参加する先の友達を表示する
        if(friendCurrentStudyInfo != null){
            val friendName = HomeActivity.users[friendCurrentStudyInfo.uid]?.userName
            clicked_friend_name_textView.text = "${friendName} の勉強に参加"
        }

        //スタートボタンが押された場合
        start_task_button.setOnClickListener {
            if(friendCurrentStudyInfo == null){ // 一人で勉強を始める場合
                Log.d("TaskNameInputActivity", "start study alone")
                makeRoom()
            }else{ // 友達に参加する場合
                Log.d("TaskNameInputActivity", "start study with friend")
                fetchFriendRoom(friendCurrentStudyInfo)
            }
        }
    }

    //ルームを作成　
    //一人で勉強を開始するときのみ使用する
    private fun makeRoom(){
        val roomRef = Firebase.database.getReference("rooms").push()
        if(roomRef.key == null) return
        val nowMillis = Calendar.getInstance().timeInMillis
        val room = Room(roomRef.key!!, nowMillis, emptyMap())
        roomRef.setValue(room)
                .addOnSuccessListener {
                    Log.d("HomeActivity", "save room to Firebase is success")

                    saveUserToRoom(room)
                }
                .addOnFailureListener {
                    Log.d("HomeActivity", "save room to Firebase is failure")
                }
    }

    //フレンドがいるルームの情報をDBから取得
    private fun fetchFriendRoom(friendCurrentStudyInfo: CurrentStudyInfo){
        val roomId = friendCurrentStudyInfo.roomId
        val roomRef = Firebase.database.getReference("/rooms/$roomId/")

        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("TaskNameInputActivity", "fetch friend room is success")
                val room = snapshot.getValue(Room::class.java)
                if(room == null) return

                saveUserToRoom(room)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TaskNameInputActivity", "fetch friend room is failure")
            }
        })
    }

    //ルーム情報に自分のuidを追加
    private fun saveUserToRoom(room: Room){
        val uid = Firebase.auth.currentUser?.uid
        val roomRef = Firebase.database.getReference("rooms/${room.roomId}")
        roomRef.child("in_room_users").child(uid.toString()).setValue(uid)
                .addOnSuccessListener {
                    Log.d("HomeActivity", "save user to room in Firebase is success")

                    saveCurrentStudyInfoToFirebase(room)
                }
                .addOnFailureListener {
                    Log.d("HomeActivity", "save user to room in Firebase is failure")
                }
    }

    //自分の勉強情報をDBに追加
    private fun saveCurrentStudyInfoToFirebase(room: Room){
        val uid = Firebase.auth.currentUser?.uid
        if(uid == null) return

        val currentStudyInfoRef = Firebase.database.getReference("CurrentStudyInfos/$uid")

        val startStudyAt = Calendar.getInstance().timeInMillis
        val taskName = task_name_edit_text.text.toString()

        val currentStudyInfo = CurrentStudyInfo(uid, room.roomId, taskName, room.roomStartAt, startStudyAt)

        currentStudyInfoRef.setValue(currentStudyInfo)
                .addOnSuccessListener {
                    Log.d("TaskNameInputActivity", "save studyInfo to Firebase is success")
                    moveToRoom(room)
                }
                .addOnFailureListener {
                    Log.d("TaskNameInputActivity", "save studyInfo to Firebase is failure")
                }
    }

    //StudyActivityに遷移（roomに遷移）
    //intentに参加する部屋の情報を付加する
    private fun moveToRoom(room: Room){
        val intent = Intent(this, StudyActivity::class.java)
        intent.putExtra("ROOM_KEY", room)
        startActivity(intent)
    }
}