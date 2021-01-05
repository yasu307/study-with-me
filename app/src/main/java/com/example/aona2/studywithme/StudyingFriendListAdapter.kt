package com.example.aona2.studywithme

import android.content.Context
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item.view.*

//HomeActivityの勉強中の友達リストのアダプター
class StudyingFriendListAdapter internal constructor(context: Context, listener: Listener)
    : RecyclerView.Adapter<StudyingFriendListAdapter.StudyingFriendViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    //実際のデータが作成できたら使用する
//    private val friends = emptyList<Friend>()
    private val clickListener: Listener = listener

    inner class StudyingFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val user_icon = itemView.user_icon
        val user_name = itemView.user_name
        val task_name = itemView.task_name
        val remain_time = itemView.remain_time
        val task_status_icon = itemView.task_status_icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyingFriendViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return StudyingFriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudyingFriendViewHolder, position: Int) {
        //ダミーデータを手動入力
        if(position == 0){
            holder.user_icon.setImageResource(R.drawable.member1)
            holder.user_name.text = "ヨシダ"
            holder.task_status_icon.setImageResource(R.drawable.study_status)
            holder.remain_time.text = "15分"
            holder.task_name.text = "実システム創造　進捗作成"
            holder.itemView.setOnClickListener {
                clickListener.onItemClicked(holder.adapterPosition)
            }
        }
        if(position == 1){
            holder.user_icon.setImageResource(R.drawable.member2)
            holder.user_name.text = "フクダ"
            holder.task_status_icon.setImageResource(R.drawable.breaktime_status)
            holder.remain_time.text = "2分"
            holder.task_name.text = ""
            holder.itemView.setOnClickListener {
                clickListener.onItemClicked(holder.adapterPosition)
            }
        }
        if(position == 2){
            holder.user_icon.setImageResource(R.drawable.member3)
            holder.user_name.text = "ヤマダ"
            holder.task_status_icon.setImageResource(R.drawable.study_status)
            holder.remain_time.text  = "20分"
            holder.task_name.text = "関連研究探し"
            holder.itemView.setOnClickListener {
                clickListener.onItemClicked(holder.adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int = 3

    //itemがクリックされたか
    interface Listener{
        fun onItemClicked(index: Int)
    }
}