package com.example.lifetracker.routineTracker

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.lifetracker.R
import com.example.lifetracker.database.RoutineDatabase
import com.example.lifetracker.databinding.FragmentRoutineTrackerBinding
import com.example.lifetracker.mainActivity.MainActivityViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*


class RoutineTrackerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentRoutineTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_routine_tracker, container, false)

        setHasOptionsMenu(true)

        val application = requireActivity().application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val viewModelFactory = RoutineTrackerViewModelFactory(dataSource)
        val routineTrackerViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RoutineTrackerViewModel::class.java)

        binding.routineTrackerViewModel = routineTrackerViewModel
        binding.lifecycleOwner = this

        routineTrackerViewModel.isStartButton.observe(viewLifecycleOwner, Observer { value ->
            if(value == false) {
                binding.chronometer.base = SystemClock.elapsedRealtime() - routineTrackerViewModel.getCurrentDuration()
                binding.chronometer.start()
            }
        })

        routineTrackerViewModel.navigateToRoutineSave.observe(viewLifecycleOwner, Observer { routine ->
            routine?.let {
                this.findNavController().navigate(
                    RoutineTrackerFragmentDirections
                        .actionRoutineTrackerFragmentToRoutineSaveFragment(routine.id))
                routineTrackerViewModel.doneNavigating()
            }
        })

        routineTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Prepopulation completed",
                    Snackbar.LENGTH_SHORT
                ).show()

                routineTrackerViewModel.doneShowingSnackbar()
            }
        })

        binding.chronometer.onChronometerTickListener =
            OnChronometerTickListener { cArg ->
                val time = SystemClock.elapsedRealtime() - cArg.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                cArg.text = String.format("%02d:%02d:%02d", h, m, s );
            }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.track_routine))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}
