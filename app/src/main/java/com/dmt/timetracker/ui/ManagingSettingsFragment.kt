package com.dmt.timetracker.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.dmt.timetracker.R
import com.dmt.timetracker.database.getDatabase
import com.dmt.timetracker.repository.TimeRepository
import com.dmt.timetracker.viewmodels.ManagingSettingsViewModel
import com.dmt.timetracker.viewmodels.ManagingSettingsViewModelFactory
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class ManagingSettingsFragment : PreferenceFragmentCompat() {

    private lateinit var activityViewModel : MainActivityViewModel
    private lateinit var managingSettingsViewModel : ManagingSettingsViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        managingSettingsViewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    "Deleted",
                    Snackbar.LENGTH_SHORT
                ).show()

                managingSettingsViewModel.doneShowingSnackBar()
            }
        })
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.managing_preferences, rootKey)

        val application = requireActivity().application
        val database = getDatabase(application)
        val viewModelFactory = ManagingSettingsViewModelFactory(TimeRepository(database))

        managingSettingsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(ManagingSettingsViewModel::class.java)

        val context = requireContext()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        findPreference<Preference>(resources.getString(R.string.delete_current_key))?.setOnPreferenceClickListener {
            managingSettingsViewModel.deleteCurrentRoutine()
//            val widgetIds = appWidgetManager.getAppWidgetIds(
//                ComponentName(context, MiniTracker::class.java)
//            )
//
//            if(widgetIds.isNotEmpty())
//                updateAppWidget(context, appWidgetManager, widgetIds[0], false)

            true
        }

        findPreference<Preference>(resources.getString(R.string.delete_all_key))?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Erase everything")
                .setMessage("Do you really want to delete everything?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes) { dialog, whichButton ->
                    managingSettingsViewModel.deleteAll()
//                    val widgetIds = appWidgetManager.getAppWidgetIds(
//                        ComponentName(context, MiniTracker::class.java)
//                    )
//
//                    if(widgetIds.isNotEmpty())
//                        updateAppWidget(context, appWidgetManager, widgetIds[0], false)
                }
                .setNegativeButton(android.R.string.no, null).show()
            true
        }

        activityViewModel = ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)

        findPreference<SwitchPreferenceCompat>(resources.getString(R.string.dark_theme_key))?.setOnPreferenceClickListener {
                preference ->
            val value = (preference as SwitchPreferenceCompat).isChecked
            activityViewModel.changeToDarkTheme(value)
            true
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activityViewModel.updateActionBarTitle(resources.getString(R.string.app_settings))
    }
}
