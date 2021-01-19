package com.example.aona2.studywithme

import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class CurrentStudyInfoRepository {
    val currentStudyInfos = MutableLiveData<MutableList<CurrentStudyInfo>>()

    init{
        getCurrentStudyInfos()
    }

    private fun getCurrentStudyInfos(){
        val currentStudyInfosRef = FirebaseDatabase.getInstance().getReference("/CurrentStudyInfos")
        currentStudyInfosRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
                if (currentStudyInfo != null) currentStudyInfos.value?.add(currentStudyInfo)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
                if (currentStudyInfo != null) currentStudyInfos.value?.add(currentStudyInfo)
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                val currentStudyInfo = snapshot.getValue(CurrentStudyInfo::class.java)
                if (currentStudyInfo != null) currentStudyInfos.value?.remove(currentStudyInfo)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}