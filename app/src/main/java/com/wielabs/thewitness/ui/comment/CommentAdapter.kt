package com.wielabs.thewitness.ui.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.wielabs.thewitness.databinding.CommentItemBinding
import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.News

class CommentAdapter(
    private val commentAdapterListener: CommentAdapterListener,
    private val replyAdapterListener: ReplyAdapter.ReplyAdapterListener
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentDiffUtil) {

    interface CommentAdapterListener {
        fun onCommentClicked(commentRepliesRecyclerView: RecyclerView, showOrHideReplies: View, comment: Comment)
        fun onCommentLikeClicked(comment: Comment)
        fun onCommentReplyClicked(comment: Comment)
    }

    object CommentDiffUtil : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.author == newItem.author &&
                    oldItem.likes == newItem.likes &&
                    oldItem.message == newItem.message &&
                    oldItem.isLiked == newItem.isLiked &&
                    oldItem.userId == newItem.userId &&
                    oldItem.newsId == newItem.newsId &&
                    oldItem.replyId == newItem.replyId &&
                    oldItem.replies == newItem.replies &&
                    oldItem.timestamp == newItem.timestamp
    }

    inner class CommentViewHolder(
        private val binding: CommentItemBinding,
        private val listener: CommentAdapterListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val replyAdapter by lazy {
            ReplyAdapter(replyAdapterListener)
        }

        fun bind(comment: Comment) {
            binding.run {
                commentRepliesRecyclerView.adapter = replyAdapter
                replyAdapter.submitList(comment.replies)
                this.comment = comment
                this.listener = this@CommentViewHolder.listener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(
            CommentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            commentAdapterListener
        )

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) =
        holder.bind(getItem(position))
}