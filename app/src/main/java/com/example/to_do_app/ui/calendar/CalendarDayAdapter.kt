package com.example.to_do_app.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.R
import com.example.to_do_app.databinding.ItemCalendarDayBinding

class CalendarDayAdapter(
    private val onDayClick: (String) -> Unit
) : ListAdapter<String, CalendarDayAdapter.DayViewHolder>(DayDiffCallback()) {

    private var selectedDate: String? = null
    private var currentDate: String? = null

    fun setSelectedDate(date: String) {
        selectedDate = date
        notifyDataSetChanged()
    }

    fun setCurrentDate(date: String) {
        currentDate = date
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = getItem(position)
        holder.bind(day)
    }

    inner class DayViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            if (day.isEmpty()) {
                binding.tvDay.text = ""
                binding.tvDay.isClickable = false
                binding.tvDay.background = null
                binding.tvDay.setTextColor(binding.tvDay.context.getColor(R.color.textColor))
            } else {
                val dayOfMonth = day.split("-")[2].toInt().toString()
                binding.tvDay.text = dayOfMonth

                // Highlight ngày hiện tại
                if (day == currentDate) {
                    binding.tvDay.background = binding.tvDay.context.getDrawable(R.drawable.circle_background_current)
                    binding.tvDay.setTextColor(binding.tvDay.context.getColor(R.color.accentColor))
                }
                // Highlight ngày được chọn
                else if (day == selectedDate) {
                    binding.tvDay.background = binding.tvDay.context.getDrawable(R.drawable.circle_background)
                    binding.tvDay.setTextColor(binding.tvDay.context.getColor(R.color.primaryColor))
                } else {
                    binding.tvDay.background = null
                    binding.tvDay.setTextColor(binding.tvDay.context.getColor(R.color.textColor))
                }

                binding.tvDay.setOnClickListener {
                    onDayClick(day)
                }
            }
        }
    }
}

class DayDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}