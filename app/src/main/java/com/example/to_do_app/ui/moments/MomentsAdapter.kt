package com.example.to_do_app.ui.moments

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_app.data.model.Moment
import com.example.to_do_app.databinding.ItemMomentBinding
import java.io.File

class MomentsAdapter(
    private val onEditClick: (Moment) -> Unit,
    private val onDeleteClick: (Moment) -> Unit
) : ListAdapter<Moment, MomentsAdapter.MomentViewHolder>(MomentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MomentViewHolder {
        val binding = ItemMomentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MomentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MomentViewHolder, position: Int) {
        val moment = getItem(position)
        holder.bind(moment)
    }

    inner class MomentViewHolder(private val binding: ItemMomentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(moment: Moment) {
            binding.tvMomentTitle.text = moment.title
            binding.tvMomentDescription.text = moment.description
            binding.tvMomentDate.text = moment.date

            // Hiển thị ảnh nếu có
            if (moment.imagePath != null && File(moment.imagePath).exists()) {
                val bitmap = BitmapFactory.decodeFile(moment.imagePath)
                binding.ivMomentImage.setImageBitmap(bitmap)
                binding.ivMomentImage.visibility = View.VISIBLE
            } else {
                binding.ivMomentImage.visibility = View.GONE
            }

            binding.btnEdit.setOnClickListener {
                onEditClick(moment)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(moment)
            }
        }
    }
}

class MomentDiffCallback : DiffUtil.ItemCallback<Moment>() {
    override fun areItemsTheSame(oldItem: Moment, newItem: Moment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Moment, newItem: Moment): Boolean {
        return oldItem == newItem
    }
}