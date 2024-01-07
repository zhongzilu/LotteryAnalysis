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

package zilu.lottery.analysis.activity

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import kotlinx.coroutines.*
import org.jetbrains.anko.collections.forEachReversedByIndex
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.startActivity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.*
import zilu.lottery.analysis.data.*
import zilu.lottery.analysis.utils.SP
import zilu.lottery.annotation.SPKey
import java.io.IOException
import java.net.Proxy

class SplashActivity : AppCompatActivity() {

    private lateinit var logText: TextView
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var nextBtn: View
    private val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!SP.isInit) {
            goMainActivity()
            return
        }

        //first install, doing some initial operates
        setContentView(R.layout.activity_splash)
        logText = findViewById(R.id.logText)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        logText.movementMethod = ScrollingMovementMethod.getInstance()
        nextBtn = findViewById<View>(R.id.nextBtn)
        nextBtn.setOnClickListener {
            goMainActivity()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }

    private fun start() {
        log("> 正在执行数据初始化……\n")

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log("> 加载数据异常，请重启应用！\n")
            throwable.printStackTrace()
        }
        mainScope.launch(exceptionHandler) {
            initLotteryDataTask()
            initSSQDataTask()
            initPLSDataTask()
            initPLWDataTask()
            SP.remove(SPKey.UPDATE)
            SP.isInit = false
            nextBtn.visibility = View.VISIBLE
        }
    }

    private suspend fun initLotteryDataTask() = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            log("============================\n")
            log("> 开始初始化大乐透数据\n> 检查本地数据……\n")
        }

        val size = LotteryTable.count()
        if (size > 0) {
            withContext(Dispatchers.Main) {
                log("> 已存在 $size 条数据，不需要进行初始化……\n")
            }
            return@withContext
        }

        withContext(Dispatchers.Main) {
            log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")
        }

        val document: Document
        try {
            val url = getString(R.string.lottery_history_url, "07001")
            document = Jsoup.connect(url)
                .proxy(Proxy.NO_PROXY)
                .userAgent(getString(R.string.user_agent))
                .maxBodySize(0)
                .get()

            withContext(Dispatchers.Main) {
                log("> 加载数据完毕，正在解析数据……\n")
            }
        } catch (e: IOException) {
            throw IOException("加载数据异常，请重启应用！", e)
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

            withContext(Dispatchers.Main) {
                log("> 解析数据完毕，正在保存数据……\n")
            }
        } catch (e: Throwable) {
            throw RuntimeException("解析数据异常，请重启应用！", e)
        }

        //save to SQLite database
        try {
            LotteryTable.save(lotteries)

            val map = HashMap<String, Int>()

            //统计红球5分区历史数据
            lotteries.forEach { lo ->
                val t = lo.redP.p5t
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1
            }
            //保存红球5分区历史数据
            map.forEach { e ->
                val mapKV =
                    MapKV(0, "rp5t.${e.key}", "${e.value}", System.currentTimeMillis())
                MapTable.save(mapKV)
            }

            //按年统计红球5分区类型
            map.clear()
            lotteries.forEach { lo ->
                val y = lo.date.substring(0, 4)
                val t = "$y.${lo.redP.p5t}"
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1

//                    val year = yearMap[y]
//                    yearMap[y] = if (year != null) year + 1 else 1
            }
            map.forEach { e ->
                val mapKV =
                    MapKV(0, "rp5t.${e.key}", "${e.value}", System.currentTimeMillis())
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
                    MapKV(0, "rp7t.${e.key}", "${e.value}", System.currentTimeMillis())
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
                val mapKV =
                    MapKV(0, "rp7t.${e.key}", "${e.value}", System.currentTimeMillis())
                MapTable.save(mapKV)
            }

//                yearMap.clear()
            map.clear()

            withContext(Dispatchers.Main) {
                log("> 保存数据完毕, 共保存 ${lotteries.size} 条数据\n")
//                    nextBtn.visibility = View.VISIBLE
            }

//            initSSQDataTask()

        } catch (e: Throwable) {
            throw RuntimeException("保存数据异常", e)
        }
    }

    private suspend fun initSSQDataTask() = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            log("============================\n")
            log("> 开始初始化双色球数据\n> 检查本地数据……\n")
        }

        val size = SSQTable.count()
        if (size > 0) {
            withContext(Dispatchers.Main) {
                log("> 已存在 $size 条数据，不需要进行初始化……\n")
            }
            return@withContext
        }

        withContext(Dispatchers.Main) {
            log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")
        }

        val document: Document
        try {
            val url = getString(R.string.ssq_history_url, "03001")
            document = Jsoup.connect(url)
                .proxy(Proxy.NO_PROXY)
                .userAgent(getString(R.string.user_agent))
                .maxBodySize(0)
                .get()

            withContext(Dispatchers.Main) {
                log("> 加载数据完毕，正在解析数据……\n")
            }
        } catch (e: IOException) {
            throw IOException("加载数据异常，请重启应用！", e)
        }

        val ssqList: ArrayList<SSQ>
        try {
            val elements = document.select("#tdata > .t_tr1")

            var upper: SSQ? = null
            ssqList = ArrayList(elements.size)
            elements.forEachReversedByIndex {
                val id = it.child(0).text()
                val r1 = it.child(1).text()
                val r2 = it.child(2).text()
                val r3 = it.child(3).text()
                val r4 = it.child(4).text()
                val r5 = it.child(5).text()
                val r6 = it.child(6).text()
                val b1 = it.child(7).text()
                val jackpot = it.child(9).text()
                val date = it.child(15).text()

                val balls = "$r1 $r2 $r3 $r4 $r5 $r6+$b1"
                val ssq = SSQ(id, balls, date, jackpot)
                if (upper != null) {
                    ssq.miss.forEachWithIndex { i, loss ->
                        if (loss == 1) ssq.miss[i] = upper!!.miss[i] + 1
                    }
                }

                upper = ssq
                ssqList.add(ssq)
            }

            withContext(Dispatchers.Main) {
                log("> 解析数据完毕，正在保存数据……\n")
            }
        } catch (e: Throwable) {
            throw RuntimeException("解析数据异常，请重启应用！", e)
        }

        //save to SQLite database
        try {
            SSQTable.save(ssqList)
            withContext(Dispatchers.Main) {
                log("> 保存数据完毕, 共保存 ${ssqList.size} 条数据\n")
            }

        } catch (e: Throwable) {
            throw RuntimeException("保存数据异常", e)
        }
    }

    private suspend fun initPLSDataTask() = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            log("============================\n")
            log("> 开始初始化排列3数据\n> 检查本地数据……\n")
        }

        val size = PLSTable.count()
        if (size > 0) {
            withContext(Dispatchers.Main) {
                log("> 已存在 $size 条数据，不需要进行初始化……\n")
            }
            return@withContext
        }

        withContext(Dispatchers.Main) {
            log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")
        }

        val document: Document
        try {
            val url = getString(R.string.pls_history_url, -1, "04001")
            document = Jsoup.connect(url)
                .proxy(Proxy.NO_PROXY)
                .userAgent(getString(R.string.user_agent))
                .maxBodySize(0)
                .get()

            withContext(Dispatchers.Main) {
                log("> 加载数据完毕，正在解析数据……\n")
            }
        } catch (e: IOException) {
            throw IOException("加载数据异常，请重启应用！", e)
        }

        val plsList: ArrayList<PLS>
        try {
            val elements = document.select("#tablelist tr.t_tr1")

            plsList = ArrayList(elements.size)
            elements.forEachReversedByIndex {
                val id = it.child(0).text()
                val balls = it.child(1).text()
                val saleAmount = it.child(3).text()
                val date = it.child(10).text()

                val pls = PLS(id, balls, date, saleAmount)
                plsList.add(pls)
            }

            withContext(Dispatchers.Main) {
                log("> 解析数据完毕，正在保存数据……\n")
            }
        } catch (e: Throwable) {
            throw RuntimeException("解析数据异常，请重启应用！", e)
        }

        //save to SQLite database
        try {
            PLSTable.save(plsList)
            withContext(Dispatchers.Main) {
                log("> 保存数据完毕, 共保存 ${plsList.size} 条数据\n")
            }

        } catch (e: Throwable) {
            e.printStackTrace()
            throw RuntimeException("保存数据异常", e)
        }
    }

    private suspend fun initPLWDataTask() = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            log("============================\n")
            log("> 开始初始化排列5数据\n> 检查本地数据……\n")
        }

        val size = PLWTable.count()
        if (size > 0) {
            withContext(Dispatchers.Main) {
                log("> 已存在 $size 条数据，不需要进行初始化……\n")
            }
            return@withContext
        }

        withContext(Dispatchers.Main) {
            log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")
        }

        val document: Document
        try {
            val url = getString(R.string.plw_history_url, -1, "04001")
            document = Jsoup.connect(url)
                .proxy(Proxy.NO_PROXY)
                .userAgent(getString(R.string.user_agent))
                .maxBodySize(0)
                .get()

            withContext(Dispatchers.Main) {
                log("> 加载数据完毕，正在解析数据……\n")
            }
        } catch (e: IOException) {
            throw IOException("加载数据异常，请重启应用！", e)
        }

        val plwList: ArrayList<PLW>
        try {
            val elements = document.select("#tablelist tr.t_tr1")

            plwList = ArrayList(elements.size)
            elements.forEachReversedByIndex {
                val id = it.child(0).text()
                val balls = it.child(1).text()
                val saleAmount = it.child(3).text()
                val date = it.child(4).text()

                val pls = PLW(id, balls, date, saleAmount)
                plwList.add(pls)
            }

            withContext(Dispatchers.Main) {
                log("> 解析数据完毕，正在保存数据……\n")
            }
        } catch (e: Throwable) {
            throw RuntimeException("解析数据异常，请重启应用！", e)
        }

        //save to SQLite database
        try {
            PLWTable.save(plwList)
            withContext(Dispatchers.Main) {
                log("> 保存数据完毕, 共保存 ${plwList.size} 条数据\n")
            }

        } catch (e: Throwable) {
            throw RuntimeException("保存数据异常", e)
        }
    }

    private fun log(msg: String) {
        logText.append(msg)
        nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
    }

    private fun goMainActivity() {
        startActivity<MainActivity>()
        finish()
    }
}
