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

package zilu.lottery.analysis.ui.basic

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bin.david.form.core.SmartTable
import com.bin.david.form.data.column.Column
import com.bin.david.form.data.style.FontStyle
import com.bin.david.form.data.table.TableData
import com.bin.david.form.utils.DensityUtils
import org.jetbrains.anko.doAsync
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.data.Constants
import zilu.lottery.analysis.data.DataUtils
import zilu.lottery.analysis.table.BallColumnDrawFormat
import zilu.lottery.analysis.table.MyDLTBasicContentCellBackgroundFormat
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.widget.MyFilterSpinner
import zilu.lottery.annotation.Val

class BasicFragment : BaseFragment(), Observer<Int>, MyFilterSpinner.OnItemSelectedListener {

    private lateinit var basicViewModel: BasicViewModel
    private lateinit var table: SmartTable<Lottery>

    private val columnArray = ArrayList<Column<*>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        basicViewModel =
            ViewModelProviders.of(this).get(BasicViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_basic, container, false)
        root.findViewById<MyFilterSpinner>(R.id.filterSpinner)
            .setOnItemSelectedListener(this)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        basicViewModel.num.observe(viewLifecycleOwner, this)
        table = view.findViewById(R.id.table)

        initTableConfig()
    }

    private fun initTableConfig() {
        FontStyle.setDefaultTextSpSize(context, 10)
        val vPadding = DensityUtils.dp2px(activity, Val.V_PADDING)
        val hPadding = DensityUtils.dp2px(activity, Val.H_PADDING)
        table.config
            .setContentCellBackgroundFormat(MyDLTBasicContentCellBackgroundFormat())
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
            .setFixedYSequence(false)
            .setShowYSequence(false)
            .isShowTableTitle = false

        table.setZoom(true, 1f, .5f)
        table.setOnSmartTableInvalidateListener {
            initData = false
        }
    }

    private fun buildTable(run: (() -> List<Lottery>)) {
        doAsync {

            val datas = run.invoke()
            val dataLen = datas.size

            val qihaoColumn = Column<String>("期号", "")
            qihaoColumn.datas = List(dataLen) { i -> datas[i].id }
            qihaoColumn.isFixed = true
//            qihaoColumn.setOnColumnItemClickListener { column, value, t, position ->
//                Log.d("-->", position.toString())
//            }
            columnArray.clear()
            columnArray.add(qihaoColumn)

            val ballRadius = DensityUtils.dp2px(activity, 9f).toFloat()
            val otherTextColor: Int = 0xFFCCCCCC.toInt()
            repeat(Val.DLT_RED_BALL_SIZE) { i ->
                val redXSequence = i.inc()
                val column = Column<Int>(
                    redXSequence.toString(),
                    "",
                    BallColumnDrawFormat()
                        .setBallRadius(ballRadius)
                        .setOtherTextColor(otherTextColor)
                )
                var miss: Int
                column.datas = List(dataLen) { j ->
                    miss = datas[j].miss[i]
                    if (miss == 0) redXSequence else -miss
                }
                columnArray.add(column)
            }

            repeat(Val.DLT_BLUE_BALL_SIZE) { i ->
                val blueXSequence = i.inc()
                val column = Column<Int>(
                    blueXSequence.toString(),
                    "",
                    BallColumnDrawFormat()
                        .setBallColor(Constants.blueBallColor)
                        .setBallRadius(ballRadius)
                        .setOtherTextColor(otherTextColor)
                )
                var miss: Int
                column.datas = List(dataLen) { j ->
                    miss = datas[j].miss[i + Val.DLT_RED_BALL_SIZE]
                    if (miss == 0) blueXSequence else -miss
                }
                columnArray.add(column)
            }

            //表格数据 datas是需要填充的数据
            val tableData = TableData("基本走势图", datas, columnArray)

//            table.setSortColumn(qihaoColumn)
            table.tableData = tableData
        }
    }

    override fun onChanged(t: Int) {
        buildTable { DataUtils.getLottery(t) }
    }

    override fun onIssuesItemSelected(parent: AdapterView<*>, position: Int) {
        basicViewModel.num.value = when (position) {
            0 -> 30
            1 -> 50
            2 -> 100
            else -> -1
        }
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
