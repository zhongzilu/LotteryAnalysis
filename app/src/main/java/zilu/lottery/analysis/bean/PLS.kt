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
 * 排列三
 * Create by zilu 2023/09/22
 */

class PLS {

    @JvmField
    val id: String

    @JvmField
    val balls: String

    //红球组
    @JvmField
    val redBalls: List<Int>

    @JvmField
    val date: String

    @JvmField
    val jackpot: String

    constructor(id: String, balls: String, date: String, jackpot: String) {
        val split = balls.split("+")
        if (split.size < 2) throw IllegalArgumentException("Error: balls format invalidate, like: `1 2 3 4 5+2 3`")
        this.id = id
        this.balls = balls
        this.redBalls = split[0].split(" ").map { it.toInt() }
        this.date = date
        this.jackpot = jackpot
    }

    constructor(
        id: String, balls: String, rBalls: List<Int>, jackpot: String, date: String
    ) {
        this.id = id
        this.balls = balls
        this.redBalls = rBalls
        this.date = date
        this.jackpot = jackpot
    }

    override fun toString(): String = gson.toJson(this)
}