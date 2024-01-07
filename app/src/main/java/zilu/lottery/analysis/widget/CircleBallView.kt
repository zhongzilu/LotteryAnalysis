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
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import zilu.lottery.analysis.R

/**
 *
 * Create by zilu 2023/12/21
 */
class CircleBallView : TextView, Checkable {

    private var mChecked: Boolean = false
    private var selectedDrawable: Drawable? = null
    private var selectedTextColor: Int = textColors.defaultColor
    private var mBackgroundDrawable: Drawable? = null
    private var mDefaultTextColor: Int = textColors.defaultColor

    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr, 0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleBallView)
        mChecked = a.getBoolean(R.styleable.CircleBallView_android_checked, false)
        selectedDrawable = a.getDrawable(R.styleable.CircleBallView_selectedDrawable)
        selectedTextColor =
            a.getColor(R.styleable.CircleBallView_selectedTextColor, textColors.defaultColor)
        mBackgroundDrawable = a.getDrawable(R.styleable.CircleBallView_android_background)
        mDefaultTextColor =
            a.getColor(R.styleable.CircleBallView_android_textColor, textColors.defaultColor)
        isChecked = mChecked
        a.recycle()
    }

    fun setSelectedDrawable(drawable: Drawable?) {
        selectedDrawable = drawable
    }

    fun setSelectedDrawableResource(resid: Int) {
        selectedDrawable = ResourcesCompat.getDrawable(resources, resid, context.theme)
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        mDefaultTextColor = color
    }

    override fun setBackground(drawable: Drawable?) {
        super.setBackground(drawable)
        mBackgroundDrawable = drawable
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        mBackgroundDrawable = ResourcesCompat.getDrawable(resources, resid, context.theme)
    }

    override fun setChecked(checked: Boolean) {
        if (checked != mChecked) {
            super.setBackground(if (checked) selectedDrawable else mBackgroundDrawable)
            super.setTextColor(if (checked) selectedTextColor else mDefaultTextColor)
            mChecked = checked
        }
    }

    override fun isChecked(): Boolean = mChecked

    override fun toggle() {
        isChecked = !mChecked
    }
}