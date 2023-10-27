/*
 *
 *        ___                       ___       ___
 *       /\  \          ___        /\__\     /\__\
 *       \:\  \        /\  \      /:/  /    /:/  /
 *        \:\  \       \:\  \    /:/  /    /:/  /
 *         \:\  \      /::\__\  /:/  /    /:/  /  ___
 *   _______\:\__\  __/:/\/__/ /:/__/    /:/__/  /\__\
 *   \::::::::/__/ /\/:/  /    \:\  \    \:\  \ /:/  /
 *    \:\~~\~~     \::/__/      \:\  \    \:\  /:/  /
 *     \:\  \       \:\__\       \:\  \    \:\/:/  /
 *      \:\__\       \/__/        \:\__\    \::/  /
 *       \/__/                     \/__/     \/__/
 *
 *  Copyright (C) 2019 ZiLu https://github.com/zhongzilu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package zilu.lottery.analysis.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import zilu.lottery.analysis.R
import java.util.*

/**
 *
 * Create by zilu 2023/09/01
 */
class MyFilterSpinner : LinearLayout {

    private val issuesSpinnerLabel: TextView
    private val yearsSpinnerLabel: TextView
    private val issuesSpinner: Spinner
    private val yearsSpinner: Spinner

    constructor(context: Context?) : this(context, null, 0, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        super.setOrientation(HORIZONTAL)
        val view = LayoutInflater.from(context).inflate(R.layout.widget_filter_spinner, this, true)
        issuesSpinnerLabel = view.findViewById(R.id.issuesSpinnerLabel)
        yearsSpinnerLabel = view.findViewById(R.id.yearsSpinnerLabel)
        issuesSpinner = view.findViewById(R.id.issuesSpinner)
        yearsSpinner = view.findViewById(R.id.yearsSpinner)

        issuesSpinner.onItemSelectedListener = _itemSelectedListener
        yearsSpinner.onItemSelectedListener = _itemSelectedListener

        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val currentYear = calendar.get(Calendar.YEAR)
        val array = (2007..currentYear).toList()
        yearsSpinner.adapter =
            ArrayAdapter(view.context, R.layout.support_simple_spinner_dropdown_item, array)
        yearsSpinner.setSelection(array.lastIndex)
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        issuesSpinner.onItemSelectedListener = _itemSelectedListener
//        yearsSpinner.onItemSelectedListener = _itemSelectedListener
//    }

//    fun setOnIssuesItemSelectedListener(listener: AdapterView.OnItemSelectedListener): MyFilterSpinner {
//        issuesSpinner.onItemSelectedListener = listener
//        return this
//    }
//
//    fun setOnYearsItemSelectedListener(listener: AdapterView.OnItemSelectedListener): MyFilterSpinner {
//        yearsSpinner.onItemSelectedListener = listener
//        return this
//    }

    private var onItemSelectedListener: OnItemSelectedListener? = null
    fun setOnItemSelectedListener(listener: OnItemSelectedListener): MyFilterSpinner {
        this.onItemSelectedListener = listener
        return this
    }

    private var _itemSelectedListener = object : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            when (parent.id) {
                R.id.issuesSpinner -> onItemSelectedListener?.onIssuesItemSelected(parent, position)
                R.id.yearsSpinner -> onItemSelectedListener?.onYearsItemSelected(parent, position)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
        }
    }

    override fun setOrientation(orientation: Int) {
        super.setOrientation(HORIZONTAL)
    }

    interface OnItemSelectedListener {

        fun onIssuesItemSelected(parent: AdapterView<*>, position: Int)

        fun onYearsItemSelected(parent: AdapterView<*>, position: Int)
    }
}