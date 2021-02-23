package com.example.aona2.studywithme

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.User
import com.google.firebase.database.*

class CurrentStudyInfoRepository {
    val currentStudyInfos = MutableLiveData<List<CurrentStudyInfo>>()

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
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("CurrentStudyInfoRepository","on data cancelled")
            }
        })
    }
}