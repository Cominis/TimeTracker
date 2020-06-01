package com.dmt.timetracker.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dmt.timetracker.ui.CustomStatisticsFragment
import com.dmt.timetracker.ui.PredefinedStatisticsFragment
import com.dmt.timetracker.utils.*

class StatisticsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val timestampFrom = "timestampFrom"
    private val timestampTo = "timestampTo"
    private val day = 86_400_000L
    private val week = 604_800_000L
    private val month = 2_592_000_000L
    private val year = 31_536_000_000L

    private enum class Fragments(val position: Int) {
        TODAY(0),
        WEEK(1),
        MONTH(2),
        YEAR(3),
        CUSTOM(4);
    }

    override fun getItemCount(): Int = Fragments.values().size

    override fun createFragment(position: Int): Fragment {
        return when(position){
            Fragments.TODAY.position -> {
                val newFragment =
                    PredefinedStatisticsFragment()
                newFragment.arguments = Bundle().apply {
                    putLong(timestampFrom,
                        toMillis(startOfDay())
                    )
                    putLong(timestampTo,
                        toMillis(endOfDay())
                    )
                }
                newFragment
            }
            Fragments.WEEK.position -> {
                val newFragment =
                    PredefinedStatisticsFragment()
                newFragment.arguments = Bundle().apply {
                    putLong(timestampFrom,
                        toMillis(startOfWeek())
                    )
                    putLong(timestampTo,
                        toMillis(endOfWeek())
                    )
                }
                newFragment
            }
            Fragments.MONTH.position -> {
                val newFragment =
                    PredefinedStatisticsFragment()
                newFragment.arguments = Bundle().apply {
                    putLong(timestampFrom,
                        toMillis(startOfMonth())
                    )
                    putLong(timestampTo,
                        toMillis(endOfMonth())
                    )
                }
                newFragment
            }
            Fragments.YEAR.position -> {
                val newFragment =
                    PredefinedStatisticsFragment()
                newFragment.arguments = Bundle().apply {
                    putLong(timestampFrom,
                        toMillis(startOfYear())
                    )
                    putLong(timestampTo,
                        toMillis(endOfYear())
                    )
                }
                newFragment
            }
            Fragments.CUSTOM.position -> CustomStatisticsFragment()
            else -> CustomStatisticsFragment()
        }
    }
}