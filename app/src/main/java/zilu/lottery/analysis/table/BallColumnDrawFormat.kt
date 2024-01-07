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

package zilu.lottery.analysis.table

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.format.draw.TextDrawFormat
import com.bin.david.form.utils.DrawUtils
import zilu.lottery.analysis.data.Constants

/**
 *
 * Create by zilu 2023/07/28
 */
open class BallColumnDrawFormat : TextDrawFormat<Int>() {

    @JvmField
    protected var otherTextColor: Int = 0xff636363.toInt()

    @JvmField
    protected var ballColor = Constants.redBallColor

    @JvmField
    protected var ballPaintStyle = Paint.Style.FILL

    @JvmField
    protected var ballRadius: Float = 28f

    @JvmField
    protected var ballBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(c: Canvas, rect: Rect, cellInfo: CellInfo<Int>, config: TableConfig) {
        val paint = config.paint
        setTextPaint(config, cellInfo, paint)
        if (cellInfo.column.textAlign != null) {
            paint.textAlign = cellInfo.column.textAlign
        }

        //draw ball background
        var value = cellInfo.data
        if (value != null && value > 0) {
            ballBgPaint.style = ballPaintStyle
            ballBgPaint.color = ballColor
            c.drawCircle(rect.exactCenterX(), rect.exactCenterY(), ballRadius * config.zoom, ballBgPaint)
            paint.color = Color.WHITE
        } else {
            value = -value
            paint.color = otherTextColor
        }

        drawText(c, value.toString(), rect, paint)
    }

    override fun drawText(c: Canvas, value: String, rect: Rect, paint: Paint) {
        DrawUtils.drawSingleText(c, paint, rect, value)
    }

    fun setBallColor(color: Int): BallColumnDrawFormat {
        this.ballColor = color
        return this
    }

    fun setBallPaintStyle(style: Paint.Style): BallColumnDrawFormat {
        ballPaintStyle = style
        return this
    }

    fun setBallRadius(radius: Float): BallColumnDrawFormat {
        this.ballRadius = radius
        return this
    }

    fun setOtherTextColor(color: Int): BallColumnDrawFormat {
        this.otherTextColor = color
        return this
    }

}