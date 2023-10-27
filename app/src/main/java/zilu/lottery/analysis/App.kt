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

package zilu.lottery.analysis

import android.app.Application
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import org.jetbrains.anko.doAsync

/**
 *
 * Create by zilu 2023/08/10
 */
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        startService(Intent(base, UpdateDataService::class.java))
    }

    override fun onCreate() {
        super.onCreate()
        deleteOldApk()
    }

    private fun deleteOldApk() {
        doAsync {
            getExternalFilesDir("")?.listFiles { file ->
                file.name.endsWith(".apk")
            }?.forEach { it.delete() }
        }
    }
}

val gson: Gson by lazy { Gson() }

//fun View.visible() {
//    visibility = View.VISIBLE
//}
//
//fun View.gone() {
//    visibility = View.GONE
//}
//
//fun View.invisible() {
//    visibility = View.INVISIBLE
//}