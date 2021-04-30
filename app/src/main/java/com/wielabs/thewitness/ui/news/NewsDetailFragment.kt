package com.wielabs.thewitness.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.button.MaterialButton
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.FragmentNewsDetailBinding
import com.wielabs.thewitness.model.Comment
import com.wielabs.thewitness.model.News
import com.wielabs.thewitness.model.Reply
import com.wielabs.thewitness.ui.MainActivityViewModel
import com.wielabs.thewitness.ui.comment.CommentAdapter
import com.wielabs.thewitness.ui.comment.ReplyAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NewsDetailFragment : Fragment(), CommentAdapter.CommentAdapterListener,
    ReplyAdapter.ReplyAdapterListener, NewsSuggestionAdapter.NewsSuggestionAdapterListener {
    private lateinit var binding: FragmentNewsDetailBinding
    private val args: NewsDetailFragmentArgs by navArgs()
    private val news by lazy {
        args.news
    }

    private val newsSuggestionAdapter by lazy {
        NewsSuggestionAdapter(this)
    }

    private val commentAdapter by lazy {
        CommentAdapter(this, this)
    }

    @Inject
    lateinit var newsViewModel: NewsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            lifecycleOwner = this@NewsDetailFragment
            this.news = this@NewsDetailFragment.news
            this.newsViewModel = this@NewsDetailFragment.newsViewModel

            suggestionsRecyclerView.adapter = newsSuggestionAdapter
            PagerSnapHelper().attachToRecyclerView(suggestionsRecyclerView)
            this@NewsDetailFragment.run {
                newsViewModel.getNews().observe(viewLifecycleOwner) {
                    newsSuggestionAdapter.submitList(it.filter { news -> news.id != this@NewsDetailFragment.news.id }
                        .sortedByDescending { news -> news.likesCount })
                }

                mainActivityViewModel.getProfile().value?.let { profile ->
                    newsViewModel.refreshComments(
                        userId = profile.id,
                        forceRefresh = false
                    )
                }
                commentsRecyclerView.adapter = commentAdapter
                newsViewModel.getComments().observe(viewLifecycleOwner) { comments ->
                    val commentsForThisNews =
                        comments.filter { comment -> comment.newsId == this@NewsDetailFragment.news.id }
                    binding.comments = commentsForThisNews
                    commentAdapter.submitList(commentsForThisNews)
                }

                newsViewModel.getIsCommentPosted().observe(viewLifecycleOwner) {
                    isPosting = false
                    it?.let { isCommentPosted ->
                        if (isCommentPosted)
                            mainActivityViewModel.getProfile().value?.let { profile ->
                                newsViewModel.refreshComments(
                                    userId = profile.id,
                                    forceRefresh = true
                                )
                            }
                        newsViewModel.setIsCommentPosted(newValue = null)
                    }
                }

                newsViewModel.getIsReplyLikesUpdated().observe(viewLifecycleOwner) {
                    it?.let { isReplyLikesUpdated ->
                        if (isReplyLikesUpdated)
                            mainActivityViewModel.getProfile().value?.let { profile ->
                                newsViewModel.refreshComments(
                                    userId = profile.id,
                                    forceRefresh = true
                                )
                            }
                        newsViewModel.setIsReplyLikeUpdated(newValue = null)
                    }
                }

                newsViewModel.getIsCommentLikeUpdated().observe(viewLifecycleOwner) {
                    it?.let { isCommentLikeUpdated ->
                        if (isCommentLikeUpdated)
                            mainActivityViewModel.getProfile().value?.let { profile ->
                                newsViewModel.refreshComments(
                                    userId = profile.id,
                                    forceRefresh = true
                                )
                            }
                        newsViewModel.setIsCommentLikeUpdated(newValue = null)
                    }
                }

                postCommentButton.setOnClickListener {
                    mainActivityViewModel.getProfile().value?.let { profile ->
                        isPosting = true
                        newsViewModel.postComment(
                            userId = profile.id,
                            newsId = this@NewsDetailFragment.news.id,
                            name = profile.email.substring(0, profile.email.lastIndexOf('@')),
                            message = commentEditText.text.toString()
                        )
                    }
                }
            }

            newComment.setOnClickListener {
                findNavController().navigate(
                    NewsDetailFragmentDirections.actionNewsDetailFragmentToCommentBottomSheetDialog(
                        isComment = true,
                        comment = null,
                        reply = null,
                        newsId = this@NewsDetailFragment.news.id
                    )
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        binding.exoPlayerView.run {
            player?.let { exoPlayer ->
                mainActivityViewModel.currentPosition = exoPlayer.currentPosition
                exoPlayer.release()
                player = null
            }
        }
    }

    override fun onStart() {
        super.onStart()
        this.news.videoUrl?.let {
            binding.exoPlayerView.run {
                if (player == null) {
                    player = SimpleExoPlayer.Builder(requireContext()).build()
                    player?.let { exoPlayer ->
                        initializePlayer(player = player as SimpleExoPlayer, videoUrl = it)
                    }
                } else
                    initializePlayer(player = player as SimpleExoPlayer, videoUrl = it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivityViewModel.currentPosition = 0
    }

    override fun onCommentClicked(
        commentRepliesRecyclerView: RecyclerView,
        showOrHideReplies: View,
        comment: Comment
    ) {
        (showOrHideReplies as MaterialButton).text =
            if (commentRepliesRecyclerView.visibility == View.VISIBLE)
                getString(R.string.view_replies)
            else
                getString(R.string.hide_replies)

        commentRepliesRecyclerView.visibility =
            if (commentRepliesRecyclerView.visibility == View.VISIBLE)
                View.GONE
            else
                View.VISIBLE
    }

    override fun onCommentLikeClicked(comment: Comment) {
        mainActivityViewModel.getProfile().value?.let { profile ->
            newsViewModel.updateCommentLikes(comment = comment, userId = profile.id)
        }
    }

    override fun onCommentReplyClicked(comment: Comment) {
        findNavController().navigate(
            NewsDetailFragmentDirections.actionNewsDetailFragmentToCommentBottomSheetDialog(
                isComment = false,
                reply = null,
                comment = comment,
                newsId = this.news.id
            )
        )
    }

    override fun onReplyClicked(reply: Reply) {
        findNavController().navigate(
            NewsDetailFragmentDirections.actionNewsDetailFragmentToCommentBottomSheetDialog(
                isComment = false,
                reply = reply,
                comment = null,
                newsId = this.news.id
            )
        )
    }

    override fun onReplyLikeClicked(reply: Reply) {
        mainActivityViewModel.getProfile().value?.let { profile ->
            newsViewModel.updateReplyLikes(reply = reply, userId = profile.id)
        }
    }

    override fun onNewsSuggestionClick(view: View, news: News) {
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        findNavController().navigate(
            NewsDetailFragmentDirections.actionNewsDetailFragmentSelf(
                news = news
            ), extras
        )
    }

    private fun initializePlayer(player: SimpleExoPlayer, videoUrl: String) {
        player.run {
            mainActivityViewModel.videoUri = videoUrl
            player.playWhenReady = true
            setMediaSource(
                DashMediaSource.Factory(DefaultHttpDataSourceFactory()).createMediaSource(
                    MediaItem.fromUri(videoUrl)
                )
            )
            seekTo(mainActivityViewModel.currentPosition)
            prepare()
        }
    }
}