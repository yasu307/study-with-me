package com.example.aona2.studywithme.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Room(val roomId: String, val roomStartAt: Long): Parcelable {
    constructor() : this("",0)
}