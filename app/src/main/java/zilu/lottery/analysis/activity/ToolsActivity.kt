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

package zilu.lottery.analysis.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import zilu.lottery.analysis.R
import zilu.lottery.analysis.ui.tools.DLTToolsSelectedNumRecordListFragment
import zilu.lottery.annotation.LotteryTypeDef

/**
 * 投注优化工具主页面
 * Create by zilu 2023/08/19
 */
class ToolsActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_TOOL_TAG = "tag"
    }

    private var toolsTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        toolsTitle = findViewById<TextView>(R.id.toolsTitle)
        toolsTitle?.setOnClickListener(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val tag = intent.getStringExtra(EXTRA_TOOL_TAG) ?: LotteryTypeDef.DLT
        val fragment = getFragment(tag)
        if (fragment != null) showFragment(fragment)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        toolsTitle?.text = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.toolsContentWrapper, fragment, fragment.javaClass.simpleName)
        transaction.commit()
    }

    private fun getFragment(tag: String): Fragment? = when (tag) {
        LotteryTypeDef.DLT -> DLTToolsSelectedNumRecordListFragment()
        else -> null
    }

    override fun onClick(v: View) {
        if (v.id == R.id.toolsTitle) {
            //TODO - change tools Fragment by drop down menu
        }
    }

    override fun onBackPressed() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0)
            manager.popBackStack()
        else super.onBackPressed()
    }
}