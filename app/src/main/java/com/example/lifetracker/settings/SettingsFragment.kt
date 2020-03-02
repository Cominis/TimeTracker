package com.example.lifetracker.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lifetracker.R
import com.example.lifetracker.database.RoutineDatabase
import com.example.lifetracker.databinding.FragmentSettingsBinding
import com.example.lifetracker.mainActivity.MainActivityViewModel


class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentSettingsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_settings, container, false)

        //val arguments = SettingsFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val viewModelFactory = SettingsViewModelFactory(dataSource = dataSource)
        val settingsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(SettingsViewModel::class.java)

        settingsViewModel.routineId

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.settings))
    }
}
