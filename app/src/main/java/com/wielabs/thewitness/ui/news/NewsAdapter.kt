package com.wielabs.thewitness.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.wielabs.thewitness.databinding.NewsItemBinding
import com.wielabs.thewitness.model.News

class NewsAdapter(private val newsAdapterListener: NewsAdapterListener) : ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffUtil) {

    interface NewsAdapterListener {
        fun onNewsClicked(view: View, news: News)
        fun onLikeClicked(news: News)
        fun onShareClicked(news: News)
    }

    object NewsDiffUtil : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
            oldItem.comments == newItem.comments &&
                    oldItem.commentsCount == newItem.commentsCount &&
                    oldItem.description == newItem.description &&
                    oldItem.imageUrl == newItem.imageUrl &&
                    oldItem.likesCount == newItem.likesCount &&
                    oldItem.title == newItem.title &&
                    oldItem.videoUrl == newItem.videoUrl
    }

    class NewsViewHolder(private val binding: NewsItemBinding, private val newsAdapterListener: NewsAdapterListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News, position: Int) {
            binding.run {
                this.news = news
                this.newsAdapterListener = this@NewsViewHolder.newsAdapterListener
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder =
        NewsViewHolder(NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), newsAdapterListener)

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) =
        holder.bind(getItem(position), position)
}