package com.wielabs.thewitness.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.wielabs.thewitness.BuildConfig
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.FragmentSearchBinding
import com.wielabs.thewitness.model.News
import com.wielabs.thewitness.ui.MainActivity
import com.wielabs.thewitness.ui.MainActivityViewModel
import com.wielabs.thewitness.ui.news.NewsAdapter
import com.wielabs.thewitness.ui.news.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), NewsAdapter.NewsAdapterListener {
    private lateinit var binding: FragmentSearchBinding
    private val newsAdapter by lazy {
        NewsAdapter(this)
    }

    @Inject
    lateinit var newsViewModel: NewsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    private val backPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as MainActivity).run {
                    hideKeyboard()
                    search_edit_text.clearFocus()
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        binding.searchEmpty = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            newsRecyclerView.adapter = newsAdapter
            (requireActivity()).findViewById<TextInputEditText>(R.id.search_edit_text)
                .addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        s?.let {
                            searchEmpty = it.toString().isEmpty()
                            newsAdapter.submitList(filterSearchedKeywords(keywords = it.toString()))
                        }
                    }
                })
        }
    }

    override fun onNewsClicked(view: View, news: News) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToNewsDetailFragment(
                news = news
            )
        )
    }

    override fun onLikeClicked(news: News) {
        mainActivityViewModel.getProfile().value?.let { profile ->
            newsViewModel.updateNewsLikes(news = news, userId = profile.id)
        }
    }

    override fun onShareClicked(news: News) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(news.articleLink)
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(binding.root, getString(R.string.some_error_occurred), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun filterSearchedKeywords(keywords: String): List<News>? {
        if (keywords.isEmpty())
            return emptyList()
        newsViewModel.getNews().value?.let { newsList ->
            return newsList.filter { news ->
                isUserSearchedNews(tags = news.tags, keywords = keywords.split(' '))
            }
        }
        return emptyList()
    }

    private fun isUserSearchedNews(tags: List<String>, keywords: List<String>): Boolean {
        for (tag in tags)
            for (keyword in keywords)
                if (tag.startsWith(keyword) || tag == keyword)
                    return true
        return false
    }
}