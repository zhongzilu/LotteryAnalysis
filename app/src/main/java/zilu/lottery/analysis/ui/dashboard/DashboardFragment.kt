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

package zilu.lottery.analysis.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.data.LotteryTable
import zilu.lottery.analysis.data.MapTable
import zilu.lottery.analysis.ui.BaseFragment
import java.io.IOException

class DashboardFragment : BaseFragment() {

    private lateinit var logText: TextView

    private val KEY_LOG = "log"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        logText = root.findViewById(R.id.logText)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logText.text = savedInstanceState?.getCharSequence(KEY_LOG) ?: ""
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { v ->
            if (running) {
                Snackbar.make(v, "任务进行中，请稍后……", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
                    .show()
            } else {
                start()
            }
        }
    }

    private var running = false
    private fun start() {
        running = true
        logText.text = ""
        logText.append("> 正在执行数据初始化……\n> 检查本地数据……\n")

        doAsync {

            var size = LotteryTable.count()
            uiThread {
                logText.append("> 本地存在 $size 条数据！\n")
            }

            var upper: Lottery? = null
            var startId = "00000"
            if (size > 0) {
                uiThread {
                    logText.append("> 查询本地最新数据……\n")
                }

                upper = LotteryTable.findByLimit(1)[0]
                startId = upper.id
            }

            uiThread {
                logText.append("> 本地最新数据为 $startId 期！\n> 开始加载网络最新数据……\n")
            }

            val document: Document
            try {
                val url = getString(R.string.lottery_history_url, startId)
                document = Jsoup.connect(url)
                    .userAgent(getString(R.string.user_agent))
                    .get()

                uiThread {
                    logText.append("> 加载数据完毕，正在解析数据……\n")
                }
            } catch (e: IOException) {
                uiThread {
                    logText.append("> 加载数据异常，请重试！\n")
                }
                running = false
                return@doAsync
            }

            val lotteries: ArrayList<Lottery>
            try {
                val elements = document.select("#tdata > .t_tr1")
                size = elements.size
                if (size <= 1) {
                    //already latest data
                    uiThread {
                        logText.append("> 已经是最新数据！\n")
                    }

                    running = false
                    return@doAsync
                }

                lotteries = ArrayList<Lottery>(size)
                elements.forEachReversedWithIndex { i, element ->
                    if (i < size - 1) {
                        val id = element.child(0).text()
                        val r1 = element.child(1).text()
                        val r2 = element.child(2).text()
                        val r3 = element.child(3).text()
                        val r4 = element.child(4).text()
                        val r5 = element.child(5).text()
                        val b1 = element.child(6).text()
                        val b2 = element.child(7).text()
                        val jackpot = element.child(8).text()
                        val date = element.child(14).text()

                        val balls = "$r1 $r2 $r3 $r4 $r5+$b1 $b2"
                        val lottery = Lottery(id, balls, date, jackpot)
                        if (upper != null) {
                            lottery.miss.forEachWithIndex { j, loss ->
                                if (loss == 1) lottery.miss[j] = upper!!.miss[j] + 1
                            }
                        }

                        upper = lottery
                        lotteries.add(lottery)
                    }
                }

                uiThread {
                    logText.append("> 解析数据完毕，正在保存数据……\n")
                }
            } catch (e: Throwable) {
                uiThread {
                    logText.append("> 解析数据异常，请重试！\n")
                }
                running = false
                return@doAsync
            }

            //save to sqlite database
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
                    val mapKV = MapTable.findByKey("rp5t.${e.key}")
                    val value = mapKV.value.split("/").map { it.toInt() }
                    //modify the old value
                    mapKV.value = "${value[0] + e.value}/${value[1] + total}"
                    mapKV.date = System.currentTimeMillis()
                    if (mapKV.id == 0) {
                        //not exist
                        MapTable.save(mapKV)
                    } else {
                        MapTable.update(mapKV)
                    }
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
                    val mapKV = MapTable.findByKey("rp5t.${e.key}")
                    val value = mapKV.value.split("/").map { it.toInt() }
                    //modify the old value
                    mapKV.value =
                        "${value[0] + e.value}/${value[1] + yearMap[e.key.substring(0, 4)]!!}"
                    mapKV.date = System.currentTimeMillis()
                    if (mapKV.id == 0) {
                        //not exist
                        MapTable.save(mapKV)
                    } else {
                        MapTable.update(mapKV)
                    }
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
                    val mapKV = MapTable.findByKey("rp7t.${e.key}")
                    val value = mapKV.value.split("/").map { it.toInt() }
                    //modify the old value
                    mapKV.value = "${value[0] + e.value}/${value[1] + total}"
                    mapKV.date = System.currentTimeMillis()
                    if (mapKV.id == 0) {
                        //not exist
                        MapTable.save(mapKV)
                    } else {
                        MapTable.update(mapKV)
                    }
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
                    val mapKV = MapTable.findByKey("rp7t.${e.key}")
                    val value = mapKV.value.split("/").map { it.toInt() }
                    //modify the old value
                    mapKV.value =
                        "${value[0] + e.value}/${value[1] + yearMap[e.key.substring(0, 4)]!!}"
                    mapKV.date = System.currentTimeMillis()
                    if (mapKV.id == 0) {
                        //not exist
                        MapTable.save(mapKV)
                    } else {
                        MapTable.update(mapKV)
                    }
                }

                yearMap.clear()
                map.clear()

                uiThread {
                    logText.append("> 保存数据完毕, 共保存 ${lotteries.size} 条数据\n")
                }
            } catch (e: Throwable) {
                uiThread {
                    logText.append("> 保存数据异常： ${Log.getStackTraceString(e)}\n")
                }
            }

            running = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(KEY_LOG, logText.text)
    }
}
