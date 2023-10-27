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
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.IntDef
import zilu.lottery.analysis.R

/**
 *
 * Create by zilu 2023/09/08
 */
class ReleaseBallView : View {

    /**红球数组*/
    private var redBalls: List<String> = emptyList()

    /**蓝球数组*/
    private var blueBalls: List<String> = emptyList()

    /**
     * 红球背景，可以是drawable资源,也可以是颜色值
     */
    private var ballRedBackground: Drawable? = null

    /**
     * 蓝球背景，可以是drawable资源,也可以是颜色值
     */
    private var ballBlueBackground: Drawable? = null

    /**
     * 球尺寸大小，默认32
     */
    private var ballSize: Float = 32f

    /**
     * 球中心显示的文字内容
     */
    private var ballText: CharSequence = ""

    /**
     * 球中心显示的文字颜色
     */
    private var ballTextColor: Int = Color.BLACK

    /**
     * 球中心显示的文字大小
     */
    private var ballTextSize: Float = 14f

    /**
     * 球之间的间距
     */
    private var ballSpace: Float = 0f

    /**
     * 球重力方向
     */
    @GravityFlags
    private var ballGravity: Int = Gravity.START

    /**
     * 限制红球个数，-1表示不限制
     */
    private var ballRedLimit: Int = -1

    /**
     * 限制蓝球个数，-1表示不限制
     */
    private var ballBlueLimit: Int = -1

    /**
     * 红球与蓝球之间的间距
     */
    private var ballRedBlueSpace: Float = 18f

    /**
     * 红球画笔
     */
    private var redBallPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 蓝球画笔
     */
    private var blueBallPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mWidth: Int = 0
    private var mHeight: Int = 0

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
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.ReleaseBallView,
            defStyleAttr,
            defStyleRes
        )

        ballRedBackground = a.getDrawable(R.styleable.ReleaseBallView_ballRedBackground)
        ballBlueBackground = a.getDrawable(R.styleable.ReleaseBallView_ballBlueBackground)
        ballSize = a.getDimension(R.styleable.ReleaseBallView_ballSize, 32f)
        ballText = a.getText(R.styleable.ReleaseBallView_ballText)
        ballTextColor = a.getColor(R.styleable.ReleaseBallView_ballTextColor, Color.BLACK)
        ballTextSize = a.getDimension(R.styleable.ReleaseBallView_ballTextSize, 14f)
        ballSpace = a.getDimension(R.styleable.ReleaseBallView_ballSpace, 0f)
        ballGravity = a.getInt(R.styleable.ReleaseBallView_ballGravity, Gravity.START)
        ballRedLimit = a.getInt(R.styleable.ReleaseBallView_ballRedLimit, -1)
        ballBlueLimit = a.getInt(R.styleable.ReleaseBallView_ballBlueLimit, -1)
        ballRedBlueSpace = a.getDimension(R.styleable.ReleaseBallView_ballRedBlueSpace, 10f)
        a.recycle()

        redBallPaint.color = ballTextColor
        redBallPaint.textSize = ballTextSize

        blueBallPaint.color = ballTextColor
        blueBallPaint.textSize = ballTextSize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = width
        mHeight = height
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw red balls
//        redBalls.forEach {
//            canvas.drawBitmap()
//            canvas.drawCircle()
//        }
    }

    fun setRedBalls(balls: List<String>) = apply {
        this.redBalls = balls
    }

    fun setBlueBalls(balls: List<String>) = apply {
        this.blueBalls = balls
    }

    fun setFormatBallString(format: String) = apply {
        val formatSplit = format.split("+")
        if (formatSplit.size < 2) throw IllegalArgumentException("balls string format error")
        this.redBalls = formatSplit[0].split(" ")
        this.blueBalls = formatSplit[1].split(" ")
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [Gravity.START, Gravity.END])
    annotation class GravityFlags
}