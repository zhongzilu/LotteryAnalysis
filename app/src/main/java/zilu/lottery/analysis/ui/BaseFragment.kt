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

package zilu.lottery.analysis.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 *
 * Create by zilu 2023/08/09
 */
abstract class BaseFragment : Fragment {

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    @JvmField
    protected var container: ViewGroup? = null

    @JvmField
    protected var initData = true

    @JvmField
    protected val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        activity?.onBackPressedDispatcher?.addCallback(this,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    onBackPressed(this)
//                }
//            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.container = container
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected open fun containerId(): Int = 0

    protected open fun replaceFragment(fragment: Fragment) {
        val containerId = containerId()
        if (containerId == 0) throw IllegalArgumentException("You must override the containerId() method.")
        replaceFragment(containerId, fragment)
    }

    protected open fun replaceFragment(
        containerId: Int,
        fragment: Fragment,
        backStack: Boolean = false
    ) {
        replaceFragment(containerId, fragment, fragment.javaClass.simpleName, backStack)
    }

    protected open fun replaceFragment(
        containerId: Int,
        fragment: Fragment,
        tag: String?,
        backStack: Boolean = false
    ) {
        val manager = requireActivity().supportFragmentManager
        val transaction = manager.beginTransaction()
        val f = manager.findFragmentByTag(tag)
        if (backStack && f == null) {
            transaction.setReorderingAllowed(true)
            transaction.replace(containerId, fragment, tag)
            transaction.addToBackStack(tag)
            transaction.commitAllowingStateLoss()
        } else {
            if (f == null) {
                transaction.replace(containerId, fragment, tag)
            } else {
                transaction.show(f)
            }
            transaction.commit()
        }
    }

    override fun onDestroy() {
        mainScope.cancel()
        super.onDestroy()
    }
}