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

package zilu.lottery.analysis.ui.partition

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.bin.david.form.core.SmartTable
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.table.TableData
import com.bin.david.form.utils.DensityUtils
import org.jetbrains.anko.doAsync
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.data.Constants
import zilu.lottery.analysis.data.DataUtils
import zilu.lottery.analysis.table.BallNumberColumnDrawFormat
import zilu.lottery.analysis.table.MyContentCellBackgroundFormat
import zilu.lottery.analysis.table.MyCountFormat
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.widget.MyFilterSpinner
import zilu.lottery.annotation.Val

/**
 * 前区5分区统计
 * Create by zilu 2023/07/31
 */
class Partition5MainFragment : BaseFragment(), MyFilterSpinner.OnItemSelectedListener {

    private lateinit var table: SmartTable<Lottery>

    private val columnArray = ArrayList<Column<*>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_partitions_main, container, false)
        root.findViewById<MyFilterSpinner>(R.id.filterSpinner)
            .setOnItemSelectedListener(this)
//        val readme = root.findViewById<TextView>(R.id.readmeText)
//        readme.text = getText(R.string.readme_r5p)
//        readme.visibility = View.VISIBLE
        table = root.findViewById(R.id.table)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTableConfig()
        buildTable { DataUtils.getLottery() }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) buildTable { DataUtils.getLottery() }
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

//                    Log.d("-->", rect.toString())
//                val text = "你好+世界"
//                val builder = SpannableStringBuilder(text).apply {
//                    val red = ForegroundColorSpan(Constants.redBallColor)
//                    setSpan(red, 0, text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
//                }
//
//                val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
//                    color = paint.color
//                    style = Paint.Style.FILL
//                    textSize = paint.textSize
//                }
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                    DynamicLayout.Builder.obtain(builder, textPaint, rect.width())
//                        .setTextDirection(TextDirectionHeuristics.LTR)
//                        .setAlignment(Layout.Alignment.ALIGN_CENTER)
//                        .setEllipsize(TextUtils.TruncateAt.END)
//                        .setLineSpacing(0f, 1f)
//                        .setIncludePad(false)
//                        .build()
//                        .draw(canvas)
//                    StaticLayout.Builder.obtain(
//                        builder,
//                        0,
//                        builder.length,
//                        textPaint,
//                        rect.width()
//                    )
//                        .setTextDirection(TextDirectionHeuristics.LTR)
//                        .setAlignment(Layout.Alignment.ALIGN_CENTER)
//                        .setEllipsize(TextUtils.TruncateAt.END)
//                        .setLineSpacing(0.0f, 1.0f)
//                        .setIncludePad(false)
//                        .build()
//                        .draw(canvas)
//                }
            }
            .setHorizontalPadding(hPadding)
            .setVerticalPadding(vPadding)
            .setColumnTitleHorizontalPadding(hPadding)
            .setColumnTitleVerticalPadding(vPadding)
            .setShowXSequence(false)
            .setShowYSequence(false)
            .setFixedCountRow(true)
//            .setCountBgCellFormat(countCellFormat)
            .setMinTableWidth(displayMetrics.widthPixels)
            .isShowTableTitle = false

        table.setZoom(false, 1f, .5f)
        table.setOnSmartTableInvalidateListener {
            initData = false
        }
    }

    private val intCountFormat = MyCountFormat<Int>()

//    private val countCellFormat = object : ICellBackgroundFormat<Column<*>> {
//        override fun drawBackground(
//            canvas: Canvas?,
//            rect: Rect?,
//            t: Column<*>?,
//            paint: Paint?
//        ) {
//        }
//
//        override fun getTextColor(t: Column<*>?): Int = Constants.blueBallColor
//    }

    private fun buildTable(run: (() -> List<Lottery>)) {
        doAsync {

            columnArray.clear()

            val datas = run.invoke()
            val dataLen = datas.size

            val qihaoColumn = Column<String>("期号", "")
            qihaoColumn.datas = List(dataLen) { i -> datas[i].id }
            qihaoColumn.isFixed = true
            columnArray.add(qihaoColumn)

            val ballsColumn = Column<String>("开奖号码", "", BallNumberColumnDrawFormat())
            ballsColumn.typeface = Typeface.MONOSPACE
            ballsColumn.datas = List(dataLen) { i -> datas[i].balls }
            columnArray.add(ballsColumn)

            val partitionArray = ArrayList<Column<*>>(5)
            repeat(5) { //5分区
                val partitionXSequence = it.inc()
                val column = Column<String>("${partitionXSequence}区", "")
//                column.countFormat = stringCountFormat
                column.datas = List(dataLen) { i ->
                    datas[i].redP.p5[it].toString()
                }
//                column.setOnColumnItemClickListener { column, value, t, position ->  }
                partitionArray.add(column)
            }

            val partitionParentColumn = Column<String>("前区五分区", partitionArray)
            columnArray.add(partitionParentColumn)

            val partitionType = Column<String>("区形态", "")
//            partitionType.countFormat = stringCountFormat
            partitionType.datas = List(dataLen) { datas[it].redP.p5t }
            columnArray.add(partitionType)

            //出号区的个数
            val oddColumn1 = Column<Int>("奇数区", "")
            oddColumn1.countFormat = intCountFormat
            oddColumn1.datas = List(dataLen) {
                arrayOf(1, 3, 5).count { i -> datas[it].redP.p5[i - 1] > 0 }
            }
            val evenColumn1 = Column<Int>("偶数区", "")
            evenColumn1.countFormat = intCountFormat
            evenColumn1.datas = List(dataLen) {
                arrayOf(2, 4).count { i -> datas[it].redP.p5[i - 1] > 0 }
            }
            val fromParentColumn = Column<Int>("出号区的个数", oddColumn1, evenColumn1)
            columnArray.add(fromParentColumn)

            //出2个号的区个数
            val oddColumn2 = Column<Int>("奇数区", "")
//            oddColumn1.isAutoCount = true
            oddColumn2.countFormat = intCountFormat
            oddColumn2.datas = List(dataLen) {
                arrayOf(1, 3, 5).count { i -> datas[it].redP.p5[i - 1] == 2 }
            }
            val evenColumn2 = Column<Int>("偶数区", "")
            evenColumn2.countFormat = intCountFormat
            evenColumn2.datas = List(dataLen) {
                arrayOf(2, 4).count { i -> datas[it].redP.p5[i - 1] == 2 }
            }
            val doubleParentColumn = Column<Int>("出2个号的区个数", oddColumn2, evenColumn2)
            columnArray.add(doubleParentColumn)

//            columnArray.forEach { it.isFast = true }

            //表格数据 datas是需要填充的数据
            val tableData = TableData("前区5分区统计分析表", datas, columnArray)

//            tableData.isShowCount = true
//            tableData.setOnItemClickListener { column, value, t, col, row ->
//                Log.d("-->", "column: ${column.fieldName} & col: $col & row: $row")
//            }

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
        buildTable { DataUtils.getLottery(num) }
    }

    private var initYearSpinner = true
    override fun onYearsItemSelected(parent: AdapterView<*>, position: Int) {
        if (initYearSpinner) {
            initYearSpinner = false
            return
        }

        val year = parent.getItemAtPosition(position).toString()
        buildTable { DataUtils.getLottery(year) }
    }
}