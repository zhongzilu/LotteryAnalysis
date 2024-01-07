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

package zilu.lottery.analysis.data

import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.annotation.Val

/**
 *
 * Create by zilu 2023/08/01
 */
object DataUtils {

    private var limit: Int = Val.DEFAULT_QUERY_LIMIT
    private var year: String = ""
    private val datas = ArrayList<Lottery>(50)

    @JvmStatic
    fun getLottery(): List<Lottery> = datas

    @JvmStatic
    fun getLottery(limit: Int): List<Lottery> {
        if (this.limit == limit && datas.isNotEmpty())
            return datas

        datas.clear()
        datas.addAll(LotteryTable.findByLimit(limit))
        datas.reverse()

        this.limit = limit
        this.year = ""
        return datas
    }

    @JvmStatic
    fun getLottery(year: String): List<Lottery> {
        if (year.isEmpty()) return datas
        if (this.year == year && datas.isNotEmpty())
            return datas

        datas.clear()
        datas.addAll(LotteryTable.findByDate(year))

        this.year = year
        this.limit = -1
        return datas
    }
}