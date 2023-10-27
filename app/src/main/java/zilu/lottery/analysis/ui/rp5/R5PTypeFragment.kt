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

package zilu.lottery.analysis.ui.rp5

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.bin.david.form.core.SmartTable
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.table.TableData
import com.bin.david.form.utils.DensityUtils
import org.jetbrains.anko.doAsync
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.data.Constants
import zilu.lottery.analysis.data.DataUtils
import zilu.lottery.analysis.data.LotteryTable
import zilu.lottery.analysis.data.MapTable
import zilu.lottery.analysis.table.MyContentCellBackgroundFormat
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.widget.MyFilterSpinner
import zilu.lottery.annotation.Val
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class R5PTypeFragment(private val name: String) : BaseFragment(),
    MyFilterSpinner.OnItemSelectedListener {

    constructor() : this("前区5分区形态分析")

    private lateinit var table: SmartTable<Lottery>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_partitions_main, container, false)
        root.findViewById<MyFilterSpinner>(R.id.filterSpinner)
            .setOnItemSelectedListener(this)
        val readme = root.findViewById<TextView>(R.id.readmeText)
        readme.setText(R.string.readme_partition_type)
        readme.visibility = View.VISIBLE
        table = root.findViewById(R.id.table)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTableConfig()
        buildAnalysisTable { DataUtils.getLottery() }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) buildAnalysisTable { DataUtils.getLottery() }
    }

    private fun initTableConfig() {
        val displayMetrics = resources.displayMetrics
//        Log.i("init-->", "w: ${displayMetrics.widthPixels}, h: ${displayMetrics.heightPixels}")

        val vPadding = DensityUtils.dp2px(activity, Val.V_PADDING)
        val hPadding = DensityUtils.dp2px(activity, Val.H_PADDING)
        val contentCellBackgroundFormat = MyContentCellBackgroundFormat()
        table.config
            .setContentCellBackgroundFormat(contentCellBackgroundFormat)
            .setColumnTitleBackground { canvas, rect, paint ->
                paint.color = Constants.cellBackgroundColor
                paint.style = Paint.Style.FILL
                canvas.drawRect(rect, paint)
            }
            .setHorizontalPadding(hPadding)
            .setVerticalPadding(vPadding)
            .setColumnTitleHorizontalPadding(hPadding)
            .setColumnTitleVerticalPadding(vPadding)
            .setShowXSequence(false)
            .setShowYSequence(false)
            .minTableWidth = displayMetrics.widthPixels

        table.setCanVerticalScroll(false)
    }

    private fun buildAnalysisTable(run: (() -> List<Lottery>)) {

        doAsync {
            val datas = run.invoke()
            val map = HashMap<String, Int>()
            datas.forEach { lo ->
                val t = lo.redP.p5t
                val count = map[t]
                map[t] = if (count != null) count + 1 else 1
            }

            val sortedList = ArrayList(map.entries)
            sortedList.sortByDescending { it.value }

            val typeColumn = Column<String>("区形态", "")
            typeColumn.isFixed = true
            typeColumn.datas = sortedList.map { it.key }

            val countColumn = Column<Int>("次数", "")
            countColumn.datas = sortedList.map { it.value }

            val currentPercentColumn = Column<String>("占比", "")
            currentPercentColumn.datas =
                sortedList.map { String.format("%.2f%%", (it.value.toFloat() / datas.size) * 100) }

            val currentColumn = Column<String>("当前统计", countColumn, currentPercentColumn)

            //本年度统计
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            val currentYear = calendar.get(Calendar.YEAR)
            val analysisList = ArrayList<Pair<String, String>>(sortedList.size)
            val currentYearCount = LotteryTable.countByYear(currentYear.toString())
            val historyCount = LotteryTable.count()

            sortedList.forEach {
                val num = MapTable.findByKey("rp5t.$currentYear.${it.key}").value
                analysisList.add(
                    num to String.format(
                        "%.2f%%",
                        (num.toFloat() / currentYearCount.toFloat()) * 100
                    )
                )
            }

            val yearCountColumn = Column<String>("次数", "")
            yearCountColumn.datas = analysisList.map { it.first }

            val yearPercentColumn = Column<String>("占比", "")
            yearPercentColumn.datas = analysisList.map { it.second }

            val yearColumn =
                Column<String>("(${currentYear}年)统计", yearCountColumn, yearPercentColumn)

            //历史统计
            analysisList.clear()
            sortedList.forEach {
                val num = MapTable.findByKey("rp5t.${it.key}").value
                analysisList.add(
                    num to String.format("%.2f%%", (num.toFloat() / historyCount.toFloat()) * 100)
                )
            }

            val historyCountColumn = Column<String>("次数", "")
            historyCountColumn.datas = analysisList.map { it.first }

            val historyPercentColumn = Column<String>("占比", "")
            historyPercentColumn.datas = analysisList.map { it.second }

            val historyColumn = Column<String>("历史统计", historyCountColumn, historyPercentColumn)

            val tableData =
                TableData(name, datas, typeColumn, currentColumn, yearColumn, historyColumn)
            table.tableData = tableData
        }
    }

    override fun onIssuesItemSelected(parent: AdapterView<*>, position: Int) {
        val num = when (position) {
            0 -> 30
            1 -> 50
            2 -> 100
            else -> -1
        }
        buildAnalysisTable { DataUtils.getLottery(num) }
    }

    private var initYearSpinner = true
    override fun onYearsItemSelected(parent: AdapterView<*>, position: Int) {
        if (initYearSpinner) {
            initYearSpinner = false
            return
        }

        val year = parent.getItemAtPosition(position).toString()
        buildAnalysisTable { DataUtils.getLottery(year) }
    }
}
