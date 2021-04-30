package com.wielabs.thewitness.ui.home

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wielabs.thewitness.ui.news.NewsFragment

private const val TABS_COUNT = 8

class HomeStateAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = TABS_COUNT

    override fun createFragment(position: Int): Fragment =
        NewsFragment().apply { arguments = bundleOf("position" to position) }
}