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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import kotlinx.android.synthetic.main.fragment_dlt_tool_num_filter_conditions.*
import org.jetbrains.anko.toast
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Record
import zilu.lottery.analysis.ui.BaseFragment

/**
 * 大乐透投注优化删选条件选择Fragment
 * Create by zilu 2024/01/07
 */
class DLTNumFilterConditionsFragment :
    BaseFragment(R.layout.fragment_dlt_tool_num_filter_conditions) {

    companion object {
        const val EXTRA_PARCELABLE_KEY = "record"
    }

    private var currentRecord: Record? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentRecord = arguments?.getParcelable(EXTRA_PARCELABLE_KEY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        step1RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rpRadio) {
                //选择前区
                rpRadioGroup.let {
                    it.isEnabled = true
                    it.isGone = false
                }
                bpRadioGroup.isGone = true
            } else {
                //后区
                bpRadioGroup.let {
                    it.isEnabled = true
                    it.isGone = false
                }
                rpRadioGroup.isGone = true
            }
        }

        rpRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            partitionTypeBtn.isEnabled = true
        }
        bpRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            partitionTypeBtn.isEnabled = true
        }
        partitionTypeBtn.setOnClickListener {
            requireActivity().toast(R.string.msg_developing)
        }
        nextBtn.setOnClickListener {
            requireActivity().toast(R.string.msg_developing)
        }
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
//                .setMessage(R.string.msg_selected_num_record_help)
                .show()
        }
        return true
    }
}