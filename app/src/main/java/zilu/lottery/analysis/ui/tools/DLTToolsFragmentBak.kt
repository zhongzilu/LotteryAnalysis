///*
// *
// *        ___                       ___       ___
// *       /\  \          ___        /\__\     /\__\
// *       \:\  \        /\  \      /:/  /    /:/  /
// *        \:\  \       \:\  \    /:/  /    /:/  /
// *         \:\  \      /::\__\  /:/  /    /:/  /  ___
// *   _______\:\__\  __/:/\/__/ /:/__/    /:/__/  /\__\
// *   \::::::::/__/ /\/:/  /    \:\  \    \:\  \ /:/  /
// *    \:\~~\~~     \::/__/      \:\  \    \:\  /:/  /
// *     \:\  \       \:\__\       \:\  \    \:\/:/  /
// *      \:\__\       \/__/        \:\__\    \::/  /
// *       \/__/                     \/__/     \/__/
// *
// *  Copyright (C) 2019 ZiLu https://github.com/zhongzilu
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *       http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//package zilu.lottery.analysis.ui.tools
//
//import android.graphics.Color
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import kotlinx.android.synthetic.main.fragment_dlt_tools_select_num.*
//import org.jetbrains.anko.toast
//import zilu.lottery.analysis.R
//import zilu.lottery.analysis.adapter.DLTBallToolRecyclerAdapter
//import zilu.lottery.analysis.adapter.RVItemClickListener
//import zilu.lottery.analysis.ui.BaseFragment
//import zilu.lottery.annotation.Val
//import java.util.*
//import kotlin.collections.ArrayList
//import kotlin.random.Random
//
///**
// * 大乐透投注优化 - 选号
// * Create by zilu 2023/12/19
// */
//class LotteryToolsFragmentBak : BaseFragment(), RVItemClickListener<DLTBallToolRecyclerAdapter.VH> {
//
//    private val redBallRecyclerAdapter =
//        DLTBallToolRecyclerAdapter(Val.DLT_RED_BALL_SIZE, Color.RED)
//    private val blueBallRecyclerAdapter =
//        DLTBallToolRecyclerAdapter(Val.DLT_BLUE_BALL_SIZE, Color.BLUE)
//
//    private val selectedRedBallList = ArrayList<String>(Val.DLT_RED_BALL_LIMIT)
//    private val selectedBlueBallList = ArrayList<String>(Val.DLT_BLUE_BALL_LIMIT)
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_dlt_tools_select_num, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        redBallLabel.text = getString(R.string.format_tools_red_ball_label, Val.DLT_RED_BALL_LIMIT)
//        blueBallLabel.text =
//            getString(R.string.format_tools_blue_ball_label, Val.DLT_BLUE_BALL_LIMIT)
//        selectedBallsView.setRedBalls(selectedRedBallList)
//        selectedBallsView.setBlueBalls(selectedBlueBallList)
//        selectedBallsView.show()
//
//        allRedSelectBtn.setOnClickListener(this::allRedSelectOrCancel)
//        allBlueSelectBtn.setOnClickListener(this::allBlueSelectOrCancel)
//        randomBtn.setOnClickListener { startRandomBalls() }
//        nextBtn.setOnClickListener {
//            if (selectedRedBallList.size < Val.DLT_RED_BALL_LIMIT
//                || selectedBlueBallList.size < Val.DLT_BLUE_BALL_LIMIT
//            ) {
//                activity?.toast("请先选择号码后再进行下一步")
//                return@setOnClickListener
//            }
//            val fragment = LotteryToolsSelectedNumRecordListFragment()
//            fragment.arguments = Bundle().apply {
//                val string = selectedRedBallList.joinToString(" ") + "+" +
//                        selectedBlueBallList.joinToString(" ")
//                putString(
//                    LotteryToolsSelectedNumRecordListFragment.EXTRA_BALLS_FORMAT_STRING,
//                    string
//                )
//            }
//            replaceFragment(R.id.toolsContentWrapper, fragment)
//        }
//
//        with(redballRecycler) {
//            setHasFixedSize(true)
//            adapter = redBallRecyclerAdapter.setOnItemClickListener(this@LotteryToolsFragmentBak)
//        }
//        with(blueBallRecycler) {
//            setHasFixedSize(true)
//            adapter = blueBallRecyclerAdapter.setOnItemClickListener(this@LotteryToolsFragmentBak)
//        }
//    }
//
//    private fun allBlueSelectOrCancel(v: View) {
//        TODO("Not yet implemented")
//    }
//
//    private fun allRedSelectOrCancel(v: View) {
//        selectedRedBallList.clear()
//        redBallRecyclerAdapter.resetAllItemCheckState()
//        if (v.tag == null) {
//            //select all
//            selectedRedBallList.addAll(List(Val.DLT_RED_BALL_SIZE) { it.inc().toString() })
//            v.tag = "selected all"
//        } else {
//            //cancel all
//            selectedRedBallList.clear()
//            v.tag = null
//        }
//
//        redBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_RED_BALL_SIZE)
//    }
//
//    override fun onItemClickListener(
//        holder: DLTBallToolRecyclerAdapter.VH,
//        v: View,
//        position: Int
//    ) {
//        val itemIsChecked = holder.ballView.isChecked
//        if (holder.itemViewType == Color.RED) {
//            if (selectedRedBallList.size < Val.DLT_RED_BALL_LIMIT) {
//                if (!itemIsChecked)
//                    selectedRedBallList.add(position.inc().toString())
//                else
//                    selectedRedBallList.remove(position.inc().toString())
//                redBallRecyclerAdapter.notifyItemChecked(position, !itemIsChecked)
//            } else if (itemIsChecked) {
//                selectedRedBallList.remove(position.inc().toString())
//                redBallRecyclerAdapter.notifyItemChecked(position, false)
//            }
//        } else {
//            if (selectedBlueBallList.size < Val.DLT_BLUE_BALL_LIMIT) {
//                if (!itemIsChecked)
//                    selectedBlueBallList.add(position.inc().toString())
//                else
//                    selectedBlueBallList.remove(position.inc().toString())
//                blueBallRecyclerAdapter.notifyItemChecked(position, !itemIsChecked)
//            } else if (itemIsChecked) {
//                selectedBlueBallList.remove(position.inc().toString())
//                blueBallRecyclerAdapter.notifyItemChecked(position, false)
//            }
//        }
//
//        selectedBallsView.show()
//    }
//
//    private fun startRandomBalls() {
//        selectedRedBallList.clear()
//        selectedBlueBallList.clear()
//        redBallRecyclerAdapter.resetAllItemCheckState()
//        blueBallRecyclerAdapter.resetAllItemCheckState()
//
//        //random red balls
//        repeat(Val.DLT_RED_BALL_LIMIT) {
//            do {
//                val i = Random.nextInt(Val.DLT_RED_BALL_SIZE)
//                val ballNum = i.inc().toString()
//                val alreadyExist = selectedRedBallList.contains(ballNum)
//                if (!alreadyExist) {
//                    selectedRedBallList.add(ballNum)
//                    redBallRecyclerAdapter.setItemChecked(i, true)
//                }
//            } while (alreadyExist)
//        }
//
//        //random blue balls
//        repeat(Val.DLT_BLUE_BALL_LIMIT) {
//            do {
//                val i = Random.nextInt(Val.DLT_BLUE_BALL_SIZE)
//                val ballNum = i.inc().toString()
//                val alreadyExist = selectedBlueBallList.contains(ballNum)
//                if (!alreadyExist) {
//                    selectedBlueBallList.add(ballNum)
//                    blueBallRecyclerAdapter.setItemChecked(i, true)
//                }
//            } while (alreadyExist)
//        }
//
//        //sort the ball list
//        selectedRedBallList.sortBy { it.toInt() }
//        selectedBlueBallList.sortBy { it.toInt() }
//
//        //update Ui
//        redBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_RED_BALL_SIZE)
//        blueBallRecyclerAdapter.notifyItemRangeChanged(0, Val.DLT_BLUE_BALL_SIZE)
//        selectedBallsView.show()
//    }
//}