package com.example.lifetracker.statistics

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.example.lifetracker.R
import com.example.lifetracker.database.RoutineDatabase
import com.example.lifetracker.databinding.FragmentStatisticsBinding
import com.example.lifetracker.mainActivity.MainActivityViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList


class StatisticsFragment : Fragment(), SeekBar.OnSeekBarChangeListener,
OnChartValueSelectedListener {

    private lateinit var chart : PieChart
    private lateinit var seekBarX : SeekBar
    private lateinit var seekBarY : SeekBar
    private lateinit var tvX: TextView
    private lateinit var tvY: TextView

    private lateinit var tfRegular: Typeface
    private lateinit var tfLight: Typeface

    private val parties = arrayOf("Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
        "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
        "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
        "Party Y", "Party Z")

    companion object Constants {
        private const val PERMISSION_STORAGE = 0
    }

    private lateinit var activityViewModel : MainActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentStatisticsBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_statistics, container, false)

        setHasOptionsMenu(true)
        //val arguments = StatisticsFragmentArgs.fromBundle(requireArguments())

        val application = requireNotNull(this.activity).application
        val dataSource = RoutineDatabase.getInstance(application).routineDatabaseDao

        val sharedPref = getDefaultSharedPreferences(context)

        val viewModelFactory = StatisticsViewModelFactory(dataSource, sharedPref)
        val statisticsViewModel = ViewModelProvider(this, viewModelFactory)
            .get(StatisticsViewModel::class.java)


        activityViewModel = ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)

        activityViewModel.updateActionBarTitle(resources.getString(R.string.custom_statistics))
        activityViewModel.toUpdateStatistics(true)
        activityViewModel.updateStatistics.observe(viewLifecycleOwner, Observer { toUpdate ->
            if(toUpdate) {
                val timeFrom = sharedPref.getInt(getString(R.string.time_picker_from_key), 42)  //todo
                val timeTo = sharedPref.getInt(getString(R.string.time_picker_to_key), 42)
                val dateFrom = sharedPref.getLong(getString(R.string.date_picker_from_key), 42)
                val dateTo = sharedPref.getLong(getString(R.string.date_picker_to_key), 42)

                val weekdays = sharedPref.getStringSet(getString(R.string.weekdays_key), null)?.toList()
                    ?: listOf("1", "2", "3", "4", "5", "6", "7")

                statisticsViewModel.updateDatabase(dateFrom, dateTo, timeFrom, timeTo, weekdays)
                activityViewModel.toUpdateStatistics(false)
            }
        })

        statisticsViewModel.data.observe(viewLifecycleOwner, Observer { statisticsData ->
            Log.v("DAUMANTAS","""pasieleido metodas""")
            if(statisticsData.isNotEmpty()){
                statisticsData.forEachIndexed { index, s ->
                    Log.v("DAUMANTAS","""gauti values: $index : ${s.name} : ${s.startTimeMilli}""")
                }
            }
        })


        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_piechart);

        //setTitle("PieChartActivity");

        //tfRegular = Typeface.createFromAsset(requireActivity().assets, "open_sans_regular.ttf")
        //tfLight = Typeface.createFromAsset(requireActivity().assets, "open_sans_light.ttf")
        tfRegular = resources.getFont(R.font.open_sans_regular)
        tfLight = resources.getFont(R.font.open_sans_light)

        //tvX = findViewById(R.id.tvXMax);
        //tvY = findViewById(R.id.tvYMax);
        tvX = binding.tvXMax
        tvY = binding.tvYMax

        //seekBarX = findViewById(R.id.seekBar1);
        //seekBarY = findViewById(R.id.seekBar2);
        seekBarX = binding.seekBar1
        seekBarY = binding.seekBar2
        seekBarX.setOnSeekBarChangeListener(this)
        seekBarY.setOnSeekBarChangeListener(this)

        //chart = findViewById(R.id.chart1);
        chart = binding.chart1
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5F, 10F, 5F, 5F)

        chart.dragDecelerationFrictionCoef = 0.95f

        chart.setCenterTextTypeface(tfLight)
        chart.centerText = generateCenterSpannableText()

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)

        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)

        chart.holeRadius = 58f
        chart.transparentCircleRadius = 61f

        chart.setDrawCenterText(true)

        chart.rotationAngle = 0F
        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this)

        seekBarX.progress = 4
        seekBarY.progress = 10

        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        val l : Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        chart.setEntryLabelTypeface(tfRegular)
        chart.setEntryLabelTextSize(12f)


        return binding.root
    }

    private fun setData(count: Int, range: Float) {
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of the chart.
        for (i in 0 until count) {
            entries.add(
                PieEntry(
                    (Math.random() * range + range / 5).toFloat(),
                    parties[i % parties.size],
                    resources.getDrawable(R.drawable.star, null)
                )
            )
        }
        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

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
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(tfLight)

        chart.data = data
        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pie, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewGithub -> {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://github.com/PhilJay/MPAndroidChart")
                startActivity(i)
            }
            R.id.actionToggleValues -> {
                for (set in chart.data.dataSets) set.setDrawValues(!set.isDrawValuesEnabled)
                item.isChecked = if(chart.data.dataSets.size > 0) chart.data.dataSets[0].isDrawValuesEnabled else false
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                for (set in chart.data.dataSets) set.setDrawIcons(!set.isDrawIconsEnabled)
                item.isChecked = if(chart.data.dataSets.size > 0) chart.data.dataSets[0].isDrawIconsEnabled else false
                chart.invalidate()
            }
            R.id.actionToggleHole -> {
                chart.isDrawHoleEnabled = !chart.isDrawHoleEnabled
                item.isChecked = chart.isDrawHoleEnabled
                chart.invalidate()
            }
            R.id.actionToggleMinAngles -> {
                if (chart.minAngleForSlices == 0f) {
                    chart.minAngleForSlices = 36f
                    item.isChecked = true
                } else {
                    chart.minAngleForSlices = 0f
                    item.isChecked = false
                }
                chart.notifyDataSetChanged()
                chart.invalidate()
            }
            R.id.actionToggleCurvedSlices -> {
                val toSet = !chart.isDrawRoundedSlicesEnabled || !chart.isDrawHoleEnabled
                chart.setDrawRoundedSlices(toSet)
                if (toSet && !chart.isDrawHoleEnabled) {
                    chart.isDrawHoleEnabled = true
                    //todo
                }
                if (toSet && chart.isDrawSlicesUnderHoleEnabled) {
                    chart.setDrawSlicesUnderHole(false)
                }
                item.isChecked = toSet
                chart.invalidate()
            }
            R.id.actionDrawCenter -> {
                if (chart.isDrawCenterTextEnabled) {
                    chart.setDrawCenterText(false)
                    item.isChecked = false
                } else {
                    chart.setDrawCenterText(true)
                    item.isChecked = true
                }
                chart.invalidate()
            }
            R.id.actionToggleXValues -> {
                chart.setDrawEntryLabels(!chart.isDrawEntryLabelsEnabled)
                item.isChecked = chart.isDrawEntryLabelsEnabled
                chart.invalidate()
            }
            R.id.actionTogglePercent -> {
                chart.setUsePercentValues(!chart.isUsePercentValuesEnabled)
                item.isChecked = chart.isUsePercentValuesEnabled
                chart.invalidate()
            }
            R.id.animateX -> {
                chart.animateX(1400)
            }
            R.id.animateY -> {
                chart.animateY(1400)
            }
            R.id.animateXY -> {
                chart.animateXY(1400, 1400)
            }
            R.id.actionToggleSpin -> {
                chart.spin(
                    1000,
                    chart.rotationAngle,
                    chart.rotationAngle + 360,
                    Easing.EaseInOutCubic
                )
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
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(MainActivityViewModel::class.java)
            .updateActionBarTitle(resources.getString(R.string.custom_statistics))
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

    override fun onProgressChanged(
        seekBar: SeekBar?,
        progress: Int,
        fromUser: Boolean
    ) {
        tvX.text = seekBarX.progress.toString()
        tvY.text = seekBarY.progress.toString()
        setData(seekBarX.progress, seekBarY.progress.toFloat())
    }

    private fun saveToGallery() {
        saveToGallery(chart, "PieChartActivity")
    }

    private fun generateCenterSpannableText(): SpannableString? {
        val s = SpannableString("Pie Chart\nFrom: 2020-02-02 20:20:02\nTo: 2020-02-02 20:20:02")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 9, 0)

        s.setSpan(StyleSpan(Typeface.NORMAL), 9, s.length, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 9, s.length, 0)
        s.setSpan(RelativeSizeSpan(.8f), 9, s.length, 0)

        //s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
        //s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 14, s.length, 0)
        return s
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e == null) return
        if (h != null) {
            Log.i(
                "VAL SELECTED",
                "Value: " + e.y.toString() + ", index: " + h.x
                    .toString() + ", DataSet index: " + h.dataSetIndex
            )
        }
    }

    override fun onNothingSelected() {
        Log.i("PieChart", "nothing selected")
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}
