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
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import zilu.lottery.analysis.R

/**
 *
 * Create by zilu 2023/09/08
 */
class ReleaseBallView2 : LinearLayout {

    /**红球数组*/
    private var redBalls: List<String> = emptyList()

    /**蓝球数组*/
    private var blueBalls: List<String> = emptyList()

    private var ballSize: Int = 0

    /**
     * 红球与蓝球之间的间距
     */
    private var ballRedBlueSpace: Int = 20

    /**
     * 球之间的间距
     */
    private var ballSpace: Int = 8

    /**
     * 球中心显示的文字大小
     */
    private var ballTextSize: Float = 16f

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
        ballSize = resources.getDimension(R.dimen.ball_icon_size).toInt()
    }

    fun setRedBalls(balls: List<String>) = apply {
        this.redBalls = balls
    }

    fun setBlueBalls(balls: List<String>) = apply {
        this.blueBalls = balls
    }

    fun setFormatBallString(format: String) = apply {
        val formatSplit = format.split("+")
        val size = formatSplit.size
        if (size == 0) throw IllegalArgumentException("balls string format error")
        this.redBalls = formatSplit[0].split(" ")
        if (size > 1) this.blueBalls = formatSplit[1].split(" ")
    }

    fun show() {
        val redLast = redBalls.lastIndex
        removeAllViews()
        redBalls.forEachIndexed { i, s ->
            val v =
                if (i == redLast) generateChildView(s, ballRedBlueSpace)
                else generateChildView(s, ballSpace)
            v.setBackgroundResource(R.drawable.red_ball_big_bg)
            addView(v)
        }

        blueBalls.forEach {
            val v = generateChildView(it, ballSpace)
            v.setBackgroundResource(R.drawable.blue_ball_big_bg)
            addView(v)
        }
    }

    private fun generateChildView(t: String, endMargin: Int) = TextView(context).apply {
        val lp = LayoutParams(ballSize, ballSize)
        lp.setMargins(0, 0, endMargin, 0)
        layoutParams = lp
        gravity = Gravity.CENTER
        textSize = ballTextSize
        setTextColor(Color.WHITE)
        text = t
    }

}