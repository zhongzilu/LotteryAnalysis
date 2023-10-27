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
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import org.jetbrains.anko.collections.forEachReversedByIndex
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.bean.MapKV
import zilu.lottery.analysis.bean.SSQ
import zilu.lottery.analysis.data.LotteryTable
import zilu.lottery.analysis.data.MapTable
import zilu.lottery.analysis.data.SSQTable
import zilu.lottery.analysis.utils.SP
import zilu.lottery.annotation.SPKey
import java.io.IOException

class SplashActivity : AppCompatActivity() {

    private lateinit var logText: TextView
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var nextBtn: View

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

    private fun start() {
        log("> 正在执行数据初始化……\n")

//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val initLotteryWorkRequest =
//            OneTimeWorkRequest.Builder(InitLotteryWorker::class.java)
//                .setConstraints(constraints)
//                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//                .build()
//
//        val workManager = WorkManager.getInstance(this)
//        workManager.getWorkInfoByIdLiveData(initLotteryWorkRequest.id)
//            .observe(this, ::progressObserver)
//        workManager.beginWith(initLotteryWorkRequest)
//            .enqueue()

        initLotteryDataTask()

//        MainScope().launch {
//            val res = withContext(Dispatchers.IO) {
//            }
//        }
//        CoroutineScope(Dispatchers.IO).launch {
//
//        }
    }

    private fun initLotteryDataTask() {
        log("============================\n")
        log("> 开始初始化大乐透数据\n> 检查本地数据……\n")

        doAsync {

            val size = LotteryTable.count()
            if (size > 0) {
                uiThread {
                    log("> 已存在 $size 条数据，不需要进行初始化……\n")
//                    nextBtn.visibility = View.VISIBLE
                }
                initSSQDataTask()
                return@doAsync
            }

            uiThread {
                log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")
            }

            val document: Document
            try {
                val url = getString(R.string.lottery_history_url, "07001")
                document = Jsoup.connect(url)
                    .userAgent(getString(R.string.user_agent))
                    .get()

                uiThread {
                    log("> 加载数据完毕，正在解析数据……\n")
                }
            } catch (e: IOException) {
                uiThread {
                    log("> 加载数据异常，请重启应用！\n")
                }
                return@doAsync
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

                uiThread {
                    log("> 解析数据完毕，正在保存数据……\n")
                }
            } catch (e: Throwable) {
                uiThread {
                    log("> 解析数据异常，请重启应用！\n")
                }
                return@doAsync
            }

            //save to SQLite database
            try {
                LotteryTable.save(lotteries)

                val map = HashMap<String, Int>()
//                val yearMap = HashMap<String, Int>()
//                val total = lotteries.size

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

                uiThread {
                    log("> 保存数据完毕, 共保存 ${lotteries.size} 条数据\n")
//                    nextBtn.visibility = View.VISIBLE
                }

                initSSQDataTask()

            } catch (e: Throwable) {
                uiThread {
                    log("> 保存数据异常: \n ${Log.getStackTraceString(e)}\n")
                    log("> 请重启应用！\n")
                }
            }
        }
    }

    private fun initSSQDataTask() {

        doAsync {
            uiThread {
                log("============================\n")
                log("> 开始初始化双色球数据\n> 检查本地数据……\n")
            }

            val size = SSQTable.count()
            if (size > 0) {
                uiThread {
                    log("> 已存在 $size 条数据，不需要进行初始化……\n")
                    nextBtn.visibility = View.VISIBLE
                }
                return@doAsync
            }

            uiThread {
                log("> 本地存在 $size 条数据\n> 开始加载网络数据……\n")
            }

            val document: Document
            try {
                val url = getString(R.string.ssq_history_url, "03001")
                document = Jsoup.connect(url)
                    .userAgent(getString(R.string.user_agent))
                    .get()

                uiThread {
                    log("> 加载数据完毕，正在解析数据……\n")
                }
            } catch (e: IOException) {
                uiThread {
                    log("> 加载数据异常，请重启应用！\n")
                }
                return@doAsync
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

                uiThread {
                    log("> 解析数据完毕，正在保存数据……\n")
                }
            } catch (e: Throwable) {
                uiThread {
                    log("> 解析数据异常，请重启应用！\n")
                }
                return@doAsync
            }

            //save to SQLite database
            try {
                SSQTable.save(ssqList)
                SP.remove(SPKey.UPDATE)
                SP.isInit = false
                uiThread {
                    log("> 保存数据完毕, 共保存 ${ssqList.size} 条数据\n")
                    nextBtn.visibility = View.VISIBLE
                }

            } catch (e: Throwable) {
                uiThread {
                    log("> 保存数据异常: \n ${Log.getStackTraceString(e)}\n")
                }
            }
        }
    }

//    private fun progressObserver(workInfo: WorkInfo?) {
//        val data = workInfo?.progress ?: return
//        val log = data.getString("log")
//        if (!TextUtils.isEmpty(log))
//            logText.append(log)
//    }

    private fun log(msg: String) {
        logText.append(msg)
        nestedScrollView.fullScroll(NestedScrollView.FOCUS_DOWN)
    }

    private fun goMainActivity() {
        startActivity<MainActivity>()
        finish()
    }
}
