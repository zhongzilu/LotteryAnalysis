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

import android.content.SharedPreferences
import android.preference.PreferenceManager
import zilu.lottery.annotation.SPKey
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * SharePreference操作类
 * Created by zilu on 2023/08/11
 */
object SP {

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(ApplicationContext.get())
    }

    private val editor: SharedPreferences.Editor = prefs.edit()

    /**
     * 是否首次安装应用启动，默认为false
     */
    var isInit: Boolean by prefs.preference(SPKey.INIT, true)

    var isUpdate: Boolean by prefs.preference(SPKey.UPDATE, true)

    /**
     * 自动更新时间，用于限制每日只需更新一次，避免多次启动应用更新被封
     */
    var autoUpdateDate: String by prefs.preference(SPKey.AUTO_UPDATE_DATE, "")

    @JvmStatic
    fun remove(key: String) = editor.remove(key).commit()
}

/**
 * SharePreference读写代理类
 * Created by zilu on 2023/08/11
 */
class Preference<T>(
    private val prefs: SharedPreferences,
    private val name: String,
    private val default: T
) :
    ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun <T> findPreference(name: String, default: T): T = with(prefs) {
        val res: Any? = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }

        res as T
    }

    private fun <U> putPreference(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Preference")
        }.apply()
    }
}

fun <T : Any> SharedPreferences.preference(name: String, default: T) =
    Preference(this, name, default)
