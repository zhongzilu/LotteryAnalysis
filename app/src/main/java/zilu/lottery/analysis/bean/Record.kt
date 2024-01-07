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

package zilu.lottery.analysis.bean

import android.os.Parcel
import android.os.Parcelable
import zilu.lottery.analysis.gson
import java.text.SimpleDateFormat
import java.util.*

/**
 * 选号记录实体类
 * Create by zilu 2024/01/05
 */
class Record : Parcelable {

    @JvmField
    val id: Int

    @JvmField
    val rballs: String

    @JvmField
    val bballs: String

    @JvmField
    val tmp: Boolean

    @JvmField
    val type: String

    @JvmField
    val date: String

    @JvmField
    val update: String

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    constructor(
        id: Int,
        rballs: String,
        bballs: String,
        tmp: Boolean,
        type: String,
        date: String,
        update: String
    ) {
        this.id = id
        this.rballs = rballs
        this.bballs = bballs
        this.tmp = tmp
        this.type = type
        this.date = date
        this.update = update
    }

    constructor(rballs: String, bballs: String, type: String) {
        this.id = 0
        this.rballs = rballs
        this.bballs = bballs
        this.tmp = true
        this.type = type
        val date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(Date())
        this.date = date
        this.update = date
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(rballs)
        parcel.writeString(bballs)
        parcel.writeByte(if (tmp) 1 else 0)
        parcel.writeString(type)
        parcel.writeString(date)
        parcel.writeString(update)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String = gson.toJson(this)
}