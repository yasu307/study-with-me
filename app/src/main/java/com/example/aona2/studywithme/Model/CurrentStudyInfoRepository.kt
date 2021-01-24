package com.example.aona2.studywithme.Model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class CurrentStudyInfoRepository {
    val currentStudyInfos = MutableLiveData<List<CurrentStudyInfo>>()
    //リストとして使用するためのキャッシュ
    var cashCurrentStudyInfos = listOf<CurrentStudyInfo>()

    init{
        getCurrentStudyInfos()
    }

    //CurrentStudyInfoを取得　自動的に更新
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
                //LiveDataを更新
                currentStudyInfos.value = studyInfos
                //キャッシュを更新
                cashCurrentStudyInfos = studyInfos
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("CurrentStudyInfoRepository","on data cancelled")
            }
        })
    }

    //CurrentStudyInfoを新しく保存する 勉強を開始するときに使用する 成功したか？を返す
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