package com.trinitydevelopers.constitutionofindia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.constitutionofindia.R
import com.trinitydevelopers.constitutionofindia.model.CaseStudyItem
import com.trinitydevelopers.constitutionofindia.model.ScheduleItem

class CaseStudyAdapter(private val caseStudy: List<CaseStudyItem>
, private val onItemClick: (CaseStudyItem) -> Unit): RecyclerView.Adapter<CaseStudyAdapter.CaseStudyViewHolder>() {
    class CaseStudyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val title=itemView.findViewById<TextView>(R.id.caseStudy_title)
        val fadeInAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.left_to_right)

        init {
            // Optionally, set up animation here
            fadeInAnimation.startOffset = adapterPosition * 200L // Adjust delay per item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaseStudyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_casestudy, parent, false)
         return CaseStudyViewHolder(view)

    }

    override fun getItemCount(): Int {
        return caseStudy.size
    }

    override fun onBindViewHolder(holder: CaseStudyViewHolder, position: Int) {
        holder.title.text=caseStudy[position].title
        holder.itemView.clearAnimation() // Clear any previous animations
        holder.itemView.startAnimation(holder.fadeInAnimation)
        holder.itemView.setOnClickListener {
            onItemClick(caseStudy[position])
        }
    }
}