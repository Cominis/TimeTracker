package com.dmt.timetracker.ui

import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Chronometer.OnChronometerTickListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.dmt.timetracker.R
import com.dmt.timetracker.database.getDatabase
import com.dmt.timetracker.databinding.FragmentRoutineTrackerBinding
import com.dmt.timetracker.repository.TimeRepository
import com.dmt.timetracker.viewmodels.RoutineTrackerViewModel
import com.dmt.timetracker.viewmodels.RoutineTrackerViewModelFactory
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd


class RoutineTrackerFragment : Fragment() {

    private lateinit var routineTrackerViewModel: RoutineTrackerViewModel

    private val mInterstitialAdUnitId: String by lazy {
        "ca-app-pub-4699049340059173/4138129053"
    }

    private lateinit var mInterstitialAd: InterstitialAd

    private fun loadInterstitialAd(interstitialAdUnitId: String) {
        mInterstitialAd.adUnitId = interstitialAdUnitId
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun runAdEvents() {
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClicked() {
                super.onAdOpened()
                mInterstitialAd.adListener.onAdClosed()
            }

            override fun onAdClosed() {
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentRoutineTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_routine_tracker, container, false)

        mInterstitialAd = InterstitialAd(requireContext())
        loadInterstitialAd(mInterstitialAdUnitId)
        runAdEvents()

        setHasOptionsMenu(true)

        val application = requireActivity().application
        val database = getDatabase(application)

        val viewModelFactory =
            RoutineTrackerViewModelFactory(
                TimeRepository(database),
                requireContext()
            )
        routineTrackerViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RoutineTrackerViewModel::class.java)

        binding.routineTrackerViewModel = routineTrackerViewModel
        binding.lifecycleOwner = this

        routineTrackerViewModel.latestRoutine.observe(viewLifecycleOwner, Observer {
            val currentRoutine = if (it?.endTimeMilli != it?.startTimeMilli) { null } else { it }
            routineTrackerViewModel.updateCurrentRoutine(currentRoutine)
        })

        routineTrackerViewModel.isStart.observe(viewLifecycleOwner, Observer { value ->
            binding.chronometer.base = SystemClock.elapsedRealtime() - routineTrackerViewModel.getCurrentDuration()
            if(value) {
                binding.chronometer.stop()
            } else {
                binding.chronometer.start()
            }
        })

        routineTrackerViewModel.navigateToRoutineSave.observe(viewLifecycleOwner, Observer { routine ->
            routine?.let {

                routineTrackerViewModel.doneNavigating()
                this.findNavController().navigate(
                    RoutineTrackerFragmentDirections.actionRoutineTrackerFragmentToRoutineSaveFragment(
                        routine.id
                    )
                )

                if (mInterstitialAd.isLoaded) {
                    mInterstitialAd.show()
                }
            }
        })

        binding.chronometer.onChronometerTickListener =
            OnChronometerTickListener { cArg ->
                val time = SystemClock.elapsedRealtime() - cArg.base
                val h = (time / 3600000).toInt()
                val m = (time - h * 3600000).toInt() / 60000
                val s = (time - h * 3600000 - m * 60000).toInt() / 1000
                cArg.text = "%02d:%02d:%02d".format(h, m, s)
            }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.track_routine))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.routine_tracker, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}
