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

package zilu.lottery.analysis.ui.lottery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import zilu.lottery.analysis.R
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.ui.partition.Partition4Fragment

class BlueBallFragment : BaseFragment(), TabLayout.OnTabSelectedListener {

    private lateinit var tabs: TabLayout
    private var currentIndex = 0
    private val KEY_INDEX = "index"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_blue_ball, container, false)
        tabs = root.findViewById(R.id.tabs)
        tabs.addOnTabSelectedListener(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentIndex = savedInstanceState?.getInt(KEY_INDEX) ?: 0
        tabs.selectTab(tabs.getTabAt(currentIndex))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, currentIndex)
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        currentIndex = tab.position
        showFragment(tab.position)
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        showFragment(tab.position)
    }

    private fun showFragment(index: Int) {
        val manager = childFragmentManager
        val transaction = manager.beginTransaction()
        val array = manager.fragments

        val fragment = manager.findFragmentByTag("bf$index")

        array.forEach { f ->
            if (f != null && f != fragment) transaction.hide(f)
        }

        if (fragment == null) {
            transaction.add(R.id.partitionContainer, getFragment(index), "bf$index")
        } else {
            transaction.show(fragment)
        }
        transaction.commit()
    }

    private fun getFragment(index: Int): Fragment = when (index) {
//        0 -> Partition4Fragment()
//        1 -> Partition7Fragment()
        else -> Partition4Fragment()
    }

}
