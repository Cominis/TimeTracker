package com.example.lifetracker.routineSave

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lifetracker.R
import com.example.lifetracker.database.Activity
import com.example.lifetracker.database.RoutineDatabase
import com.example.lifetracker.databinding.FragmentRoutineSaveBinding
import com.example.lifetracker.mainActivity.MainActivityViewModel
import com.example.lifetracker.routineTracker.RoutineTrackerFragmentDirections
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class RoutineSaveFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentRoutineSaveBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_routine_save, container, false)

        val arguments = RoutineSaveFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val viewModelFactory = RoutineSaveViewModelFactory(arguments.RoutineId, dataSource)
        val routineSaveViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RoutineSaveViewModel::class.java)

        binding.routineSaveViewModel = routineSaveViewModel
        binding.lifecycleOwner = this

        routineSaveViewModel.activities.observe(viewLifecycleOwner, Observer { activitiesList ->
            context?.let {
                val categoryAdapter =
                    AutoCompleteTextViewAdapter(it, R.layout.list_activities, activitiesList)

                binding.nameAutoCompleteTextView.setAdapter(categoryAdapter)
            }
        })

        binding.nameAutoCompleteTextView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                routineSaveViewModel.updateCurrentActivityId(id)
                routineSaveViewModel.updateIsNewActivityValue()
            }

        setEventListener(
            requireActivity(),
            viewLifecycleOwner,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if(!isOpen) {
                        binding.nameAutoCompleteTextView.clearFocus()
                        routineSaveViewModel.
                            evaluateActivity(binding.nameAutoCompleteTextView.text.toString())
                    }
                }
            })

        binding.showListButton.setOnClickListener { view ->
            binding.nameAutoCompleteTextView.showDropDown()
        }

        routineSaveViewModel.navigateToRoutineTracker.observe(
            viewLifecycleOwner,
            Observer { routine ->
                routine?.let {
                    this.findNavController().navigate(
                        RoutineSaveFragmentDirections
                            .actionRoutineSaveFragmentToRoutineTrackerFragment())
                    routineSaveViewModel.doneNavigating()
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.save_routine))
    }
}
