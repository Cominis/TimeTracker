package com.example.lifetracker.settings.timePickerPreference

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.example.lifetracker.R

class TimePreference(context: Context?, attrs: AttributeSet? = null) : DialogPreference(context, attrs) {

    //constructor(context: Context?) : this(context, null)
    //constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    //constructor(context: Context?, attrs: AttributeSet?, defStyleAttr : Int) : this(context, attrs, defStyleAttr, defStyleAttr)

    private var mTime = 0
    private val mDialogLayoutResId = R.layout.pref_dialog_time

    // Save to Shared Preferences
    var time: Int
        get() = mTime
        set(time) {
            mTime = time // Save to Shared Preferences
            persistInt(time)
        }

    override fun onGetDefaultValue(a: TypedArray, index: Int) : Any {
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        time = getPersistedInt(mTime)
        summary = " %02d:%02d".format(time / 60, time % 60)
    }

    override fun getDialogLayoutResource(): Int {
        return mDialogLayoutResId
    }
}