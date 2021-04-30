package com.wielabs.thewitness.ui.news

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.wielabs.thewitness.BuildConfig
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.FragmentNewsBinding
import com.wielabs.thewitness.model.News
import com.wielabs.thewitness.ui.MainActivityViewModel
import com.wielabs.thewitness.ui.home.HomeFragmentDirections
import com.wielabs.thewitness.util.Constants
import com.wielabs.thewitness.util.SharedPreferenceUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class NewsFragment : Fragment(), NewsAdapter.NewsAdapterListener {
    private lateinit var binding: FragmentNewsBinding
    private val newsAdapter by lazy {
        NewsAdapter(this)
    }
    private val telephoneManager by lazy {
        requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
    private val countryCode by lazy {
        telephoneManager.networkCountryIso.toLowerCase()
    }
    private val myRegionToCheck by lazy {
        when (countryCode) {
            in Constants.asianCountries -> Constants.asianCountries
            in Constants.africanCountries -> Constants.africanCountries
            in Constants.antarcticaCountries -> Constants.antarcticaCountries
            in Constants.australianCountries -> Constants.australianCountries
            in Constants.europeanCountries -> Constants.europeanCountries
            in Constants.northAmericanCountries -> Constants.northAmericanCountries
            in Constants.southAmericanCountries -> Constants.southAmericanCountries
            else -> Constants.asianCountries
        }
    }
    private var position: Int = 0

    @Inject
    lateinit var newsViewModel: NewsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        binding.run {
            tapToRefresh.setOnClickListener {
                mainActivityViewModel.getProfile().value?.let { profile ->
                    newsViewModel.refreshNews(userId = profile.id, forceRefresh = true)

                }
            }

            newsSwipeRefreshLayout.setOnRefreshListener {
                mainActivityViewModel.getProfile().value?.let { profile ->
                    newsViewModel.run {
                        refreshNews(userId = profile.id, forceRefresh = true)
                    }
                }
                newsSwipeRefreshLayout.isRefreshing = false
            }

            newsRecyclerView.apply {
                adapter = newsAdapter
            }

            newsViewModel.getNews().observe(viewLifecycleOwner) {
                newsAdapter.submitList(getListForPosition(position, it)) {
                    it.getOrNull(0)?.let { news ->
                        newsViewModel.setSuggestReload(
                            news.lastFetched == -1L || Date().time - news.lastFetched >= TimeUnit.DAYS.toMillis(
                                1
                            )
                        )
                    }
                }
            }

            newsViewModel.getLastFetched().observe(viewLifecycleOwner) { lastFetched ->
                if (lastFetched == -1L) {
                    mainActivityViewModel.getProfile().value?.let { profile ->
                        newsViewModel.refreshNews(userId = profile.id, forceRefresh = true)
                        return@observe
                    }
                } else
                    newsViewModel.setSuggestReload(
                        Date().time - lastFetched >= TimeUnit.DAYS.toMillis(
                            1
                        )
                    )
            }

            newsViewModel.getIsNewsLikeUpdated().observe(viewLifecycleOwner) {
                it?.let { isLikeUpdated ->
                    if (isLikeUpdated)
                        mainActivityViewModel.getProfile().value?.let { profile ->
                            newsViewModel.refreshNews(userId = profile.id, forceRefresh = true)
                        }
                }
            }
        }
    }

    override fun onNewsClicked(view: View, news: News) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToNewsDetailFragment(news = news)
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
            Snackbar.make(
                binding.root,
                getString(R.string.some_error_occurred),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun getListForPosition(position: Int, news: List<News>): List<News> =
        try {
            when (position) {
                0 -> news.filter { news -> news.region.toLowerCase() in myRegionToCheck.keys }
                1 -> news.filter { news -> news.region.toLowerCase() in Constants.asianCountries.keys }
                2 -> news.filter { news -> news.region.toLowerCase() in Constants.africanCountries.keys }
                3 -> news.filter { news -> news.region.toLowerCase() in Constants.northAmericanCountries.keys }
                4 -> news.filter { news -> news.region.toLowerCase() in Constants.southAmericanCountries.keys }
                5 -> news.filter { news -> news.region.toLowerCase() in Constants.antarcticaCountries.keys }
                6 -> news.filter { news -> news.region.toLowerCase() in Constants.europeanCountries.keys }
                7 -> news.filter { news -> news.region.toLowerCase() in Constants.australianCountries.keys }
                else -> throw IllegalArgumentException("Unknown position $position while getting news")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
}