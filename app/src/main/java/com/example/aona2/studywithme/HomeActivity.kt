package com.example.aona2.studywithme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), StudyingFriendListAdapter.Listener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //recyclerViewの設定
        val adapter = StudyingFriendListAdapter(this, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //fabの設定
        start_study_alone_fab_homeActivity.setOnClickListener {
            val intent = Intent(this, TaskNameInputActivity::class.java)
            startActivity(intent)
        }
    }

    //recyclerViewのアイテムがクリックされた場合呼び出される
    override fun onItemClicked(index: Int) {
        val intent = Intent(this, TaskNameInputActivity::class.java)
        startActivity(intent)
    }
}