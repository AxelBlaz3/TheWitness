package com.wielabs.thewitness.ui.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.FragmentCommentBottomSheetBinding
import com.wielabs.thewitness.ui.MainActivityViewModel
import com.wielabs.thewitness.ui.news.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCommentBottomSheetBinding
    private val args: CommentBottomSheetDialogArgs by navArgs()
    private val isComment by lazy {
        args.isComment
    }
    private val reply by lazy {
        args.reply
    }
    private val newsId by lazy {
        args.newsId
    }
    private val comment by lazy {
        args.comment
    }

    @Inject
    lateinit var newsViewModel: NewsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            authorName =
                if (reply == null && comment == null) null else if (reply == null) comment?.author else reply?.author
            isComment = this@CommentBottomSheetDialog.isComment

            newsViewModel.getIsCommentEmpty().observe(viewLifecycleOwner) {
                it?.let { isCommentEmpty ->
                    if (isCommentEmpty) {
                        opinionEditText.run {
                            error = context.getString(R.string.opinion_empty)
                            requestFocus()
                        }
                        isPosting = false
                    }
                    newsViewModel.setIsCommentEmpty(null)
                }
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
                    dismiss()
                }
            }

            newsViewModel.getIsReplyPosted().observe(viewLifecycleOwner) {
                isPosting = false
                it?.let { isReplyPosted ->
                    if (isReplyPosted)
                        mainActivityViewModel.getProfile().value?.let { profile ->
                            newsViewModel.refreshComments(
                                userId = profile.id,
                                forceRefresh = true
                            )
                        }
                    newsViewModel.setIsReplyPosted(newValue = null)
                    dismiss()
                }
            }

            postComment.setOnClickListener {
                isPosting = true
                mainActivityViewModel.getProfile().value?.let { profile ->
                    if (this@CommentBottomSheetDialog.isComment)
                        newsViewModel.postComment(
                            userId = profile.id,
                            newsId = this@CommentBottomSheetDialog.newsId,
                            name = profile.email.substring(0, profile.email.lastIndexOf('@')),
                            message = opinionEditText.text.toString()
                        )
                    else
                        newsViewModel.postReply(
                            userId = profile.id,
                            comment = comment,
                            reply = reply,
                            name = profile.email.substring(0, profile.email.lastIndexOf('@')),
                            message = opinionEditText.text.toString()
                        )
                }
            }

            cancelComment.setOnClickListener { dismiss() }
        }
    }
}
