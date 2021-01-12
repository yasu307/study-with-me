package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_task_name_input.*
import java.util.*

class TaskNameInputActivity : AppCompatActivity() {
    private var friendStudyTogether: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_name_input)

        val uidStudyTogether = intent.getStringExtra("USER_UID")
        Log.d("TaskNameInputActivity", "user id is $uidStudyTogether")

        start_task_button.setOnClickListener {
            if(friendStudyTogether == null){
                Log.d("TaskNameInputActivity", "start study alone")
                makeRoom()
            }else{
                Log.d("TaskNameInputActivity", "start study with friend")
//                saveUserToRoom(ref)
            }

            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun makeRoom(){
        val ref = Firebase.database.getReference("rooms").push()
        if(ref.key == null) return
        val nowMillis = Calendar.getInstance().timeInMillis
        val room = Room(ref.key!!, nowMillis)
        ref.setValue(room)
                .addOnSuccessListener {
                    Log.d("HomeActivity", "save room to Firebase is success")

//                    //ダミーのroomId
//                    val ref = Firebase.database.getReference("rooms/-MQpGYwCbZVzKgawu6OD")

                    saveUserToRoom(ref)
                }
                .addOnFailureListener {
                    Log.d("HomeActivity", "save room to Firebase is failure")
                }
    }

    private fun saveUserToRoom(ref: DatabaseReference){
        val uid = Firebase.auth.currentUser?.uid
        ref.child("in_room_users").child(uid.toString()).setValue(uid)
                .addOnSuccessListener {
                    Log.d("HomeActivity", "save user to room in Firebase is success")
                }
                .addOnFailureListener {
                    Log.d("HomeActivity", "save user to room in Firebase is failure")
                }
    }

    private fun startStudyWithFriend(){

    }

    private fun saveStudyInfoToFirebase(){
        val uid = Firebase.auth.currentUser?.uid
        if(uid == null) return

        val ref = Firebase.database.getReference("StudyInfos/$uid").push()
        if(ref.key == null) return

        val nowMillis = Calendar.getInstance().timeInMillis
        val taskName = task_name_edit_text.text.toString()

        val studyInfo = StudyInfo(ref.key!!, uid, taskName, nowMillis, -1)

        ref.setValue(studyInfo)
                .addOnSuccessListener {
                    Log.d("TaskNameInputActivity", "save studyInfo to Firebase is success")
                }
                .addOnFailureListener {
                    Log.d("TaskNameInputActivity", "save studyInfo to Firebase is failure")
                }

    }
}