package com.example.aona2.studywithme

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val userName: String, val userImageView: String): Parcelable {
    constructor() : this("","","")
}