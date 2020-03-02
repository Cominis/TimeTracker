package com.example.lifetracker.statisticsSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lifetracker.R
import com.example.lifetracker.database.RoutineDatabase
import com.example.lifetracker.databinding.FragmentStatisticsSettingsBinding
import com.example.lifetracker.mainActivity.MainActivityViewModel


class StatisticsSettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatisticsSettingsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_statistics_settings, container, false)

        //val arguments = StatisticsSettingsFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val viewModelFactory = StatisticsSettingsViewModelFactory(0L, dataSource = dataSource)
        val statisticsSettingsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(StatisticsSettingsViewModel::class.java)

        binding.overAllButton.setOnClickListener { view ->
            this.findNavController().navigate(
                StatisticsSettingsFragmentDirections.actionStatisticsSettingsFragmentToStatisticsFragment())
        }

        binding.overAllButtonTest.setOnClickListener { view ->
            this.findNavController().navigate(
                StatisticsSettingsFragmentDirections.actionStatisticsSettingsFragmentToOverAllStatisticsFragment())
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.statistics_settings))
    }

}
