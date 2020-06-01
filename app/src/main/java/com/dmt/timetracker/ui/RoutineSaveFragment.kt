package com.dmt.timetracker.ui

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dmt.timetracker.App
import com.dmt.timetracker.R
import com.dmt.timetracker.adapters.ActivitiesAutoCompleteTextViewAdapter
import com.dmt.timetracker.database.Activity
import com.dmt.timetracker.database.getDatabase
import com.dmt.timetracker.databinding.FragmentRoutineSaveBinding
import com.dmt.timetracker.repository.TimeRepository
import com.dmt.timetracker.viewmodels.RoutineSaveViewModel
import com.dmt.timetracker.viewmodels.RoutineSaveViewModelFactory
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.filter.DefaultIconFilter
import com.maltaisn.icondialog.pack.IconPack
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class RoutineSaveFragment : Fragment(), IconDialog.Callback {

    private lateinit var app: App
    override val iconDialogIconPack: IconPack?
        get() = app.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        val drawable = icons[0].drawable
        logoImage.setImageDrawable(drawable)
        routineSaveViewModel.updateIcon(icons[0].id.toString())
    }

    companion object {
        private const val ICON_DIALOG_TAG = "icon-dialog"
    }

    private lateinit var logoImage: ImageView
    private lateinit var iconDialog: IconDialog
    private lateinit var routineSaveViewModel: RoutineSaveViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentRoutineSaveBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_routine_save, container, false)

        val arguments =
            RoutineSaveFragmentArgs.fromBundle(
                requireArguments()
            )

        val application = requireNotNull(this.activity).application
        app = application as App
        val database = getDatabase(application)

        val viewModelFactory =
            RoutineSaveViewModelFactory(
                TimeRepository(database)
            )

        routineSaveViewModel = ViewModelProvider(this, viewModelFactory)
            .get(RoutineSaveViewModel::class.java)

        mainActivityViewModel = ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)

        binding.routineSaveViewModel = routineSaveViewModel
        binding.lifecycleOwner = this
        logoImage = binding.logoImageView

        routineSaveViewModel.activities.observe(viewLifecycleOwner, Observer { activitiesList ->
            context?.let {
                val categoryAdapter =
                    ActivitiesAutoCompleteTextViewAdapter(
                        it,
                        R.layout.list_activities,
                        activitiesList,
                        app
                    )

                binding.nameAutoCompleteTextView.setAdapter(categoryAdapter)
            }
        })

        routineSaveViewModel.iconChanged.observe(viewLifecycleOwner, Observer {isChanged ->
            if(isChanged){
                routineSaveViewModel.doneChangingIcon()
                val id = routineSaveViewModel.currentActivity.imageName.toIntOrNull() ?: -1
                if(id == -1){
                    logoImage.setImageDrawable(null)
                } else {
                    val drawable = iconDialogIconPack?.getIcon(id)?.drawable
                    logoImage.setImageDrawable(drawable)
                }
            }
        })

        binding.nameAutoCompleteTextView.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val activity = parent.adapter.getItem(position) as Activity
                routineSaveViewModel.updateCurrentActivity(activity)
                binding.nameAutoCompleteTextView.clearFocus()
            }

        setEventListener(
            requireActivity(),
            viewLifecycleOwner,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    val text = binding.nameAutoCompleteTextView.text.toString()
                    routineSaveViewModel.updateKeyboardStatus(isOpen)
                    if(!isOpen) {
                        routineSaveViewModel.updateCurrentName(text)
                        routineSaveViewModel.evaluateActivity()
                        binding.nameAutoCompleteTextView.clearFocus()
                    }
                }
            })

        binding.nameTextInput.setEndIconOnClickListener {
            binding.nameAutoCompleteTextView.setText("")
            routineSaveViewModel.updateCurrentName("")
            routineSaveViewModel.evaluateActivity()
        }

        var isActive = false
        var isShowing = false

        binding.showListButton.setOnClickListener { view ->
            binding.nameAutoCompleteTextView.showDropDown()
            binding.showListButton.isClickable = false
        }

        binding.nameAutoCompleteTextView.setOnDismissListener {
            Handler().postDelayed({ binding.showListButton.isClickable = true }, 100)
        }

        val context = requireContext()
        val appWidgetManager = AppWidgetManager.getInstance(context)

        routineSaveViewModel.navigateToRoutineTracker.observe(
            viewLifecycleOwner,
            Observer { routine ->
                routine?.let {

//                    val widgetIds = appWidgetManager.getAppWidgetIds(
//                        ComponentName(
//                            context,
//                            MiniTracker::class.java
//                        )
//                    )
//
//                    if(widgetIds.isNotEmpty())
//                        updateAppWidget(context, appWidgetManager, widgetIds[0], false)

                    this.findNavController().navigate(
                        RoutineSaveFragmentDirections.actionRoutineSaveFragmentToRoutineTrackerFragment()
                    )
                    routineSaveViewModel.doneNavigating()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val iconFilter = DefaultIconFilter()
        iconFilter.idSearchEnabled = true

        iconDialog = childFragmentManager.findFragmentByTag(ICON_DIALOG_TAG) as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings {
                this.iconFilter = iconFilter
                //titleVisibility = IconDialog.TitleVisibility.ALWAYS
                //searchVisibility = IconDialog.SearchVisibility.ALWAYS
                //headersVisibility =IconDialog.HeadersVisibility.SHOW
                maxSelection = 1
                //showMaxSelectionMessage = true
                //showSelectBtn = true
                //showClearBtn = true
            })

        logoImage.setOnClickListener {
            iconDialog.show(childFragmentManager,
                ICON_DIALOG_TAG
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.save_routine))
    }
}
