package com.example.aona2.studywithme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item_in_room.view.*

//StudyActivityのルームにいる人リストのアダプター
class InRoomFriendListAdapter internal constructor(context: Context)
    : RecyclerView.Adapter<InRoomFriendListAdapter.InRoomFriendViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    //実際のデータが作成できたら使用する
//    private val friends = emptyList<Friend>()

    inner class InRoomFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user_icon = itemView.user_icon_in_room
        val user_name = itemView.user_name_in_room
        val task_name = itemView.task_name_in_room
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InRoomFriendViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item_in_room, parent, false)
        return InRoomFriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InRoomFriendViewHolder, position: Int) {
        //ダミーデータを手動入力
        if (position == 0) {
            holder.user_icon.setImageResource(R.drawable.member1)
            holder.user_name.text = "ヨシダ"
            holder.task_name.text = "実システム創造　進捗作成"
        }
        if (position == 1) {
            holder.user_icon.setImageResource(R.drawable.member2)
            holder.user_name.text = "フクダ"
            holder.task_name.text = ""
        }
        if (position == 2) {
            holder.user_icon.setImageResource(R.drawable.member3)
            holder.user_name.text = "ヤマダ"
            holder.task_name.text = "関連研究探し"
        }
        if (position == 3) {
            holder.user_icon.setImageResource(R.drawable.member1)
            holder.user_name.text = "ヨシダ"
            holder.task_name.text = "実システム創造　進捗作成"
        }
        if (position == 4) {
            holder.user_icon.setImageResource(R.drawable.member2)
            holder.user_name.text = "フクダ"
            holder.task_name.text = ""
        }
        if (position == 5) {
            holder.user_icon.setImageResource(R.drawable.member3)
            holder.user_name.text = "ヤマダ"
            holder.task_name.text = "関連研究探し"
        }
    }

    override fun getItemCount(): Int = 6
}