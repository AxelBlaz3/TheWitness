package com.wielabs.thewitness.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.wielabs.thewitness.R
import com.wielabs.thewitness.databinding.FragmentHomeBinding
import com.wielabs.thewitness.ui.MainActivityViewModel
import com.wielabs.thewitness.ui.news.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var newsViewModel: NewsViewModel

    @Inject
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityViewModel.getProfile().observe(viewLifecycleOwner) { profile ->
            if (profile == null)
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSignInFragment())
            else
                setupTabs()
        }
    }

    private fun setupTabs() {
        binding.run {
            homeViewPager.adapter =
                HomeStateAdapter(fragment = this@HomeFragment)
            TabLayoutMediator(homeTabLayout, homeViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.for_you)
                    1 -> getString(R.string.asia)
                    2 -> getString(R.string.africa)
                    3 -> getString(R.string.north_america)
                    4 -> getString(R.string.south_america)
                    5 -> getString(R.string.antarctica)
                    6 -> getString(R.string.europe)
                    7 -> getString(R.string.australia)
                    else -> throw RuntimeException("${this@HomeFragment.javaClass.simpleName}: Unknown position - $position when setting up TabLayout")
                }
            }.attach()
        }
    }
}