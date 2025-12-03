package com.openclassroom.p8_vitesse.ui.HomeScreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassroom.p8_vitesse.ui.HomeScreen.CandidateAdapter.CandidateViewHolder
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.domain.Candidate

class CandidateAdapter() : ListAdapter<Candidate, CandidateViewHolder> (CandidateDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.candidate_item, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = getItem(position)
        Glide.with(holder.itemView.context).load(candidate.photo).into(holder.picture)// Transformation de l'URI en chemin compréhensible par le ImageView
        holder.name.text = String.format(candidate.firstName + candidate.lastName)
        holder.note.text = candidate.note

    }

    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var picture: ImageView
        var name: TextView
        var note: TextView

        init {
            picture = itemView.findViewById(R.id.candidate_picture)
            name = itemView.findViewById(R.id.candidate_name)
            note = itemView.findViewById(R.id.candidate_note)
        }
    }

    companion object{
        private val CandidateDiffUtil : DiffUtil.ItemCallback<Candidate> =
            object : DiffUtil.ItemCallback<Candidate>() {
                override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
                    return oldItem == newItem
                }
            }
    }
}