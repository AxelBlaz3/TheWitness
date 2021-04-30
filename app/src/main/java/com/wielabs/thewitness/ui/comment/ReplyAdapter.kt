package com.wielabs.thewitness.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wielabs.thewitness.databinding.ReplyItemBinding
import com.wielabs.thewitness.model.Reply

class ReplyAdapter(private val listener: ReplyAdapterListener) : ListAdapter<Reply, ReplyAdapter.ReplyViewHolder>(ReplyDiffUtil) {

    interface ReplyAdapterListener {
        fun onReplyClicked(reply: Reply)
        fun onReplyLikeClicked(reply: Reply)
    }

    object ReplyDiffUtil : DiffUtil.ItemCallback<Reply>() {
        override fun areItemsTheSame(oldItem: Reply, newItem: Reply): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Reply, newItem: Reply): Boolean =
            oldItem.author == newItem.author &&
                    oldItem.likes == newItem.likes &&
                    oldItem.message == newItem.message &&
                    oldItem.newsId == newItem.newsId &&
                    oldItem.commentId == newItem.commentId &&
                    oldItem.isLiked == newItem.isLiked &&
                    oldItem.timestamp == newItem.timestamp
    }

    class ReplyViewHolder(private val binding: ReplyItemBinding, private val listener: ReplyAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reply: Reply) {
            binding.run {
                this.reply = reply
                this.listener = this@ReplyViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder =
        ReplyViewHolder(
            ReplyItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) =
        holder.bind(getItem(position))
}