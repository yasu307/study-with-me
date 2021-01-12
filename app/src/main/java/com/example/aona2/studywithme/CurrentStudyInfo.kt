package com.example.aona2.studywithme

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class CurrentStudyInfo(val uid: String,
                       val roomId: String,
                       val taskName: String,
                       val roomStartAt: Long,
                       val studyStartAt: Long): Parcelable {
    constructor() : this("","","",0,0)
}