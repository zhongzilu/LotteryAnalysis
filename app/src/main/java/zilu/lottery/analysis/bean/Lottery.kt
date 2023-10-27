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
import zilu.lottery.annotation.Val

/**
 * @param id 开奖期号
 * @param balls 开奖号码，格式: `1 2 3 4 5+2 3`
 * Create by zilu 2023/07/27
 */
class Lottery {

    @JvmField
    val id: String

    @JvmField
    val balls: String

    @JvmField
    val miss: Array<Int>

    //红球组
    @JvmField
    val redBalls: List<Int>

    //篮球组
    @JvmField
    val blueBalls: List<Int>

    @JvmField
    val redP: RedBallPartition

    @JvmField
    val blueP: BlueBallPartition

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
        this.blueBalls = split[1].split(" ").map { it.toInt() }
        this.miss = Array(Val.DLT_BALL_SIZE) { i ->
            if (i < Val.DLT_RED_BALL_SIZE) {
                if (redBalls.contains(i + 1)) 0 else 1
            } else {
                if (blueBalls.contains(i - Val.DLT_RED_BALL_SIZE.dec())) 0 else 1
            }
        }
        this.redP = RedBallPartition(redBalls)
        this.blueP = BlueBallPartition(blueBalls)
        this.date = date
        this.jackpot = jackpot
    }

    constructor(
        id: String,
        balls: String,
        miss: List<Int>,
        rBalls: List<Int>,
        bBalls: List<Int>,
        redP: RedBallPartition,
        blueP: BlueBallPartition,
        jackpot: String,
        date: String
    ) {
        this.id = id
        this.balls = balls
        this.miss = miss.toTypedArray()
        this.redBalls = rBalls
        this.blueBalls = bBalls
        this.redP = redP
        this.blueP = blueP
        this.date = date
        this.jackpot = jackpot
    }

    override fun toString(): String = gson.toJson(this)
}

class RedBallPartition {

    @JvmField
    val p5: Array<Int> = Array(5) { 0 }

    @JvmField
    val p5t: String

    @JvmField
    val p7: Array<Int> = Array(7) { 0 }

    @JvmField
    val p7t: String

    constructor(redBalls: List<Int>) {
        computePartitionFrom(redBalls)

        //compute five Partition Type
        val arr = ArrayList<Int>(5)
        for (i in p5) if (i > 0) arr.add(i)
        arr.sortDescending()
        p5t = arr.joinToString("")
        arr.clear()

        //compute seven Partition Type
        for (i in p7) if (i > 0) arr.add(i)
        arr.sortDescending()
        p7t = arr.joinToString("")
        arr.clear()
    }

    constructor(p5: Array<Int>, p5t: String, p7: Array<Int>, p7t: String) {
        p5.copyInto(this.p5)
        this.p5t = p5t
        p7.copyInto(this.p7)
        this.p7t = p7t
    }

    private fun computePartitionFrom(redBalls: List<Int>) {
        var i: Int
        for (redBall in redBalls) {
            i = redBall - 1
            p5[i / 7] += 1
            p7[i / 5] += 1
        }
    }

    override fun toString(): String = gson.toJson(this)
}

class BlueBallPartition {

    constructor(blueBalls: List<Int>)

    override fun toString(): String = gson.toJson(this)
}

