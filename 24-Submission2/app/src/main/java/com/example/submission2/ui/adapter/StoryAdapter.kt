package com.example.submission2.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.submission2.R
import com.example.submission2.data.model.Story
import com.example.submission2.databinding.StoryItemLayoutBinding
import com.example.submission2.utils.GlideApp
import com.example.submission2.utils.Helper

class StoryAdapter :
    PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            holder.bind(storyItem)
        }
    }

    class ViewHolder(private var binding: StoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(storyItem: Story) {
            binding.apply {
                btnStoryMore.setOnClickListener {
                    expandTextDescription()
                }
                tvStoryDesc.setOnClickListener {
                    expandTextDescription()
                }
                GlideApp.with(itemView.context).load(storyItem.photoUrl)
                    .placeholder(R.drawable.round_image_200).into(ivStoryPhoto)
                tvUserName.text = storyItem.name
                tvStoryDesc.text = storyItem.description
                tvStoryDate.text =
                    Helper.generateExactDiffTime(itemView.context, storyItem.createdAt)
                Helper.Location.generateLocation(
                    itemView.context,
                    storyItem.lat,
                    storyItem.lon
                ) { cityName ->
                    tvStoryLocation.text = cityName
                }
            }
        }

        private fun expandTextDescription() {
            val textButton = binding.btnStoryMore.text.toString()
            if (textButton == itemView.context.getString(R.string.desc_more_text)) {
                binding.tvStoryDesc.maxLines = Integer.MAX_VALUE
                binding.btnStoryMore.text = itemView.context.getString(R.string.desc_less_text)
            } else {
                binding.tvStoryDesc.maxLines = 2
                binding.btnStoryMore.text = itemView.context.getString(R.string.desc_more_text)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Story> = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(
                oldItem: Story, newItem: Story
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Story, newItem: Story
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}