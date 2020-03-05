package com.example.lifetracker.settings.datePickerPreference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.example.lifetracker.R
import com.example.lifetracker.convertLongToDateOnlyString
import com.example.lifetracker.convertLongToDateString

class DatePreference(context: Context?, attrs: AttributeSet? = null) : DialogPreference(context, attrs) {

    //constructor(context: Context?) : this(context, null)
    //constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    //constructor(context: Context?, attrs: AttributeSet?, defStyleAttr : Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    private var mTime = System.currentTimeMillis()
    private val mDialogLayoutResId = R.layout.pref_dialog_date

    // Save to Shared Preferences
    var time: Long
        get() = mTime
        set(time) {
            mTime = time // Save to Shared Preferences
            persistLong(time)
        }

    override fun onGetDefaultValue(a: TypedArray, index: Int) : Long? {
        return System.currentTimeMillis()
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        time = getPersistedLong(mTime)
        summary = convertLongToDateOnlyString(time)
    }

    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }
}