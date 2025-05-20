package com.example.to_do_app.ui.focustimer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.data.model.FocusSession
import com.example.to_do_app.databinding.ItemFocusSessionBinding

class FocusSessionAdapter : ListAdapter<FocusSession, FocusSessionAdapter.FocusSessionViewHolder>(FocusSessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FocusSessionViewHolder {
        val binding = ItemFocusSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FocusSessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FocusSessionViewHolder, position: Int) {
        val focusSession = getItem(position)
        holder.bind(focusSession)
    }

    inner class FocusSessionViewHolder(private val binding: ItemFocusSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(focusSession: FocusSession) {
            binding.tvStartTime.text = "Start: ${focusSession.startTime}"
            binding.tvDuration.text = "Duration: ${focusSession.durationMinutes} minutes"
            binding.tvStatus.text = "Status: ${focusSession.status.capitalize()}"
        }
    }
}

class FocusSessionDiffCallback : DiffUtil.ItemCallback<FocusSession>() {
    override fun areItemsTheSame(oldItem: FocusSession, newItem: FocusSession): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FocusSession, newItem: FocusSession): Boolean {
        return oldItem == newItem
    }
}