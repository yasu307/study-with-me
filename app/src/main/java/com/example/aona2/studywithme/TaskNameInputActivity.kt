package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_task_name_input.*
import java.util.*

class TaskNameInputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_name_input)

        start_task_button.setOnClickListener {


            saveStudyInfoToFirebase()
            val intent = Intent(this, StudyActivity::class.java)
            startActivity(intent)
        }
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