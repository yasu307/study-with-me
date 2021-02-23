package com.example.aona2.studywithme.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Room(val roomId: String, val roomStartAt: Long, val inRoomsUsers: List<String>): Parcelable {
    constructor() : this("",0, emptyList())
}