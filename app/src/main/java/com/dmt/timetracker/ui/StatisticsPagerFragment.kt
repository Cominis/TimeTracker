package com.dmt.timetracker.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.viewpager2.widget.ViewPager2
import com.dmt.timetracker.R
import com.dmt.timetracker.adapters.StatisticsPagerAdapter
import com.dmt.timetracker.databinding.FragmentStatisticsViewPagerBinding
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.google.android.material.tabs.TabLayoutMediator


class StatisticsPagerFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatisticsViewPagerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_statistics_view_pager, container, false)

        //NavigationUI.setupWithNavController(toolbar, NavHostFragment.findNavController(nav_host))
        setHasOptionsMenu(true)

        val viewPager = binding.statisticsViewPager
        viewPager.adapter =
            StatisticsPagerAdapter(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var mCurrentPosition = 0
            private var mScrollState = 0

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mCurrentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                handleScrollState(state)
                mScrollState = state
            }

            private fun handleScrollState(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (mScrollState != ViewPager2.SCROLL_STATE_SETTLING) {
                        val lastPosition: Int = viewPager.adapter!!.itemCount - 1
                        if (mCurrentPosition == 0) {
                            viewPager.setCurrentItem(lastPosition, false)
                        } else if (mCurrentPosition == lastPosition) {
                            viewPager.setCurrentItem(0, false)
                        }
                    }
                }
            }
        })

        val titles = resources.getStringArray(R.array.statisticsPageTitles)
        TabLayoutMediator(binding.tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.statistics))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.custom_statistics, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            NavHostFragment.findNavController(this))
                || super.onOptionsItemSelected(item)
    }
}
