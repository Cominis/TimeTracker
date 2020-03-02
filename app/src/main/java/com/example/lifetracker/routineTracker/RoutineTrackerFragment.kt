package com.example.lifetracker.routineTracker

import android.os.Bundle
import android.view.*
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


class RoutineTrackerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentRoutineTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_routine_tracker, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val viewModelFactory = RoutineTrackerViewModelFactory(dataSource)
        val routineTrackerViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RoutineTrackerViewModel::class.java)

        binding.routineTrackerViewModel = routineTrackerViewModel
        binding.lifecycleOwner = this

        routineTrackerViewModel.isStartButton.observe(viewLifecycleOwner, Observer { value ->
            if(value == true)
                binding.chronometer.stop()
            else
                binding.chronometer.start()
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
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Prepopulation completed",
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()

                routineTrackerViewModel.doneShowingSnackbar()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
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
