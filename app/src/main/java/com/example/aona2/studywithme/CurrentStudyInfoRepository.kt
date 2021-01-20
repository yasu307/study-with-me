package com.example.aona2.studywithme

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.User
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_task_name_input.*
import java.util.*

class CurrentStudyInfoRepository {
    val currentStudyInfos = MutableLiveData<List<CurrentStudyInfo>>()
    var cashCurrentStudyInfos = listOf<CurrentStudyInfo>()

    init{
        getCurrentStudyInfos()
    }

    private fun getCurrentStudyInfos(){
        val currentStudyInfosRef = FirebaseDatabase.getInstance().getReference("/CurrentStudyInfos")
        currentStudyInfosRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("CurrentStudyInfoRepository","on data change")
                val studyInfos = mutableListOf<CurrentStudyInfo>()
                snapshot.children.forEach {
                    val studyInfo = it.getValue(CurrentStudyInfo::class.java) ?: return@forEach
                    studyInfos.add(studyInfo)
                }
                currentStudyInfos.value = studyInfos
                cashCurrentStudyInfos = studyInfos
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("CurrentStudyInfoRepository","on data cancelled")
            }
        })
    }

    suspend fun saveCurrentStudyInfo(room: Room, taskName: String): Boolean{
        val uid = Firebase.auth.uid ?: return false

        val currentStudyInfoRef = Firebase.database.getReference("CurrentStudyInfos/$uid")

        val startStudyAt = Calendar.getInstance().timeInMillis

        val currentStudyInfo = CurrentStudyInfo(uid, room.roomId, taskName, room.roomStartAt, startStudyAt)

        try{
            currentStudyInfoRef.setValue(currentStudyInfo).addOnCompleteListener {
                Log.d("CurrentStudyInfoRepository", "save study info is success")
            }
        }catch (e: FirebaseAuthException){
            return false
        }
        return true
    }
}