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

package zilu.lottery.analysis.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stx.xhb.androidx.XBanner
import zilu.lottery.analysis.R
import zilu.lottery.analysis.activity.LotteryActivity
import zilu.lottery.analysis.activity.ToolsActivity
import zilu.lottery.analysis.adapter.HomeGridAdapter
import zilu.lottery.analysis.adapter.RVItemClickListener
import zilu.lottery.analysis.bean.BannerItem
import zilu.lottery.analysis.ui.BaseFragment
import zilu.lottery.analysis.ui.lottery.LotteryViewModel

class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: LotteryViewModel
    private lateinit var mHomeAdapter: HomeGridAdapter
    private lateinit var mHomeToolAdapter: HomeGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(LotteryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val banner = root.findViewById<XBanner>(R.id.xbanner)
        initBanner(banner)
        val homeRecycler = root.findViewById<RecyclerView>(R.id.homeRecycler)
        initHomeRecycler(homeRecycler)
        val homeToolRecycler = root.findViewById<RecyclerView>(R.id.homeToolRecycler)
        initHomeToolRecycler(homeToolRecycler)
        return root
    }

    private fun initHomeToolRecycler(homeToolRecycler: RecyclerView) {
        val items = listOf(
            HomeGridAdapter.HomeGridItem("tool1", R.mipmap.ic_tool1, "投注优化")
        )
        mHomeToolAdapter = HomeGridAdapter(items).setOnItemClickListener(homeToolRecyclerListener)
        homeToolRecycler.setHasFixedSize(true)
        homeToolRecycler.adapter = mHomeToolAdapter

    }

    private fun initHomeRecycler(homeRecycler: RecyclerView) {
        val items = listOf(
            HomeGridAdapter.HomeGridItem("lt", R.mipmap.ic_lottery, "大乐透"),
            HomeGridAdapter.HomeGridItem("ssq", R.mipmap.ic_ssq, "双色球"),
            HomeGridAdapter.HomeGridItem("r3", R.mipmap.ic_rank3, "排列三"),
            HomeGridAdapter.HomeGridItem("r5", R.mipmap.ic_rank5, "排列五"),
            HomeGridAdapter.HomeGridItem("3d", R.mipmap.ic_3d, "福彩3D"),
        )
        mHomeAdapter = HomeGridAdapter(items).setOnItemClickListener(homeRecyclerListener)
        homeRecycler.setHasFixedSize(true)
        homeRecycler.adapter = mHomeAdapter
    }

    private fun initBanner(banner: XBanner) {
        banner.loadImage { _, model, view, _ ->
            Glide.with(this).load((model as BannerItem).url).into((view as ImageView))
        }
        banner.setBannerData(
            arrayListOf(
                BannerItem(
                    "title1",
                    "https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF"
                ),
                BannerItem("title2", "https://t7.baidu.com/it/u=1819248061,230866778&fm=193&f=GIF"),
                BannerItem(
                    "title3",
                    "https://t7.baidu.com/it/u=2621658848,3952322712&fm=193&f=GIF"
                ),
                BannerItem(
                    "title4",
                    "https://t7.baidu.com/it/u=4162611394,4275913936&fm=193&f=GIF"
                ),
            )
        )
    }

    private val homeRecyclerListener = object : RVItemClickListener<HomeGridAdapter.VH> {
        override fun onItemClickListener(holder: HomeGridAdapter.VH, v: View, position: Int) {
            when (mHomeAdapter.getItemData(position).id) {
                "lt" -> startActivity(Intent(activity, LotteryActivity::class.java))
                else -> Unit
            }
        }
    }

    private val homeToolRecyclerListener = object : RVItemClickListener<HomeGridAdapter.VH> {
        override fun onItemClickListener(holder: HomeGridAdapter.VH, v: View, position: Int) {
            when (mHomeToolAdapter.getItemData(position).id) {
                "tool1" -> startActivity(Intent(activity, ToolsActivity::class.java))
                else -> Unit
            }
        }
    }
}
