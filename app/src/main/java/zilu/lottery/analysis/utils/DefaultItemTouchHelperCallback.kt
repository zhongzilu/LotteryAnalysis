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

package zilu.lottery.analysis.utils

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 实现RecyclerView的ItemTouchHelper.Callback
 * Create by zilu 2024/01/06
 */
class DefaultItemTouchHelperCallback(private var callback: Callback?) : ItemTouchHelper.Callback() {

    /**
     * 是否可以拖拽，默认可以拖拽
     */
    private var isCanDrag = true

    /**
     * 是否可以被滑动，默认可以滑动
     */
    private var isCanSwipe = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val layoutManager = recyclerView.layoutManager

        var dragFlag = 0
        var swipeFlag = 0

        // 如果布局管理器是GridLayoutManager
        if (layoutManager is GridLayoutManager) {
            // flag如果值是0，相当于这个功能被关闭
            dragFlag =
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN

            // create make
            return makeMovementFlags(dragFlag, swipeFlag)
        }

        // 如果布局管理器是LinearLayoutManager
        if (layoutManager is LinearLayoutManager) {

            when (layoutManager.orientation) {
                RecyclerView.HORIZONTAL -> { // 如果是横向布局
                    swipeFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    dragFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                }

                RecyclerView.VERTICAL -> { // 如果是纵向布局
                    swipeFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                    dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }
            }

            return makeMovementFlags(dragFlag, swipeFlag)
        }

        return 0
    }

    /**
     * 当Item被拖拽的时候被回调
     * @param recyclerView RecyclerView
     * @param viewHolder 拖拽的ViewHolder
     * @param target 目的地的ViewHolder
     * @return
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return callback?.onMove(viewHolder.adapterPosition, target.adapterPosition) ?: false
    }

    /**
     * 当Item被滑动的时候回调
     * @param viewHolder 滑动的ViewHolder
     * @param direction 滑动方向
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        callback?.onSwiped(viewHolder, direction)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        callback?.clearView(viewHolder)
    }

    /**
     * 设置Item操作的回调，去更新UI和数据源
     * @param listener
     */
    fun setOnItemTouchCallbackListener(listener: Callback) = apply {
        this.callback = listener
    }

    /**
     * 设置是否可以被拖拽
     * @param canDrag true:是 false:否
     */
    fun setDragEnable(canDrag: Boolean) = apply {
        isCanDrag = canDrag
    }

    /**
     * 设置是否可以被滑动
     * @param canSwipe true:是 false:否
     */
    fun setSwipeEnable(canSwipe: Boolean) = apply {
        isCanSwipe = canSwipe
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     * @return
     */
    override fun isLongPressDragEnabled(): Boolean = isCanDrag

    /**
     * Item是否可以被滑动（H：左右滑动，V：上下滑动）
     * @return
     */
    override fun isItemViewSwipeEnabled(): Boolean = isCanSwipe

    interface Callback {

        /**
         * 当某个Item被滑动删除的时候
         * @param viewHolder viewHolder
         */
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)

        /**
         * 当两个Item位置互换的时候被回调
         * @param srcPosition       拖拽的item的position
         * @param targetPosition    目的地的item的position
         * @return 开发者处理了操作应该返回true,开发者没有处理就返回false
         */
        fun onMove(srcPosition: Int, targetPosition: Int): Boolean

        /**
         * 滑动删除清理视图
         * @param viewHolder ViewHolder
         */
        fun clearView(viewHolder: RecyclerView.ViewHolder)
    }
}

fun RecyclerView.itemTouchHelper(callback: DefaultItemTouchHelperCallback.Callback) =
    ItemTouchHelper(DefaultItemTouchHelperCallback(callback)).attachToRecyclerView(this)