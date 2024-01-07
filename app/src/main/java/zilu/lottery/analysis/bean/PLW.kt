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

package zilu.lottery.analysis.bean

import zilu.lottery.analysis.gson

/**
 * 排列五
 * Create by zilu 2023/09/22
 */
class PLW {

    @JvmField
    val id: String

    @JvmField
    val balls: String

    @JvmField
    val miss: Array<Int>

    //红球组
    @JvmField
    val redBalls: List<Int>

    @JvmField
    val date: String

    @JvmField
    val saleAmount: String

    constructor(id: String, balls: String, date: String, saleAmount: String) {
        this.id = id
        this.balls = balls
        this.redBalls = balls.split(" ").map { it.toInt() }
        this.miss = emptyArray()
        this.date = date
        this.saleAmount = saleAmount
    }

    constructor(
        id: String, balls: String, miss: List<Int>, rBalls: List<Int>, saleAmount: String, date: String
    ) {
        this.id = id
        this.balls = balls
        this.miss = miss.toTypedArray()
        this.redBalls = rBalls
        this.date = date
        this.saleAmount = saleAmount
    }

    override fun toString(): String = gson.toJson(this)
}