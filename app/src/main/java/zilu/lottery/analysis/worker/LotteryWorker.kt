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

package zilu.lottery.analysis.worker

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jetbrains.anko.collections.forEachReversedByIndex
import org.jetbrains.anko.collections.forEachWithIndex
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.bean.MapKV
import zilu.lottery.analysis.data.LotteryTable
import zilu.lottery.analysis.data.MapTable
import zilu.lottery.analysis.utils.SP
import zilu.lottery.annotation.SPKey
import java.io.IOException
import java.net.Proxy

/**
 *
 * Create by zilu 2023/09/22
 */
class InitLotteryWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private val progressData = Data.Builder()

    override fun doWork(): Result {
        val size = LotteryTable.count()
        if (size > 0) {
            log("> 已存在 $size 条数据，不需要进行初始化……\n")
            return Result.success()
        }

        log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")

        val document: Document
        try {
            val url = applicationContext.getString(R.string.lottery_history_url, "07001")
            document = Jsoup.connect(url)
                .proxy(Proxy.NO_PROXY)
                .userAgent(applicationContext.getString(R.string.user_agent))
                .maxBodySize(0)
                .get()

            log("> 加载数据完毕，正在解析数据……\n")
        } catch (e: IOException) {
            log("> 加载数据异常，请重启应用！\n")
            return Result.failure()
        }

        val lotteries: ArrayList<Lottery>
        try {
            val elements = document.select("#tdata > .t_tr1")

            var upper: Lottery? = null
            lotteries = ArrayList(elements.size)
            elements.forEachReversedByIndex {
                val id = it.child(0).text()
                val r1 = it.child(1).text()
                val r2 = it.child(2).text()
                val r3 = it.child(3).text()
                val r4 = it.child(4).text()
                val r5 = it.child(5).text()
                val b1 = it.child(6).text()
                val b2 = it.child(7).text()
                val jackpot = it.child(8).text()
                val date = it.child(14).text()

                val balls = "$r1 $r2 $r3 $r4 $r5+$b1 $b2"
                val lottery = Lottery(id, balls, date, jackpot)
                if (upper != null) {
                    lottery.miss.forEachWithIndex { i, loss ->
                        if (loss == 1) lottery.miss[i] = upper!!.miss[i] + 1
                    }
                }

                upper = lottery
                lotteries.add(lottery)
            }

            log("> 解析数据完毕，正在保存数据……\n")
        } catch (e: Throwable) {
            log("> 解析数据异常，请重启应用！\n")
            return Result.failure()
        }

        //save to SQLite database
        try {
            LotteryTable.save(lotteries)

            val map = HashMap<String, Int>()
            val yearMap = HashMap<String, Int>()
            val total = lotteries.size

            //统计红球5分区历史数据
            lotteries.forEach { lo ->
                val t = lo.redP.p5t
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1
            }
            //保存红球5分区历史数据
            map.forEach { e ->
                val mapKV =
                    MapKV(0, "rp5t.${e.key}", "${e.value}/$total", System.currentTimeMillis())
                MapTable.save(mapKV)
            }

            //按年统计红球5分区类型
            map.clear()
            lotteries.forEach { lo ->
                val y = lo.date.substring(0, 4)
                val t = "$y.${lo.redP.p5t}"
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1

                val year = yearMap[y]
                yearMap[y] = if (year != null) year + 1 else 1
            }
            map.forEach { e ->
                val mapKV = MapKV(
                    0,
                    "rp5t.${e.key}",
                    "${e.value}/${yearMap[e.key.substring(0, 4)]}",
                    System.currentTimeMillis()
                )
                MapTable.save(mapKV)
            }

            //统计红球7分区历史统计
            map.clear()
            lotteries.forEach { lo ->
                val t = lo.redP.p7t
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1
            }
            //保存红球7分区历史统计
            map.forEach { e ->
                val mapKV =
                    MapKV(0, "rp7t.${e.key}", "${e.value}/$total", System.currentTimeMillis())
                MapTable.save(mapKV)
            }

            //按年统计红球7分区类型
            map.clear()
            lotteries.forEach { lo ->
                val y = lo.date.substring(0, 4)
                val t = "$y.${lo.redP.p7t}"
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1
            }
            map.forEach { e ->
                val mapKV = MapKV(
                    0,
                    "rp7t.${e.key}",
                    "${e.value}/${yearMap[e.key.substring(0, 4)]}",
                    System.currentTimeMillis()
                )
                MapTable.save(mapKV)
            }

            yearMap.clear()
            map.clear()

            SP.remove(SPKey.INIT)
            SP.isUpdate = false
            log("> 保存数据完毕, 共保存 ${lotteries.size} 条数据\n")

        } catch (e: Throwable) {
            log("> 保存数据异常: \n ${Log.getStackTraceString(e)}\n")
            return Result.failure()
        }
        return Result.success()
    }

    private fun log(msg: String) {
        setProgressAsync(progressData.putString("log", msg).build())
    }
}