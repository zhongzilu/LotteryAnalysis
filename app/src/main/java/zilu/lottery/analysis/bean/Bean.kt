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

import com.stx.xhb.androidx.entity.BaseBannerInfo
import zilu.lottery.analysis.gson

/**
 *
 * Create by zilu 2023/08/09
 */

data class UpdateInfo(
    @JvmField val code: Int,
    @JvmField val name: String,
    @JvmField val filename: String,
    @JvmField val url: String,
    @JvmField val ts: Long,
    @JvmField val des: String,
    @JvmField val size: Int,
    @JvmField val md5: String
) {
    override fun toString(): String = gson.toJson(this)
}

class MapKV {
    @JvmField
    val id: Int

    @JvmField
    var key: String

    @JvmField
    var value: String

    @JvmField
    var date: Long

    constructor() : this(0, "", "", System.currentTimeMillis())

    constructor(id: Int, key: String, value: String, date: Long) {
        this.id = id
        this.key = key
        this.value = value
        this.date = date
    }

    override fun toString(): String = gson.toJson(this)
}

/**
 * 广告Banner
 */
class BannerItem(@JvmField val title: String, @JvmField val url: String) : BaseBannerInfo {
    override fun getXBannerUrl(): String = url
    override fun getXBannerTitle(): String = title
}

/**
 * 开奖列表item
 */
class ReleaseItem {
    /**开奖类型图标资源文件*/
    @JvmField
    val iconRes: Int

    /**开奖类型名称*/
    @JvmField
    val title: String

    /**开奖日期: 周二、四、日 21：15*/
    @JvmField
    val date: String

    /**开奖期号: 第0000000期 09-08(五)*/
    @JvmField
    val qihao: String

    /**奖池: 累计00.00亿*/
    @JvmField
    val jackpot: CharSequence

    /**开奖号码: 01 02 03 04 11+09 08*/
    @JvmField
    val balls: String

    constructor() : this(0, "", "", "", "", "")

    constructor(
        iconRes: Int,
        title: String,
        date: String,
        qihao: String,
        jackpot: CharSequence,
        balls: String
    ) {
        this.iconRes = iconRes
        this.title = title
        this.date = date
        this.qihao = qihao
        this.jackpot = jackpot
        this.balls = balls
    }
}