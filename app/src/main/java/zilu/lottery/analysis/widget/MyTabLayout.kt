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

package zilu.lottery.analysis.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.IntDef
import com.google.android.material.tabs.TabLayout
import zilu.lottery.analysis.R

/**
 * Create by zilu 2023/08/31
 */
class MyTabLayout : TabLayout {

    companion object {
        const val SELECT_MODE_NONE = 0
        const val SELECT_MODE_SELECTED = 1
    }

    private var selectPos = 0
    private var selectMode = SELECT_MODE_SELECTED

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MyTabLayout)
        selectPos = a.getInt(R.styleable.MyTabLayout_tabSelectPosition, 0)
        selectMode = a.getInt(R.styleable.MyTabLayout_tabSelectMode, SELECT_MODE_SELECTED)
        if (selectMode == SELECT_MODE_NONE) {
            selectPos = -1
        }
        val items = a.getTextArray(R.styleable.MyTabLayout_tabItems)
        if (items != null) {
            var tab: Tab
            items.forEachIndexed { i, item ->
                tab = newTab()
                tab.text = item
                addTab(tab, i == selectPos)
            }
        }
        a.recycle()
    }

    fun setTabItems(resId: Int): MyTabLayout {
        this.setTabItems(resources.getStringArray(resId))
        return this
    }

    fun setTabItems(items: Array<String>): MyTabLayout {
        removeAllTabs()
        var tab: Tab
        items.forEachIndexed { i, item ->
            tab = newTab()
            tab.text = item
            addTab(tab, (selectMode == SELECT_MODE_SELECTED) && (i == selectPos))
        }
        return this
    }

    fun setSelectPosition(pos: Int): MyTabLayout {
        if (pos >= 0) {
            this.selectPos = pos
        }
        return this
    }

    fun setSelectMode(@SelectMod mod: Int): MyTabLayout {
        this.selectMode = mod
        if (mod == SELECT_MODE_NONE) {
            this.selectPos = -1
        }
        return this
    }

    @IntDef(value = [SELECT_MODE_NONE, SELECT_MODE_SELECTED])
    annotation class SelectMod
}