package com.example.aona2.studywithme

import androidx.lifecycle.MutableLiveData
import com.example.aona2.studywithme.Model.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class RoomRepository {
    val allRooms = MutableLiveData<List<Room>>()

    init {
        getAllRooms()
        checkSetValue()
    }

    private fun getAllRooms(){
        val ref = Firebase.database.getReference("/rooms")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = mutableListOf<Room>()
                snapshot.children.forEach {
                    val room = it.getValue(Room::class.java) ?: return@forEach
                    rooms.add(room)
                }
                allRooms.value = rooms
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkSetValue(){
        val ref = Firebase.database.getReference("/rooms").push()
        val nowMillis = Calendar.getInstance().timeInMillis
        val inRoomUsers = listOf("test")
        val room = Room(ref.key?:"", nowMillis, inRoomUsers)
        ref.setValue(room)
    }

}