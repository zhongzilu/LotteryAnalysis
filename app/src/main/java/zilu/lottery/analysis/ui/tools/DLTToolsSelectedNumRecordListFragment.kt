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

package zilu.lottery.analysis.ui.tools

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dlt_tools_select_record_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import zilu.lottery.analysis.R
import zilu.lottery.analysis.adapter.RVItemClickListener
import zilu.lottery.analysis.adapter.RecordListRecyclerAdapter
import zilu.lottery.analysis.bean.Record
import zilu.lottery.analysis.data.RecordTable
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.utils.DefaultItemTouchHelperCallback
import zilu.lottery.annotation.LotteryTypeDef
import zilu.lottery.annotation.Val
import kotlin.random.Random

/**
 * 大乐透投注优化 - 选号记录列表
 * Create by zilu 2023/12/19
 */
class DLTToolsSelectedNumRecordListFragment :
    BaseFragment(R.layout.fragment_dlt_tools_select_record_list),
    RVItemClickListener<RecordListRecyclerAdapter.VH> {

    private val recordList = ArrayList<Record>()
    private val recordRecyclerAdapter =
        RecordListRecyclerAdapter(recordList).setOnItemClickListener(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        choiceBySelfBtn.setOnClickListener {
            replaceFragment(R.id.toolsContentWrapper, DLTToolsFragment(), true)
        }
        randomBtn.setOnClickListener { generateRandomBalls() }
        random5Btn.setOnClickListener { generateRandomBalls(5) }
        swipeRefresh.setOnRefreshListener { initRecordListRecyclerViewData() }
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.blueBall)
        initRecordListRecyclerView()
    }

    private fun initRecordListRecyclerView() {
        with(recordListRecycler) {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = recordRecyclerAdapter
            val callback = object : DefaultItemTouchHelperCallback.Callback {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        AlertDialog.Builder(viewHolder.itemView.context)
                            .setCancelable(false)
                            .setTitle("删除提示")
                            .setMessage("是否删除该条记录？")
                            .setPositiveButton("确定") { dialog, _ ->
                                Log.d("-->", "deleted: ${viewHolder.itemId}")
                                RecordTable.deleteById(viewHolder.itemId.toInt())
                                recordRecyclerAdapter.removeItem(viewHolder.adapterPosition)
                                dialog.dismiss()
                            }
                            .setNegativeButton("取消") { dialog, _ ->
                                recordRecyclerAdapter.notifyItemChanged(viewHolder.adapterPosition)
                                dialog.dismiss()
                            }
                            .show()
                    } else if (direction == ItemTouchHelper.RIGHT) {
                        val record = recordList[viewHolder.adapterPosition]
                        val fragment = DLTToolsFragment()
                        fragment.arguments = Bundle().apply {
                            putParcelable(DLTToolsFragment.EXTRA_PARCELABLE_KEY, record)
                        }
                        replaceFragment(R.id.toolsContentWrapper, fragment, true)
                    }
                }

                override fun onMove(srcPosition: Int, targetPosition: Int): Boolean = true

                override fun clearView(viewHolder: RecyclerView.ViewHolder) {
                    if (recordRecyclerAdapter.itemCount == 0) {
                        noContentTip.isGone = false
                    }
                }
            }
            val defaultCallback = DefaultItemTouchHelperCallback(callback)
                .setDragEnable(false)
            ItemTouchHelper(defaultCallback).attachToRecyclerView(this)
        }
    }

    private fun generateRandomBalls(times: Int = 1) {
        if (times == 0 || times > 100) return
        val startPos = recordList.size
        repeat(times) { startRandomBalls() }
        recordRecyclerAdapter.notifyItemRangeChanged(startPos, times)
        noContentTip.isGone = true
    }

    /**
     * 随机产生一组号码
     */
    private fun startRandomBalls() {
        //random red balls
        val selectedRedBallList = ArrayList<String>(Val.DLT_RED_BALL_LIMIT)
        val selectedBlueBallList = ArrayList<String>(Val.DLT_BLUE_BALL_LIMIT)
        repeat(Val.DLT_RED_BALL_LIMIT) {
            do {
                val i = Random.nextInt(Val.DLT_RED_BALL_SIZE)
                val ballNum = i.inc().toString()
                val alreadyExist = selectedRedBallList.contains(ballNum)
                if (!alreadyExist) {
                    selectedRedBallList.add(ballNum)
                }
            } while (alreadyExist)
        }

        //random blue balls
        repeat(Val.DLT_BLUE_BALL_LIMIT) {
            do {
                val i = Random.nextInt(Val.DLT_BLUE_BALL_SIZE)
                val ballNum = i.inc().toString()
                val alreadyExist = selectedBlueBallList.contains(ballNum)
                if (!alreadyExist) {
                    selectedBlueBallList.add(ballNum)
                }
            } while (alreadyExist)
        }

        //sort the ball list
        selectedRedBallList.sortBy { it.toInt() }
        selectedBlueBallList.sortBy { it.toInt() }

        //update Ui
        val randomRecord = Record(
            selectedRedBallList.joinToString(" "),
            selectedBlueBallList.joinToString(" "),
            LotteryTypeDef.DLT
        )
        RecordTable.save(randomRecord)
        val latestRecord = RecordTable.findLatest()!!
        recordRecyclerAdapter.addItem(latestRecord)
    }

    override fun onResume() {
        super.onResume()
        initRecordListRecyclerViewData()
    }

    private fun initRecordListRecyclerViewData() {
        doAsync {
            val records = RecordTable.findByTmp(true)
            uiThread {
                if (records.isNotEmpty()) {
                    noContentTip.isGone = true
                    recordList.clear()
                    recordList.addAll(records)
                    recordRecyclerAdapter.notifyDataSetChanged()
                } else {
                    noContentTip.isGone = false
                }
                swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onItemClickListener(
        holder: RecordListRecyclerAdapter.VH,
        v: View,
        position: Int
    ) {
        val record = recordList[position]
        val fragment = DLTNumFilterConditionsFragment()
        fragment.arguments = Bundle().apply {
            putParcelable(DLTNumFilterConditionsFragment.EXTRA_PARCELABLE_KEY, record)
        }
        replaceFragment(R.id.toolsContentWrapper, fragment, true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.helpInfoMenu) {
            AlertDialog.Builder(requireContext())
                .setCancelable(true)
                .setTitle(R.string.title_help)
                .setMessage(R.string.msg_selected_num_record_help)
                .show()
        }
        return true
    }
}