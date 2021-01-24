package com.example.aona2.studywithme.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//not_change_to_mvvmブランチと比べてinRoomUsersを追加
//Roomを取得すればinRoomUsersも取得できる
@Parcelize
data class Room(val roomId: String, val roomStartAt: Long, val inRoomUsers: List<String>): Parcelable {
    constructor() : this("",0, emptyList())
}