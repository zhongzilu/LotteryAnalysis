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

package zilu.lottery.analysis.ui.release

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import zilu.lottery.analysis.R
import zilu.lottery.analysis.activity.LotteryActivity
import zilu.lottery.analysis.adapter.RVItemClickListener
import zilu.lottery.analysis.adapter.ReleaseRecyclerAdapter
import zilu.lottery.analysis.bean.ReleaseItem
import zilu.lottery.analysis.data.LotteryTable
import zilu.lottery.analysis.data.SSQTable
import zilu.lottery.analysis.ui.BaseFragment
import java.text.NumberFormat
import java.util.*

class ReleaseFragment : BaseFragment(), RVItemClickListener<ReleaseRecyclerAdapter.VH>,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var mAdapter: ReleaseRecyclerAdapter
    private lateinit var mRefresh: SwipeRefreshLayout
    private val items = ArrayList<ReleaseItem>(5)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_release, container, false)
        root.findViewById<TextView>(R.id.text_release).text = "全国开奖"
        mRefresh = root.findViewById(R.id.releaseRefresh)
        mRefresh.setOnRefreshListener(this)
        mRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.blueBall)
        val releaseRecycler = root.findViewById<RecyclerView>(R.id.releaseRecycler)
        initReleaseRecycler(releaseRecycler)
        refreshData()
        return root
    }

    private fun initReleaseRecycler(releaseRecycler: RecyclerView) {
        releaseRecycler.setHasFixedSize(true)
//        val items = MutableList(5) {
//            ReleaseItem(
//                R.mipmap.ic_lottery,
//                "大乐透",
//                "周二、四、日 21：15",
//                "第0000000期 09-08(五)",
//                "累计00.00亿",
//                "01 02 03 04 32+09 ${it + 10}"
//            )
//        }
        mAdapter = ReleaseRecyclerAdapter(items)
        releaseRecycler.adapter = mAdapter.setOnItemClickListener(this)
    }

    private fun refreshData(callback: (() -> Unit)? = null) {
        doAsync {
            items.clear()
            LotteryTable.findLatest()?.run {
                val jackpot = NumberFormat.getNumberInstance().parse(jackpot)!!.toLong()
                val format = String.format("%.2f", jackpot / 100000000f)
                items.add(
                    ReleaseItem(
                        R.mipmap.ic_lottery,
                        "大乐透",
                        "每周一、三、六 21:25开奖",
                        "第${id}期 $date",
                        format,
                        balls
                    )
                )
            }

            SSQTable.findLatest()?.run {
                val jackpot = NumberFormat.getNumberInstance().parse(jackpot)!!.toLong()
                val format = String.format("%.2f", jackpot / 100000000f)
                items.add(
                    ReleaseItem(
                        R.mipmap.ic_ssq,
                        "双色球",
                        "每周二、四、日 21:15开奖",
                        "第${id}期 $date",
                        format,
                        balls
                    )
                )
            }

            uiThread {
                mAdapter.notifyDataSetChanged()
                callback?.invoke()
            }
        }
    }

    override fun onItemClickListener(holder: ReleaseRecyclerAdapter.VH, v: View, position: Int) {
        when (v) {
            holder.itemBtn1,
            holder.itemBtn2 -> Unit
            else -> goDataAnalysis(mAdapter.getItemData(position).iconRes)
        }
    }

    private fun goDataAnalysis(iconRes: Int) = when (iconRes) {
        R.mipmap.ic_lottery -> startActivity(Intent(activity, LotteryActivity::class.java))
        else -> Unit
    }

    override fun onRefresh() {
        refreshData {
            toast("刷新成功")
            mRefresh.isRefreshing = false
        }
    }

    private fun toast(msg: String) {
        val toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}
