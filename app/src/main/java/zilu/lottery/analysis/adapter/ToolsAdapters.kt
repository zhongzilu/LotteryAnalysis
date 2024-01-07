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

package zilu.lottery.analysis.adapter

import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.recyclerview.widget.RecyclerView
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.Record
import zilu.lottery.analysis.widget.CircleBallView

/**
 * @param num 指定产生多少个数字球
 * @param textColor 指定数字球的文字颜色，同时也作为区分数字球的颜色类型
 * Create by zilu 2023/12/20
 */
class DLTBallToolRecyclerAdapter(private val num: Int, @BallColor private val textColor: Int) :
    RecyclerView.Adapter<DLTBallToolRecyclerAdapter.VH>() {

    private var itemClickListener: RVItemClickListener<VH>? = null
    private var selectedItem = SparseBooleanArray(num)

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [Color.RED, Color.BLUE])
    annotation class BallColor

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ballView: CircleBallView = v as CircleBallView

        init {
            val selectedDrawable =
                if (textColor == Color.RED) R.drawable.red_ball_big_bg else R.drawable.blue_ball_big_bg
            ballView.setSelectedDrawableResource(selectedDrawable)
            ballView.setTextColor(textColor)
        }
    }

    override fun getItemCount(): Int = num

    override fun getItemViewType(position: Int): Int = textColor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_ball_tool, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        with(holder.ballView) {
            text = String.format("%02d", position.inc())
            isChecked = selectedItem[position]
            setOnClickListener {
                selectedItem.put(position, isChecked)
                itemClickListener?.onItemClickListener(holder, this, position)
            }
        }
    }

    fun setOnItemClickListener(listener: RVItemClickListener<VH>) = apply {
        this.itemClickListener = listener
    }

    fun resetAllItemCheckState() {
        selectedItem.clear()
    }

    fun setItemChecked(position: Int, checked: Boolean) {
        selectedItem.put(position, checked)
    }

    fun notifyItemChecked(position: Int, checked: Boolean) {
        selectedItem.put(position, checked)
        notifyItemRangeChanged(position, num - position)
    }
}

/**
 * 选号历史记录RecyclerView Adapter
 */
class RecordListRecyclerAdapter(private val records: MutableList<Record>) :
    RecyclerView.Adapter<RecordListRecyclerAdapter.VH>() {

    init {
        setHasStableIds(true)
    }

    private var itemClickListener: RVItemClickListener<VH>? = null

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val selectedRedBallsText: TextView = v.findViewById(R.id.selectedRedBallsText)
        val selectedBlueBallsText: TextView = v.findViewById(R.id.selectedBlueBallsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_record_recycler, parent, false)
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val record = records[position]
        holder.selectedRedBallsText.text = record.rballs
        holder.selectedBlueBallsText.text = record.bballs
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClickListener(holder, it, position)
        }
    }

    override fun getItemCount(): Int = records.size

    override fun getItemId(position: Int): Long = records[position].id.toLong()

    fun setOnItemClickListener(listener: RVItemClickListener<VH>) = apply {
        this.itemClickListener = listener
    }

    fun removeItem(position: Int) {
        records.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    fun addItem(record: Record) {
        addItem(records.size, record)
    }

    fun addItem(position: Int, record: Record) {
        val size = records.size
        if (position >= size) {
            records.add(record)
            notifyItemInserted(size)
            notifyItemRangeChanged(0, size)
        } else {
            records.add(position, record)
            notifyItemInserted(position)
            notifyItemChanged(position)
        }
    }
}