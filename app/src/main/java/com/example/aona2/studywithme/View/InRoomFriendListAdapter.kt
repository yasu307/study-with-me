package com.example.aona2.studywithme.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item_in_room.view.*

//StudyActivityのrecyclerViewのアダプター
//ルームにいる人のリスト
class InRoomFriendListAdapter internal constructor(val context: Context)
    : RecyclerView.Adapter<InRoomFriendListAdapter.InRoomFriendViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    //ルームにいる人のリスト　StudyActivityから更新される
    private var inRoomUsersList: MutableList<User> = mutableListOf<User>()

    inner class InRoomFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userIcon = itemView.user_icon_in_room
        val userName = itemView.user_name_in_room
        val taskName = itemView.task_name_in_room
        val taskStatusIcon = itemView.taskStatusIcon_inRoom
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InRoomFriendViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item_in_room, parent, false)
        return InRoomFriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InRoomFriendViewHolder, position: Int) {
        val user = inRoomUsersList[position]
        Picasso.get().load(user.userImageView).into(holder.userIcon)
        holder.userName.text = user.userName
        holder.taskName.text = HomeActivity.currentStudyInfos[user.uid]?.taskName
        Glide.with(context).load(R.drawable.edit_animation).into(holder.taskStatusIcon);
    }

    override fun getItemCount(): Int = inRoomUsersList.size

    //フィールドにルームにいるユーザーを保持　変更があれば自動で更新される
    //StudyActivityから呼ばれる
    internal fun setInRoomUsers(inRoomUsersList: MutableList<User>){
        this.inRoomUsersList = inRoomUsersList
        notifyDataSetChanged()
    }
}