package zilu.lottery.analysis

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import org.jetbrains.anko.collections.forEachReversedWithIndex
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.bean.SSQ
import zilu.lottery.analysis.data.LotteryTable
import zilu.lottery.analysis.data.MapTable
import zilu.lottery.analysis.data.SSQTable
import zilu.lottery.analysis.utils.SP
import java.io.IOException
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 每次启动更新数据的Service
 * Create by zilu 2023/08/24
 */
class UpdateDataService : Service() {
    private val TAG = "UpdateDataSer-->"
    private val nowDate = DateFormat.getDateInstance().format(Date())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (nowDate == SP.autoUpdateDate) return

        if (!SP.isInit) {
            updateDLT()
        } else {
            stopSelf()
        }
    }

    private fun updateDLT() {
        Log.d(TAG, "> 正在执行大乐透数据初始化……\n> 检查本地数据……")
        toast("开始检查最新数据")

        doAsync {

            var size = LotteryTable.count()
            Log.d(TAG, "> 本地存在 $size 条数据！")

            var upper: Lottery? = null
            var startId = "07001"
            if (size > 0) {
                Log.d(TAG, "> 查询本地最新数据……")

                upper = LotteryTable.findByLimit(1)[0]
                startId = upper.id
            }

            Log.d(TAG, "> 本地最新数据为 $startId 期！")
            Log.d(TAG, "> 开始加载网络最新数据……")

            val document: Document
            try {
                val url = getString(R.string.lottery_history_url, startId)
                document = Jsoup.connect(url)
                    .userAgent(getString(R.string.user_agent))
                    .get()

                Log.d(TAG, "> 加载数据完毕，正在解析数据……")
            } catch (e: IOException) {
                Log.d(TAG, "> 加载数据异常，请重试！")
                uiThread { toast("加载数据异常，请重试") }
                stopSelf()
                return@doAsync
            }

            val lotteries: ArrayList<Lottery>
            try {
                val elements = document.select("#tdata > .t_tr1")
                size = elements.size
                if (size <= 1) {
                    //already latest data
                    Log.d(TAG, "> 已经是最新数据！")
                    updateSSQ()
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

                Log.d(TAG, "> 解析数据完毕，正在保存数据……")
            } catch (e: Throwable) {
                Log.d(TAG, "> 解析数据异常，请重试！")
                uiThread { toast("解析数据异常，请重试") }
                stopSelf()
                return@doAsync
            }

            //save to sqlite database
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
                    val mapKV = MapTable.findByKey("rp5t.${e.key}")
                    //modify the old value
                    mapKV.value = "${mapKV.value.toInt() + e.value}"
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

//                    val year = yearMap[y]
//                    yearMap[y] = if (year != null) year + 1 else 1
                }
                map.forEach { e ->
                    val mapKV = MapTable.findByKey("rp5t.${e.key}")
                    //modify the old value
                    mapKV.value = "${mapKV.value.toInt() + e.value}"
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
                    //modify the old value
                    mapKV.value = "${mapKV.value.toInt() + e.value}"
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
                    //modify the old value
                    mapKV.value = "${mapKV.value.toInt() + e.value}"
                    mapKV.date = System.currentTimeMillis()
                    if (mapKV.id == 0) {
                        //not exist
                        MapTable.save(mapKV)
                    } else {
                        MapTable.update(mapKV)
                    }
                }

//                yearMap.clear()
                map.clear()

                Log.d(TAG, "> 保存数据完毕, 共保存 ${lotteries.size} 条数据")
            } catch (e: Throwable) {
                Log.d(TAG, "> 保存数据异常： ${Log.getStackTraceString(e)}")
            }

            updateSSQ()
        }
    }

    private fun updateSSQ() {
        Log.d(TAG, "==============================\n")
        Log.d(TAG, "> 正在执行双色球数据初始化……\n> 检查本地数据……")

        doAsync {

            var size = SSQTable.count()
            Log.d(TAG, "> 本地存在 $size 条数据！")

            var upper: SSQ? = null
            var startId = "03001"
            if (size > 0) {
                Log.d(TAG, "> 查询本地最新数据……")

                upper = SSQTable.findByLimit(1)[0]
                startId = upper.id
            }

            Log.d(TAG, "> 本地最新数据为 $startId 期！")
            Log.d(TAG, "> 开始加载网络最新数据……")

            val document: Document
            try {
                val url = getString(R.string.ssq_history_url, startId)
                document = Jsoup.connect(url)
                    .userAgent(getString(R.string.user_agent))
                    .get()

                Log.d(TAG, "> 加载数据完毕，正在解析数据……")
            } catch (e: IOException) {
                Log.d(TAG, "> 加载数据异常，请重试！")
                uiThread { toast("加载数据异常，请重试") }
                stopSelf()
                return@doAsync
            }

            val ssqList: ArrayList<SSQ>
            try {
                val elements = document.select("#tdata > .t_tr1")
                size = elements.size
                if (size <= 1) {
                    //already latest data
                    SP.autoUpdateDate = nowDate
                    Log.d(TAG, "> 已经是最新数据！")
                    uiThread { toast("已经是最新数据") }
                    stopSelf()
                    return@doAsync
                }

                ssqList = ArrayList<SSQ>(size)
                elements.forEachReversedWithIndex { i, element ->
                    if (i < size - 1) {
                        val id = element.child(0).text()
                        val r1 = element.child(1).text()
                        val r2 = element.child(2).text()
                        val r3 = element.child(3).text()
                        val r4 = element.child(4).text()
                        val r5 = element.child(5).text()
                        val r6 = element.child(6).text()
                        val b1 = element.child(7).text()
                        val jackpot = element.child(9).text()
                        val date = element.child(15).text()

                        val balls = "$r1 $r2 $r3 $r4 $r5 $r6+$b1"
                        val ssq = SSQ(id, balls, date, jackpot)
                        if (upper != null) {
                            ssq.miss.forEachWithIndex { j, loss ->
                                if (loss == 1) ssq.miss[j] = upper!!.miss[j] + 1
                            }
                        }

                        upper = ssq
                        ssqList.add(ssq)
                    }
                }

                Log.d(TAG, "> 解析数据完毕，正在保存数据……")
            } catch (e: Throwable) {
                Log.d(TAG, "> 解析数据异常，请重试！")
                uiThread { toast("解析数据异常，请重试") }
                stopSelf()
                return@doAsync
            }

            //save to sqlite database
            try {
                SSQTable.save(ssqList)
                SP.autoUpdateDate = nowDate
                Log.d(TAG, "> 保存数据完毕, 共保存 ${ssqList.size} 条数据")
                uiThread { toast("已更新最新数据") }
            } catch (e: Throwable) {
                Log.d(TAG, "> 保存数据异常： ${Log.getStackTraceString(e)}")
            }

            stopSelf()
        }
    }

}