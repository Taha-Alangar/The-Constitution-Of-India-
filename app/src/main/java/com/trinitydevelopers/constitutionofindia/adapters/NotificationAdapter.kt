package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.model.NotificationItem

class NotificationAdapter(private val notificationList: List<NotificationItem>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title=itemView.findViewById<TextView>(R.id.notitifcation_title)
        val description=itemView.findViewById<TextView>(R.id.notification_description)
        val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

        init {
            // Optionally, set up animation here
            fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.viewholder_notification,parent,false)
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
    return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.title.text=notificationList[position].title
        holder.description.text=notificationList[position].small_description
        holder.itemView.clearAnimation() // Clear any previous animations
        holder.itemView.startAnimation(holder.fadeInAnimation)
    }
}