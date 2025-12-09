package com.openclassroom.p8_vitesse.ui.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassroom.p8_vitesse.ui.homeScreen.CandidateAdapter.CandidateViewHolder
import com.openclassroom.p8_vitesse.R
import com.openclassroom.p8_vitesse.domain.Candidate

class CandidateAdapter() : ListAdapter<Candidate, CandidateViewHolder>(CandidateDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.candidate_item, parent, false)
        return CandidateViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = getItem(position)
        Glide.with(holder.itemView.context)// Transformation de l'URI en chemin compréhensible par le ImageView
            .load(candidate.photo)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .centerCrop()
            .into(holder.picture)
        holder.name.text = String.format(candidate.firstName + " " + candidate.lastName)
        holder.note.text = candidate.note
        holder.itemView.setOnClickListener {
            val id = candidate.id ?: return@setOnClickListener
            val bundle = Bundle().apply { putLong("candidateId", id) }
            Navigation.findNavController(it).navigate(R.id.action_to_detailFragment,bundle)
        }

    }

    inner class CandidateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var picture: ImageView
        var name: TextView
        var note: TextView

        init {
            picture = itemView.findViewById(R.id.candidate_picture)
            name = itemView.findViewById(R.id.candidate_name)
            note = itemView.findViewById(R.id.candidate_note)
        }
    }

    companion object {
        private val CandidateDiffUtil: DiffUtil.ItemCallback<Candidate> =
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