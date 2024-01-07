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
import android.graphics.Paint
import android.graphics.Rect
import com.bin.david.form.data.CellInfo
import com.bin.david.form.data.format.bg.ICellBackgroundFormat
import zilu.lottery.analysis.data.Constants
import zilu.lottery.annotation.Val

/**
 *
 * Create by zilu 2023/08/01
 */
class MyDLTBasicContentCellBackgroundFormat : ICellBackgroundFormat<CellInfo<*>> {
    override fun drawBackground(
        canvas: Canvas,
        rect: Rect,
        t: CellInfo<*>,
        paint: Paint
    ) {
        if (t.col == 0) { //column == qihao
            paint.color = Constants.cellBackgroundColor
        } else if (t.col <= Val.DLT_RED_BALL_SIZE) {
            paint.color = Constants.redBallCellBgColor
        } else if (t.col > Val.DLT_RED_BALL_SIZE) {
            paint.color = Constants.blueBallCellBgColor
        }
        canvas.drawRect(rect, paint)
    }

    override fun getTextColor(t: CellInfo<*>): Int = 0

}