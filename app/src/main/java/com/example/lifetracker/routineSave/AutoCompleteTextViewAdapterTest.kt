package com.example.lifetracker.routineSave

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.lifetracker.R
import com.example.lifetracker.database.Activity
import com.example.lifetracker.database.Routine
import kotlinx.android.synthetic.main.list_activities.view.*

class AutoCompleteTextViewAdapterTest(
    context: Context,
    private val layoutResource: Int,
    private var activityList: List<Routine>
) : ArrayAdapter<Routine>(context, layoutResource, activityList) {

    val originalData: List<Routine> = activityList

    override fun getFilter(): Filter {
        return myFilter
    }

    override fun getCount(): Int {
        return activityList.size
    }

    override fun getItem(position: Int): Routine = activityList[position]


    override fun getItemId(position: Int): Long {
        return activityList[position].id
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(layoutResource, parent, false)
            holder = ViewHolder()
            holder.textView = view.findViewById(R.id.listItemActivityTextView) as TextView
            view.tag = holder
        } else {
            convertView.tag as ViewHolder
            view = convertView
        }

        val item = getItem(position)
        view.listItemActivityTextView.text = item.startTimeMilli.toString()

        return view
    }

    private class ViewHolder {
        var textView: TextView? = null
    }

    private var myFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            if (constraint != null) {
                val tempList = activityList//.filter { it.name.contains(constraint, true) }
                filterResults.values = tempList
                filterResults.count = tempList.size
            }

            return filterResults
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if (results?.count ?: -1 > 0) {
                activityList = results?.values as List<Routine>
                notifyDataSetChanged()
            } else {
                activityList = originalData
                notifyDataSetInvalidated()
            }
        }
    }


}