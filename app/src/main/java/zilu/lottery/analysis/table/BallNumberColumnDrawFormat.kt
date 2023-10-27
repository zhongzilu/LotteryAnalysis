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
import android.text.TextPaint
import com.bin.david.form.core.TableConfig
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.format.draw.TextDrawFormat
import com.bin.david.form.utils.DrawUtils
import zilu.lottery.analysis.data.Constants

/**
 *
 * Create by zilu 2023/07/28
 */
open class BallNumberColumnDrawFormat : TextDrawFormat<String>() {

    @JvmField
    protected var ballColor = Constants.redBallColor

    @JvmField
    protected var ballPaintStyle = Paint.Style.FILL

    @JvmField
    protected var ballRadius: Float = 28f

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    init {
        textPaint.color = Color.RED
        textPaint.textSize = 34f
        textPaint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun draw(canvas: Canvas, rect: Rect, cellInfo: CellInfo<String>, config: TableConfig) {
        val paint = config.paint

        setTextPaint(config, cellInfo, paint)
        if (cellInfo.column.textAlign != null) {
            paint.textAlign = cellInfo.column.textAlign
        }

//        Log.d("-->", "$rect && ${rect.width()}")
//        val text = "你好+世界"
        val text = cellInfo.value
        DrawUtils.drawSingleText(canvas, paint, rect, text)
//        val builder = SpannableStringBuilder(text).apply {
//            val red = ForegroundColorSpan(Constants.redBallColor)
//            setSpan(red, 0, text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//        }
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//            DynamicLayout.Builder.obtain(builder, TextPaint(paint), 300)
//                .setTextDirection(TextDirectionHeuristics.LTR)
//                .setAlignment(Layout.Alignment.ALIGN_CENTER)
//                .setEllipsize(TextUtils.TruncateAt.END)
//                .build()
//                .draw(canvas)
//        }
//        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
//            color = Color.RED
//            style = Paint.Style.FILL_AND_STROKE
//            textSize = 34f
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            DynamicLayout.Builder.obtain(builder, textPaint, rect.width())
//                .setTextDirection(TextDirectionHeuristics.LTR)
//                .setAlignment(Layout.Alignment.ALIGN_CENTER)
//                .setEllipsize(TextUtils.TruncateAt.END)
//                .setLineSpacing(0f, 1f)
//                .setIncludePad(false)
//                .build()
//                .draw(canvas)

//            canvas.save()
//            Log.d("drawFormat-->", builder.toString())
//            StaticLayout.Builder.obtain(
//                builder,
//                0,
//                builder.length,
//                textPaint,
//                rect.width()
//            )
//                .setTextDirection(TextDirectionHeuristics.LTR)
//                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
//                .setEllipsize(TextUtils.TruncateAt.END)
//                .setMaxLines(1)
//                .setLineSpacing(0.0f, 1.0f)
//                .setIncludePad(false)
//                .build()
//                .draw(canvas)
//            canvas.restore()
//        }
//        else {
//            StaticLayout(
//                builder,
//                textPaint,
//                canvas.width,
//                Layout.Alignment.ALIGN_CENTER,
//                1.0f,
//                0.0f,
//                false
//            ).draw(canvas)
//        }
    }

    fun setBallColor(color: Int): BallNumberColumnDrawFormat {
        this.ballColor = color
        return this
    }

    fun setBallPaintStyle(style: Paint.Style): BallNumberColumnDrawFormat {
        ballPaintStyle = style
        return this
    }

    fun setBallRadius(radius: Float): BallNumberColumnDrawFormat {
        this.ballRadius = radius
        return this
    }

}