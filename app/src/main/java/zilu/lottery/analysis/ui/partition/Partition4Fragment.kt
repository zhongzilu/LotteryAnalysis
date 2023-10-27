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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.bin.david.form.core.SmartTable
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Lottery
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.widget.MyFilterSpinner

/**
 * 前区分区(五分区、七分区)
 * Create by zilu 2023/07/31
 */
class Partition4Fragment : BaseFragment(), MyFilterSpinner.OnItemSelectedListener {

    //    private lateinit var basicViewModel: BasicViewModel
    private lateinit var table: SmartTable<Lottery>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
//        basicViewModel =
//            ViewModelProviders.of(this).get(BasicViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_partitions_main, container, false)
        table = view.findViewById(R.id.table)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
//        basicViewModel.num.observe(viewLifecycleOwner, this::buildTable)

        view.findViewById<MyFilterSpinner>(R.id.filterSpinner)
            .setOnItemSelectedListener(this)

    }

    override fun onIssuesItemSelected(parent: AdapterView<*>, position: Int) {
    }

    private var initYearSpinner = true
    override fun onYearsItemSelected(parent: AdapterView<*>, position: Int) {
        if (initYearSpinner) {
            initYearSpinner = false
            return
        }
    }
}