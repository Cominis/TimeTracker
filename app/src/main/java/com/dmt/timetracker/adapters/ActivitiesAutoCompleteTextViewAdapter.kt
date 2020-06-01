package com.dmt.timetracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.dmt.timetracker.App
import com.dmt.timetracker.R
import com.dmt.timetracker.database.Activity
import com.maltaisn.icondialog.pack.IconPack
import kotlinx.android.synthetic.main.list_activities.view.*

class ActivitiesAutoCompleteTextViewAdapter(
    context: Context,
    private val layoutResource: Int,
    private var activityList: List<Activity>,
    private val app: App
) : ArrayAdapter<Activity>(context, layoutResource, activityList) {

    private val iconDialogIconPack: IconPack?
        get() = app.iconPack

    val originalData: List<Activity> = activityList

    override fun getFilter(): Filter {
        return myFilter
    }

    override fun getCount(): Int {
        return activityList.size
    }

    override fun getItem(position: Int): Activity = activityList[position]


    override fun getItemId(position: Int): Long {
        return activityList[position].id
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(layoutResource, parent, false)
            holder =
                ViewHolder()
            holder.imageView = view.findViewById(R.id.listItemActivityImageView) as ImageView
            holder.textView = view.findViewById(R.id.listItemActivityTextView) as TextView
            view.tag = holder
        } else {
            convertView.tag as ViewHolder
            view = convertView
        }

        val activity = getItem(position)
        view.listItemActivityTextView.text = activity.name
        val id = activity.imageName.toIntOrNull()
        val drawable = if(id != null) iconDialogIconPack?.getIcon(id)?.drawable else null
        view.listItemActivityImageView.setImageDrawable(drawable)

        return view
    }

    private class ViewHolder {
        var textView: TextView? = null
        var imageView: ImageView? = null
    }

    private var myFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            if (constraint != null) {
                val tempList = activityList.filter { it.name.contains(constraint, true) }
                filterResults.values = tempList
                filterResults.count = tempList.size
            }

            return filterResults
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {
            if (results?.count ?: -1 > 0) {
                activityList = results?.values as List<Activity>
                notifyDataSetChanged()
            } else {
                activityList = originalData
                notifyDataSetInvalidated()
            }
        }
    }


}