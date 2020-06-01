package com.dmt.timetracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.*
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dmt.timetracker.App
import com.dmt.timetracker.R
import com.dmt.timetracker.databinding.FragmentStatisticsPieChartBinding
import com.dmt.timetracker.utils.getColorFromAttr
import com.dmt.timetracker.viewmodels.MainActivityViewModel
import com.dmt.timetracker.utils.millisToString
import com.dmt.timetracker.adapters.PieChartValueFormatter
import com.dmt.timetracker.database.getDatabase
import com.dmt.timetracker.domain.DataWithDuration
import com.dmt.timetracker.repository.TimeRepository
import com.dmt.timetracker.viewmodels.PredefinedStatisticsViewModel
import com.dmt.timetracker.viewmodels.PredefinedStatisticsViewModelFactory
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.snackbar.Snackbar
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.maltaisn.icondialog.pack.IconPack


class PredefinedStatisticsFragment  : Fragment(), SeekBar.OnSeekBarChangeListener,
    OnChartValueSelectedListener {
    private lateinit var app: App
    private val iconDialogIconPack: IconPack?
        get() = app.iconPack

    private lateinit var iconDrawableLoader: IconDrawableLoader
    private lateinit var chart : PieChart
    private lateinit var seekBarX : SeekBar
    private lateinit var tvX: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var tfRegular: Typeface
    private lateinit var tfLight: Typeface
    @ColorInt private var onBackgroundColor: Int = 0

    companion object Constants {
        private const val PERMISSION_STORAGE = 0
    }

    private lateinit var statisticsViewModel : PredefinedStatisticsViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatisticsPieChartBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_statistics_pie_chart, container, false)

        setHasOptionsMenu(true)

        val arguments =
            PredefinedStatisticsFragmentArgs.fromBundle(
                requireArguments()
            )

        val application = requireNotNull(this.activity).application
        app = application as App
        val database = getDatabase(application)

        val viewModelFactory =
            PredefinedStatisticsViewModelFactory(
                TimeRepository(database),
                arguments.timestampFrom,
                arguments.timestampTo
            )

        statisticsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(PredefinedStatisticsViewModel::class.java)

        mainActivityViewModel = ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)

        iconDrawableLoader = IconDrawableLoader(requireContext())

        binding.progressBar.visibility = View.VISIBLE
        binding.pieChartConstraint.visibility = View.INVISIBLE
        statisticsViewModel.data.observe(viewLifecycleOwner, Observer { statisticsData ->
            binding.progressBar.visibility = View.INVISIBLE
            if(statisticsData.isNotEmpty()){
                binding.seekBar1.max = statisticsData.size
                binding.seekBar1.progress = statisticsData.size
                binding.noTextView.visibility = View.INVISIBLE
                binding.pieChartConstraint.visibility = View.VISIBLE
            } else {
                binding.pieChartConstraint.visibility = View.INVISIBLE
                binding.noTextView.visibility = View.VISIBLE
            }
        })

        tfRegular = resources.getFont(R.font.open_sans_regular)
        tfLight = resources.getFont(R.font.open_sans_light)

        tvX = binding.tvXMax
        seekBarX = binding.seekBar1
        progressBar = binding.progressBar

        seekBarX.setOnSeekBarChangeListener(this)

        chart = binding.chart1
        setupChart()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val arguments =
            PredefinedStatisticsFragmentArgs.fromBundle(
                requireArguments()
            )

        val activityViewModel by activityViewModels<MainActivityViewModel>()

        activityViewModel.updateTheme.observe(viewLifecycleOwner, Observer {
            onBackgroundColor = requireContext().getColorFromAttr(R.attr.colorOnBackground)

            if(it){
                changeChartColors(arguments.timestampFrom, arguments.timestampTo, Color.WHITE, Color.BLACK)
            } else {
                changeChartColors(arguments.timestampFrom, arguments.timestampTo, Color.BLACK, Color.WHITE)
            }
        })
    }

    private fun setupChart(){
        chart.minAngleForSlices = 16f

        chart.setNoDataText(resources.getString(R.string.noDataText))
        chart.setNoDataTextColor(Color.RED)

        chart.setUsePercentValues(false)
        chart.description.isEnabled = false
        chart.setExtraOffsets(7F, 10F, 7F, 7F)

        chart.dragDecelerationFrictionCoef = 0.95f

        chart.setCenterTextTypeface(tfLight)

        chart.isDrawHoleEnabled = true
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 58f //todo
        chart.transparentCircleRadius = 61f

        chart.setDrawCenterText(true)

        //chart.rotationAngle = 0F

        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this)

        //chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        val l : Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.isWordWrapEnabled = true  //todo heavy on performance
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 10f
        l.yOffset = 20f

        l.formSize = 10f // set the size of the legend forms/shapes
        l.form = Legend.LegendForm.CIRCLE // set what type of form/shape should be used
        // entry label styling
        chart.setEntryLabelColor(Color.BLACK)
        chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)
    }

    private fun changeChartColors(timestampFrom: Long, timestampTo: Long, textColor: Int, backgroundColor: Int){
        chart.centerText = generateCenterSpannableText(timestampFrom, timestampTo, textColor)
        chart.setHoleColor(backgroundColor)
        chart.setTransparentCircleColor(backgroundColor) //todo
        chart.legend.textColor = textColor
    }

    private fun setData(count: Int) {
        val entries: ArrayList<PieEntry> = ArrayList()

        val dataList = statisticsViewModel.data.value ?: listOf(DataWithDuration())
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of the chart.
        for (i in 0 until count) {
            val index = i % dataList.size
            val iconId = dataList[index].imageName.toIntOrNull()
            val iconDrawable = if(iconId != null) iconDialogIconPack?.getIconDrawable(iconId, iconDrawableLoader) else null
            iconDrawable?.setTint(onBackgroundColor)
            entries.add(
                PieEntry(
                    (dataList[index].duration / 1000L).toFloat(),
                    dataList[index].name,
                    iconDrawable
                )
            )
        }

        val dataSet = PieDataSet(entries,"Results") //todo label doesn't change color
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 2f
        dataSet.iconsOffset = MPPointF(0f, 45f)
        dataSet.selectionShift = 12f
        dataSet.valueTextColor = Color.BLACK

        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
        colors.add(ColorTemplate.getHoloBlue())
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)

        data.setValueFormatter(PieChartValueFormatter())

        data.setValueTextSize(11f)
        data.setValueTypeface(tfLight)

        chart.data = data
        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chart_update, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionToggleValues -> {
                for (set in chart.data.dataSets) set.setDrawValues(!set.isDrawValuesEnabled)
                item.isChecked = if(chart.data.dataSets.size > 0) chart.data.dataSets[0].isDrawValuesEnabled else false
                chart.invalidate()
            }
            R.id.actionToggleXValues -> {
                chart.setDrawEntryLabels(!chart.isDrawEntryLabelsEnabled)
                item.isChecked = chart.isDrawEntryLabelsEnabled
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                for (set in chart.data.dataSets) set.setDrawIcons(!set.isDrawIconsEnabled)
                item.isChecked = if(chart.data.dataSets.size > 0) chart.data.dataSets[0].isDrawIconsEnabled else false
                chart.invalidate()
            }
            R.id.actionToggleCurvedSlices -> {
                val toSet = !chart.isDrawRoundedSlicesEnabled || !chart.isDrawHoleEnabled
                chart.setDrawRoundedSlices(toSet)
                if (toSet && !chart.isDrawHoleEnabled) {
                    chart.isDrawHoleEnabled = true
                }
                if (toSet && chart.isDrawSlicesUnderHoleEnabled) {
                    chart.setDrawSlicesUnderHole(false)
                }
                item.isChecked = toSet
                chart.invalidate()
            }
            R.id.actionSave -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    saveToGallery()
                } else {
                    requestStoragePermission(chart)
                }
            }
            R.id.actionToggleMinAngles -> {
                if (chart.minAngleForSlices == 0f) {
                    chart.minAngleForSlices = 16f
                    item.isChecked = true
                } else {
                    chart.minAngleForSlices = 0f
                    item.isChecked = false
                }
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
        }
        return true
    }

    private fun requestStoragePermission(view: View?) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Snackbar.make(
                    requireView(),
                    "Write permission is required to save image to gallery",
                    Snackbar.LENGTH_INDEFINITE
                )
                .setAction(android.R.string.ok) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_STORAGE
                    )
                }.show()
        } else {
            Toast.makeText(requireContext(), "Permission Required!", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveToGallery()
            } else {
                Toast.makeText(
                        requireContext(),
                        "Saving FAILED!",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }
    }

    private fun saveToGallery(chart: Chart<*>, name: String) {
        if (chart.saveToGallery(name + "_" + System.currentTimeMillis(), 70))
            Toast.makeText(requireContext(), "Saving SUCCESSFUL!", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(requireContext(), "Saving FAILED!", Toast.LENGTH_SHORT).show()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        tvX.text = seekBarX.progress.toString()
        setData(seekBarX.progress)
    }

    private fun saveToGallery() {
        saveToGallery(chart, "PieChartActivity")
    }

    private fun generateCenterSpannableText(timestampFrom : Long, timestampUntil : Long, textColor: Int): SpannableString? {
        val s = SpannableString("Pie Chart\nFrom: ${millisToString(
            timestampFrom,
            "yyyy-MM-dd"
        )}\n  To: ${millisToString(
            timestampUntil,
            "yyyy-MM-dd"
        )}")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 9, 0)

        s.setSpan(ForegroundColorSpan(textColor), 0, 9, 0)


        s.setSpan(StyleSpan(Typeface.NORMAL), 9, s.length, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 9, s.length, 0)
        s.setSpan(RelativeSizeSpan(.8f), 9, s.length, 0)
        return s
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null) return
        if (h != null) { return } //todo show info
    }

    override fun onNothingSelected() {}

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}

