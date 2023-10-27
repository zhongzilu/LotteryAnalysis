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

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.ReleaseItem
import zilu.lottery.analysis.widget.ReleaseBallView2

/**
 *
 * Create by zilu 2023/09/06
 */
interface RVItemClickListener<VH : RecyclerView.ViewHolder> {
    fun onItemClickListener(holder: VH, v: View, position: Int)
}

/**
 * 主页彩票种类Grid适配器
 */
class HomeGridAdapter(private val items: List<HomeGridItem>) :
    RecyclerView.Adapter<HomeGridAdapter.VH>() {

    private var itemClickListener: RVItemClickListener<VH>? = null

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val iconView: ImageView = v.findViewById(R.id.itemIcon)
        val itemName: TextView = v.findViewById(R.id.itemName)
//        val itemBtn2: View = v.findViewById(R.id.itemBtn2)
    }

    class HomeGridItem(
        @JvmField val id: String,
        @JvmField val icon: Int,
        @JvmField val name: String
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_home_recycler, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.iconView.setImageResource(item.icon)
        val viewClickListener: ((View) -> Unit) = { v: View ->
            itemClickListener?.onItemClickListener(holder, v, position)
        }
        holder.itemName.text = item.name
        holder.itemView.setOnClickListener(viewClickListener)
//        holder.itemBtn2.setOnClickListener(viewClickListener)
    }

    override fun getItemCount(): Int = items.size

    fun setOnItemClickListener(listener: RVItemClickListener<VH>) = apply {
        this.itemClickListener = listener
    }

    fun getItemData(pos: Int) = items[pos]
}

/**
 * 开奖列表RecyclerView适配器
 */
class ReleaseRecyclerAdapter(private val items: MutableList<ReleaseItem>) :
    RecyclerView.Adapter<ReleaseRecyclerAdapter.VH>() {

    private var itemClickListener: RVItemClickListener<VH>? = null

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ctx = itemView.context.resources
        val iconView: ImageView = v.findViewById(R.id.itemIcon)
        val itemTitle: TextView = v.findViewById(R.id.itemTitle)
        val itemReleaseDate: TextView = v.findViewById(R.id.itemReleaseDate)
        val itemQiHao: TextView = v.findViewById(R.id.itemQiHao)
        val itemJackpot: TextView = v.findViewById(R.id.itemJackpot)
        val releaseBallView: ReleaseBallView2 = v.findViewById(R.id.releaseBallView)

        val itemBtn1: View = v.findViewById(R.id.itemBtn1)
        val itemBtn2: View = v.findViewById(R.id.itemBtn2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_release_recycler, parent, false)
        )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.iconView.setImageResource(item.iconRes)
        holder.itemTitle.text = item.title
        holder.itemReleaseDate.text = item.date
        holder.itemQiHao.text = item.qihao
        val jackpotDesc = holder.ctx.getString(R.string.format_release_jackpot, item.jackpot)
        holder.itemJackpot.text = SpannableStringBuilder(jackpotDesc).apply {
            val color = ForegroundColorSpan(holder.ctx.getColor(R.color.colorPrimary))
            val size = RelativeSizeSpan(1.5f)
            setSpan(color, 4, jackpotDesc.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(size, 4, jackpotDesc.lastIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        holder.releaseBallView.setFormatBallString(item.balls).show()
        val viewClickListener: ((View) -> Unit) = { v: View ->
            itemClickListener?.onItemClickListener(holder, v, position)
        }
        holder.itemView.setOnClickListener(viewClickListener)
        holder.itemBtn1.setOnClickListener(viewClickListener)
        holder.itemBtn2.setOnClickListener(viewClickListener)
    }

    override fun getItemCount(): Int = items.size

    fun setOnItemClickListener(listener: RVItemClickListener<VH>) = apply {
        this.itemClickListener = listener
    }

    fun getItemData(pos: Int) = items[pos]
}