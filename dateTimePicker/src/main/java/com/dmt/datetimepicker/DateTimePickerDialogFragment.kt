package com.dmt.datetimepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.dmt.datetimepicker.databinding.DateTimePickerBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class DateTimePickerDialogFragment(private val listener: DateTimePickerListener) : DialogFragment() {

    companion object {
        fun newInstance(listener: DateTimePickerListener, startDate: LocalDate, startTime: LocalTime): DateTimePickerDialogFragment {
            val fragment =
                DateTimePickerDialogFragment(listener)
            val bundle = Bundle()
            bundle.putSerializable("startDate", startDate)
            bundle.putSerializable("startTime", startTime)
            fragment.arguments = bundle
            return fragment
        }

        const val TAG = "tagDateTimePickerDialogFragment"
    }

    private fun getTabName(position: Int) : String {
        return when(position){
            0 -> getString(R.string.dtp_date)
            1 -> getString(R.string.dtp_time)
            else -> throw IllegalArgumentException("Unknown position $position")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DateTimePickerBinding = DataBindingUtil.inflate(inflater, R.layout.date_time_picker, container, false)

        val args = arguments
        val startDate = args!!.getSerializable("startDate") as LocalDate
        val startTime = args.getSerializable("startTime") as LocalTime

        val viewPagerAdapter = DateTimePickerViewPagerAdapter(startDate, startTime)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getTabName(position)
        }.attach()

        binding.okButton.setOnClickListener {
            val localDateTime = viewPagerAdapter.getCurrentDateTime()
            listener.onApproveDateTime(localDateTime)
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            listener.onCancelDateTime()
            dismiss()
        }
        return binding.root
    }

}

private class DateTimePickerViewPagerAdapter(
    private val startDate: LocalDate,
    private val startTime: LocalTime)
    : RecyclerView.Adapter<DateTimePickerViewPagerAdapter.ViewHolder>() {

    private val datePickerTag = 0
    private val timePickerTag = 1

    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker

    fun getCurrentDateTime() : LocalDateTime =
        LocalDateTime.of(
            datePicker.year,
            datePicker.month + 1,
            datePicker.dayOfMonth,
            timePicker.hour,
            timePicker.minute
        )

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            datePickerTag -> {
                val view = layoutInflater.inflate(R.layout.date_picker, parent, false)
                datePicker = view.findViewById(R.id.date_picker)
                ViewHolder(
                    view
                )
            }
            timePickerTag -> {
                val view = layoutInflater.inflate(R.layout.time_picker, parent, false)
                timePicker = view.findViewById(R.id.time_picker)
                ViewHolder(
                    view
                )
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    // binds the data  in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            datePickerTag ->
                datePicker.updateDate(startDate.year, startDate.monthValue - 1, startDate.dayOfMonth)
            timePickerTag -> {
                timePicker.hour = startTime.hour
                timePicker.minute = startTime.minute
            }
            else -> throw IllegalArgumentException("Unknown position $position")
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return 2
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> datePickerTag
            1 -> timePickerTag
            else -> throw IllegalArgumentException("Unknown position $position")
        }
    }
}