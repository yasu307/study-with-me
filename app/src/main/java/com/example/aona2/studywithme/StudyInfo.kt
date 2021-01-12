package com.example.aona2.studywithme

class StudyInfo(val studyInfoId: String, val uid: String, val taskName: String, val studyStartAt: Long, val studyEndAt: Long) {
    constructor() : this("","","",0,0)
}