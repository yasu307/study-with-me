package com.example.aona2.studywithme.Model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

class RoomRepository {
    val allRooms = MutableLiveData<List<Room>>()
    //リストとして使用するためのキャッシュ
    var cashAllRooms = listOf<Room>()

    init {
        getAllRooms()
        checkSetValue()
    }

    //Firebaseからすべてのルームを取得する
    private fun getAllRooms(){
        val ref = Firebase.database.getReference("/rooms")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val rooms = mutableListOf<Room>()
                snapshot.children.forEach {
                    val room = it.getValue(Room::class.java) ?: return@forEach
                    rooms.add(room)
                }
                //キャッシュを更新
                cashAllRooms = rooms
                //LiveDataを更新
                allRooms.value = rooms
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    //Firebaseに新しくルームを保存できるかチェックする　後に消す
    private fun checkSetValue(){
        val ref = Firebase.database.getReference("/rooms").push()
        val nowMillis = Calendar.getInstance().timeInMillis
        val inRoomUsers = listOf("test")
        val room = Room(ref.key?:"", nowMillis, inRoomUsers)
        ref.setValue(room)
    }

    //すでにあるルームに自分のuidを追加する　友達の勉強に参加するときに使用する
    suspend fun addMyInfoToRoom(friendUid: String){

    }

    //新しくルームを作成　自分のuidをroomに追加する
    //一人で勉強を開始するときに使用する
    suspend fun makeRoom() : Room?{
        Log.d("RoomRepository","make room")
        val roomRef = Firebase.database.getReference("rooms").push()
        val nowMillis = Calendar.getInstance().timeInMillis
        val uid = Firebase.auth.currentUser?.uid ?: return null
        val inRoomUsers = listOf(uid)
        val room = Room(roomRef.key!!, nowMillis, inRoomUsers)
        Log.d("RoomRepository","until try")
        try{
            roomRef.setValue(room).addOnCompleteListener{
                Log.d("RoomRepository","make room is succeeded")
            }.await()
        }catch(e: FirebaseAuthException){
            Log.d("RoomRepository","catch exception")
            return null
        }
        return room
    }
}