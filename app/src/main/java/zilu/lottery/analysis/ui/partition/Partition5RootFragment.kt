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
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import zilu.lottery.analysis.R
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.ui.rp5.R5PAnalysisFragment
import zilu.lottery.analysis.ui.rp5.R5PTypeContinuationFragment
import zilu.lottery.analysis.ui.rp5.R5PTypeFragment
import zilu.lottery.analysis.widget.MyTabLayout

/**
 * 前区5分区父Fragment
 * Create by zilu 2023/07/31
 */
class Partition5RootFragment(private val name: String) : BaseFragment(),
    TabLayout.OnTabSelectedListener {

    constructor() : this("前区5分区父Fragment")

    private lateinit var tabs: MyTabLayout
    private var currentIndex = -1
    private val KEY_INDEX = "index"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_partitions_root, container, false)
        tabs = root.findViewById(R.id.tabs)
        tabs.setTabItems(R.array.r5p_analysis_tabs)
        tabs.addOnTabSelectedListener(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: -1
        if (currentIndex >= 0) {
            tabs.selectTab(tabs.getTabAt(currentIndex))
        } else {
            showFragment(currentIndex, name)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            currentIndex = -1
            showFragment(currentIndex, name)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, currentIndex)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        currentIndex = tab.position
        showFragment(tab.position, tab.text.toString())
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        showFragment(tab.position, tab.text.toString())
    }

    private fun showFragment(index: Int, name: String) {
        val manager = childFragmentManager
        val transaction = manager.beginTransaction()
        val fragment = manager.findFragmentByTag("r5f$index")
        if (fragment == null) {
            transaction.replace(R.id.partitionRootContainer, getFragment(index, name), "r5f$index")
            transaction.commit()
        }
    }

    private fun getFragment(index: Int, name: String): Fragment = when (index) {
        -1 -> Partition5MainFragment()
        0 -> R5PTypeFragment(name)
        1 -> R5PTypeContinuationFragment(name)
        else -> R5PAnalysisFragment(name)
    }

}