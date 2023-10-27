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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import zilu.lottery.analysis.R
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.ui.basic.BasicFragment

class LotteryFragment : BaseFragment(), TabLayout.OnTabSelectedListener {

    private lateinit var lotteryViewModel: LotteryViewModel
    private lateinit var tabs: TabLayout
    private var currentIndex = 0
    private val KEY_INDEX = "index"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lotteryViewModel =
            ViewModelProviders.of(this).get(LotteryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lottery, container, false)
        tabs = root.findViewById(R.id.tabs)
        tabs.addOnTabSelectedListener(this)
        val toolbar = root.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
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

        val fragment = manager.findFragmentByTag("hf$index")

        array.forEach { f ->
            if (f != null && f != fragment) transaction.hide(f)
        }

        if (fragment == null) {
            transaction.add(R.id.homeContainer, getFragment(index), "hf$index")
        } else {
            transaction.show(fragment)
        }
        transaction.commit()
    }

    private fun getFragment(index: Int): Fragment = when (index) {
        0 -> BasicFragment()
        1 -> RedBallFragment()
        else -> BlueBallFragment()
    }

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        Log.i(
//            "-->",
//            "newConfig.screenHeightDp: ${newConfig.screenHeightDp}, newConfig.screenWidthDp: ${newConfig.screenWidthDp}"
//        )
//    }
}
