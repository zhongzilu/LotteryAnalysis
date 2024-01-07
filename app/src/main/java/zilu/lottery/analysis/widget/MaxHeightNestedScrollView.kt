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
import androidx.core.widget.NestedScrollView
import zilu.lottery.analysis.R

/**
 *
 * Create by zilu 2023/09/17
 */
class MaxHeightNestedScrollView : NestedScrollView {

    private var maxHeight: Int = -1

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightNestedScrollView)
        maxHeight =
            a.getDimensionPixelSize(R.styleable.MaxHeightNestedScrollView_android_maxHeight, -1)
        a.recycle()
    }

    fun getMaxHeight() = maxHeight

    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = if (maxHeight > 0)
            MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        else heightMeasureSpec
        super.onMeasure(widthMeasureSpec, height)
    }
}