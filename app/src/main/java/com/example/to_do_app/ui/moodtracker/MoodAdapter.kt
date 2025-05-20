package com.example.to_do_app.ui.moodtracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.data.model.Mood
import com.example.to_do_app.databinding.ItemMoodBinding

class MoodAdapter : ListAdapter<Mood, MoodAdapter.MoodViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = getItem(position)
        holder.bind(mood)
    }

    inner class MoodViewHolder(private val binding: ItemMoodBinding) : RecyclerView.ViewHolder(binding.root) {
        // cập nhật hiển thị emoji động
        fun bind(mood: Mood) {
            binding.tvMoodDate.text = mood.date
            binding.tvMoodType.text = when (mood.moodType) {
                "Happy" -> "😊 Happy"
                "Sad" -> "😢 Sad"
                "Neutral" -> "😐 Neutral"
                "Stressed" -> "😓 Stressed"
                "Angry" -> "😡 Angry"
                else -> "Unknown"
            }
            // thêm hàm cập nhật mood type
            binding.tvMoodType.setTextColor(when (mood.moodType) {
                "Happy" -> 0xFF4CAF50.toInt() // Xanh lá
                "Sad" -> 0xFF2196F3.toInt()   // Xanh dương
                "Neutral" -> 0xFFFF9800.toInt() // Cam
                "Stressed" -> 0xFFF44336.toInt() // Đỏ
                "Angry" -> 0xFF9C27B0.toInt()  // Tím
                else -> 0xFF000000.toInt()     // Đen
            })
            binding.tvMoodNote.text = if (mood.note.isNotEmpty()) "Note: ${mood.note}" else ""
        }
    }
}

class MoodDiffCallback : DiffUtil.ItemCallback<Mood>() {
    override fun areItemsTheSame(oldItem: Mood, newItem: Mood): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: Mood, newItem: Mood): Boolean {
        return oldItem == newItem
    }
}