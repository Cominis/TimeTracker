package com.example.lifetracker.overAllStatistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.lifetracker.R
import com.example.lifetracker.database.RoutineDatabase
import com.example.lifetracker.databinding.FragmentStatisticsViewPagerBinding
import com.example.lifetracker.mainActivity.MainActivityViewModel
import com.example.lifetracker.statistics.StatisticsFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayout

class OverAllStatisticsFragment : Fragment() {

    private lateinit var overAllCollectionAdapter: OverAllCollectionAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatisticsViewPagerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_statistics_view_pager, container, false)

        setHasOptionsMenu(true)
        //val arguments = StatisticsFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val viewModelFactory = OverAllStatisticsViewModelFactory(dataSource = dataSource)
        val overAllStatisticsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(OverAllStatisticsViewModel::class.java)

        overAllStatisticsViewModel.routineId

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.over_all_statistics))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        overAllCollectionAdapter = OverAllCollectionAdapter(this)
        viewPager = view.findViewById(R.id.overAllStatisticsViewPager)
        viewPager.adapter = overAllCollectionAdapter

        val tabLayout = view.findViewById(R.id.tab_layout) as  TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = Fragments.fromInt(position).name
        }.attach()

    }
}

private enum class Fragments(val position: Int) {
    TODAY(0),
    WEEK(1),
    MONTH(2),
    YEAR(3);

    companion object {
        private val map = Fragments.values().associateBy(Fragments::position)
        fun fromInt(type: Int) = map[type] ?: TODAY
    }
}

class OverAllCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = Fragments.values().size

    override fun createFragment(position: Int): Fragment {
        val fragment = when(position){
            Fragments.TODAY.position -> DemoObjectFragment()
            Fragments.WEEK.position -> StatisticsFragment()
            Fragments.MONTH.position -> DemoObjectFragment()
            Fragments.YEAR.position -> DemoObjectFragment()
            else -> DemoObjectFragment()
        }

        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }
}

private const val ARG_OBJECT = "object"

class DemoObjectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(android.R.id.text1)
            textView.text = getInt(ARG_OBJECT).toString()
        }
    }
}
