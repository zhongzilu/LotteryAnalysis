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

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_dlt_tools_select_num.*
import org.jetbrains.anko.toast
import zilu.lottery.analysis.R
import zilu.lottery.analysis.adapter.DLTBallToolRecyclerAdapter
import zilu.lottery.analysis.adapter.RVItemClickListener
import zilu.lottery.analysis.bean.Record
import zilu.lottery.analysis.data.RecordTable
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.annotation.LotteryTypeDef
import zilu.lottery.annotation.Val
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

/**
 * 大乐透投注优化 - 选号
 * Create by zilu 2023/12/19
 */
class DLTToolsFragment : BaseFragment(R.layout.fragment_dlt_tools_select_num),
    RVItemClickListener<DLTBallToolRecyclerAdapter.VH> {

    companion object {
        const val EXTRA_PARCELABLE_KEY = "record"
    }

    private val redBallRecyclerAdapter =
        DLTBallToolRecyclerAdapter(Val.DLT_RED_BALL_SIZE, Color.RED)
            .setOnItemClickListener(this)
    private val blueBallRecyclerAdapter =
        DLTBallToolRecyclerAdapter(Val.DLT_BLUE_BALL_SIZE, Color.BLUE)
            .setOnItemClickListener(this)

    private val selectedRedBallList = ArrayList<String>(Val.DLT_RED_BALL_SIZE)
    private val selectedBlueBallList = ArrayList<String>(Val.DLT_BLUE_BALL_SIZE)

    private var currentRecord: Record? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentRecord = arguments?.getParcelable(EXTRA_PARCELABLE_KEY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        redBallLabel.text = getString(R.string.format_tools_red_ball_label, Val.DLT_RED_BALL_LIMIT)
        blueBallLabel.text =
            getString(R.string.format_tools_blue_ball_label, Val.DLT_BLUE_BALL_LIMIT)

        allRedSelectBtn.setOnClickListener(this::allRedSelectOrCancel)
        allBlueSelectBtn.setOnClickListener(this::allBlueSelectOrCancel)
        randomBtn.setOnClickListener { startRandomBalls() }
        nextBtn.setOnClickListener(this::nextBtnClicked)

        restoreCurrentRecord()

        with(redballRecycler) {
            setHasFixedSize(true)
            adapter = redBallRecyclerAdapter
        }
        with(blueBallRecycler) {
            setHasFixedSize(true)
            adapter = blueBallRecyclerAdapter
        }

    }

    /**
     * 对已选择的选号记录进行现场恢复
     */
    private fun restoreCurrentRecord() {
        currentRecord?.run {
            selectedRedBallList.addAll(rballs.split(" "))
            selectedBlueBallList.addAll(bballs.split(" "))

            selectedRedBallList.forEach {
                redBallRecyclerAdapter.setItemChecked(it.toInt().dec(), true)
            }
            selectedBlueBallList.forEach {
                blueBallRecyclerAdapter.setItemChecked(it.toInt().dec(), true)
            }
        }
    }

    /**
     * 选好号码后先进行存储，然后进入下一步操作
     */
    private fun nextBtnClicked(v: View) {
        if (selectedRedBallList.size < Val.DLT_RED_BALL_LIMIT) {
            activity?.toast(redBallLabel.text.toString())
            return
        }
        if (selectedBlueBallList.size < Val.DLT_BLUE_BALL_LIMIT) {
            activity?.toast(blueBallLabel.text.toString())
            return
        }

        val record = currentRecord
        val selectedRedStr = selectedRedBallList.sortedBy { it.toInt() }.joinToString(" ")
        val selectedBlueStr = selectedBlueBallList.sortedBy { it.toInt() }.joinToString(" ")
        if (record == null) {
            RecordTable.save(
                Record(selectedRedStr, selectedBlueStr, LotteryTypeDef.DLT)
            )
        } else if (record.rballs != selectedRedStr || record.bballs != selectedBlueStr) {
            val newRecord = Record(
                record.id, selectedRedStr, selectedBlueStr,
                record.tmp, record.type, record.date,
                SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(Date())
            )
            RecordTable.update(newRecord)
        }

//        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(
//            DLTToolsSelectedNumRecordListFragment::class.java.simpleName
//        ) ?: DLTToolsSelectedNumRecordListFragment()
//        replaceFragment(R.id.toolsContentWrapper, fragment)
        requireActivity().onBackPressed()
    }

    /**
     * 所有蓝色球选中或全部取消选中状态
     */
    private fun allBlueSelectOrCancel(v: View) {
        selectedBlueBallList.clear()
        blueBallRecyclerAdapter.resetAllItemCheckState()
        if (v.tag == null) {
            repeat(Val.DLT_BLUE_BALL_SIZE) {
                selectedBlueBallList.add(it.inc().toString())
                blueBallRecyclerAdapter.setItemChecked(it, true)
            }
            v.tag = "nonsense"
            (v as TextView).text = "取消"
        } else {
            v.tag = null
            (v as TextView).text = "全选"
        }
        blueBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_BLUE_BALL_SIZE)
    }

    /**
     * 所有红色球选中或全部取消选中状态
     */
    private fun allRedSelectOrCancel(v: View) {
        selectedRedBallList.clear()
        redBallRecyclerAdapter.resetAllItemCheckState()
        if (v.tag == null) {
            //select all
            repeat(Val.DLT_RED_BALL_SIZE) {
                selectedRedBallList.add(it.inc().toString())
                redBallRecyclerAdapter.setItemChecked(it, true)
            }
            v.tag = "nonsense"
            (v as TextView).text = "取消"
        } else {
            //cancel all
            v.tag = null
            (v as TextView).text = "全选"
        }

        redBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_RED_BALL_SIZE)
    }

    override fun onItemClickListener(
        holder: DLTBallToolRecyclerAdapter.VH,
        v: View,
        position: Int
    ) {
        val itemIsChecked = holder.ballView.isChecked
        if (holder.itemViewType == Color.RED) {
            if (!itemIsChecked)
                selectedRedBallList.add(position.inc().toString())
            else
                selectedRedBallList.remove(position.inc().toString())
            redBallRecyclerAdapter.notifyItemChecked(position, !itemIsChecked)
        } else {
            if (!itemIsChecked)
                selectedBlueBallList.add(position.inc().toString())
            else
                selectedBlueBallList.remove(position.inc().toString())
            blueBallRecyclerAdapter.notifyItemChecked(position, !itemIsChecked)
        }
    }

    /**
     * 随机产生一组号码
     */
    private fun startRandomBalls() {
        selectedRedBallList.clear()
        selectedBlueBallList.clear()
        redBallRecyclerAdapter.resetAllItemCheckState()
        blueBallRecyclerAdapter.resetAllItemCheckState()

        //random red balls
        repeat(Val.DLT_RED_BALL_LIMIT) {
            do {
                val i = Random.nextInt(Val.DLT_RED_BALL_SIZE)
                val ballNum = i.inc().toString()
                val alreadyExist = selectedRedBallList.contains(ballNum)
                if (!alreadyExist) {
                    selectedRedBallList.add(ballNum)
                    redBallRecyclerAdapter.setItemChecked(i, true)
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
                    blueBallRecyclerAdapter.setItemChecked(i, true)
                }
            } while (alreadyExist)
        }

        //sort the ball list
        selectedRedBallList.sortBy { it.toInt() }
        selectedBlueBallList.sortBy { it.toInt() }

        //update Ui
        redBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_RED_BALL_SIZE)
        blueBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_BLUE_BALL_SIZE)
    }

//    override fun replaceFragment(containerId: Int, fragment: Fragment) {
//        val manager = requireActivity().supportFragmentManager
//        val transaction = manager.beginTransaction()
//        val tag = fragment.id.toString()
//        val f = manager.findFragmentByTag(tag)
//        if (f != null) {
//            transaction.replace(containerId, fragment, tag)
//            transaction.commit()
//        } else {
//            transaction.setReorderingAllowed(true)
//            transaction.replace(containerId, fragment, tag)
//            transaction.addToBackStack(tag)
//            transaction.commitAllowingStateLoss()
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_help_info, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.helpInfoMenu) {
            AlertDialog.Builder(requireContext())
                .setCancelable(true)
                .setTitle(R.string.title_help)
//                .setMessage(R.string.msg_selected_num_record_help)
                .show()
        }
        return true
    }
}