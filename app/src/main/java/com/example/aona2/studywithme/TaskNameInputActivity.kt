package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        val friendCurrentStudyInfo = intent.getParcelableExtra<CurrentStudyInfo>("STUDY_INFO")
        Log.d("TaskNameInputActivity", "uid study together is ${friendCurrentStudyInfo?.uid}")

        start_task_button.setOnClickListener {
            if(friendCurrentStudyInfo == null){
                Log.d("TaskNameInputActivity", "start study alone")
                makeRoom()
            }else{
                Log.d("TaskNameInputActivity", "start study with friend")

                fetchFriendRoom(friendCurrentStudyInfo)
            }
        }
    }

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

    private fun makeRoom(){
        val roomRef = Firebase.database.getReference("rooms").push()
        if(roomRef.key == null) return
        val nowMillis = Calendar.getInstance().timeInMillis
        val room = Room(roomRef.key!!, nowMillis)
        roomRef.setValue(room)
                .addOnSuccessListener {
                    Log.d("HomeActivity", "save room to Firebase is success")

//                    //ダミーのroomId
//                    val roomRef = Firebase.database.getReference("rooms/-MQpGYwCbZVzKgawu6OD")

                    saveUserToRoom(room)
                }
                .addOnFailureListener {
                    Log.d("HomeActivity", "save room to Firebase is failure")
                }
    }

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
                    moveToRoom(room.roomId)
                }
                .addOnFailureListener {
                    Log.d("TaskNameInputActivity", "save studyInfo to Firebase is failure")
                }
    }


    private fun moveToRoom(roomId: String){
        val intent = Intent(this, StudyActivity::class.java)
        intent.putExtra("ROOM_ID", roomId)
        startActivity(intent)
    }
}