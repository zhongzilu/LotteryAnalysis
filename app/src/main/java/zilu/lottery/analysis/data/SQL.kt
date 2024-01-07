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

package zilu.lottery.analysis.data

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import zilu.lottery.analysis.bean.*
import zilu.lottery.analysis.utils.ApplicationContext
import zilu.lottery.annotation.*

/**
 * 数据库操作类
 * Create by zilu 2023/08/09
 */
private class SQL(ctx: Context) : ManagedSQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "data.db"
        const val DB_VERSION = 12

        @JvmStatic
        val instance: SQL by lazy { SQL(ApplicationContext.get()) }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) return

        //创建Lottery数据库表
        db.createTable(
            LotteryTableDef.NAME, true,
            LotteryTableDef.ID to TEXT + PRIMARY_KEY,
            LotteryTableDef.BALLS to TEXT,
            LotteryTableDef.MISS to TEXT,
            LotteryTableDef.RED_BALLS to TEXT,
            LotteryTableDef.BLUE_BALLS to TEXT,
            LotteryTableDef.FIVE_PARTITION to TEXT,
            LotteryTableDef.FIVE_PARTITION_TYPE to TEXT,
            LotteryTableDef.SEVEN_PARTITION to TEXT,
            LotteryTableDef.SEVEN_PARTITION_TYPE to TEXT,
            LotteryTableDef.JACKPOT to TEXT,
            LotteryTableDef.DATE to TEXT,
        )
        //双色球
        db.createTable(
            SSQTableDef.NAME, true,
            SSQTableDef.ID to TEXT + PRIMARY_KEY,
            SSQTableDef.BALLS to TEXT,
            SSQTableDef.MISS to TEXT,
            SSQTableDef.RED_BALLS to TEXT,
            SSQTableDef.BLUE_BALLS to TEXT,
            SSQTableDef.JACKPOT to TEXT,
            SSQTableDef.DATE to TEXT,
        )
        //排列三
        db.createTable(
            PLSTableDef.NAME, true,
            PLSTableDef.ID to TEXT + PRIMARY_KEY,
            PLSTableDef.BALLS to TEXT,
            PLSTableDef.MISS to TEXT,
            PLSTableDef.RED_BALLS to TEXT,
            PLSTableDef.SALE_AMOUNT to TEXT,
            PLSTableDef.DATE to TEXT,
        )
        //排列五
        db.createTable(
            PLWTableDef.NAME, true,
            PLWTableDef.ID to TEXT + PRIMARY_KEY,
            PLWTableDef.BALLS to TEXT,
            PLWTableDef.MISS to TEXT,
            PLWTableDef.RED_BALLS to TEXT,
            PLWTableDef.SALE_AMOUNT to TEXT,
            PLWTableDef.DATE to TEXT,
        )
        //键值对
        db.createTable(
            MapTableDef.NAME, true,
            MapTableDef.ID to INTEGER + PRIMARY_KEY,
            MapTableDef.KEY to TEXT,
            MapTableDef.VALUE to TEXT,
            MapTableDef.DATE to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        db?.dropTable(LotteryTableDef.NAME, true)
//        db?.dropTable(MapTableDef.NAME, true)
//        db?.dropTable(SSQTableDef.NAME, true)
        db?.dropTable(PLSTableDef.NAME, true)
        db?.dropTable(PLWTableDef.NAME, true)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
}

/**
 * Lottery数据库表对象, 用于存储每期开奖记录
 */
object LotteryTable {

    @JvmStatic
    fun save(lotteries: List<Lottery>) {
        SQL.instance.use {
            try {
                beginTransaction()
                lotteries.forEach { lottery ->
                    val i = insert(
                        LotteryTableDef.NAME,
                        LotteryTableDef.ID to lottery.id,
                        LotteryTableDef.BALLS to lottery.balls,
                        LotteryTableDef.MISS to lottery.miss.joinToString(","),
                        LotteryTableDef.RED_BALLS to lottery.redBalls.joinToString(","),
                        LotteryTableDef.BLUE_BALLS to lottery.blueBalls.joinToString(","),
                        LotteryTableDef.FIVE_PARTITION to lottery.redP.p5.joinToString(","),
                        LotteryTableDef.FIVE_PARTITION_TYPE to lottery.redP.p5t,
                        LotteryTableDef.SEVEN_PARTITION to lottery.redP.p7.joinToString(","),
                        LotteryTableDef.SEVEN_PARTITION_TYPE to lottery.redP.p7t,
                        LotteryTableDef.JACKPOT to lottery.jackpot,
                        LotteryTableDef.DATE to lottery.date,
                    )
                    if (i == -1L) throw SQLException("Error inserting $lottery")
                }
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
    }

    private val rowParser =
        rowParser { id: String, balls: String, miss: String, rballs: String, bballs: String,
                    rp5: String, rp5t: String, rp7: String, rp7t: String, jackpot: String, date: String ->
            val missArray = miss.split(",").map { it.toInt() }
            val rBalls = rballs.split(",").map { it.toInt() }
            val bBalls = bballs.split(",").map { it.toInt() }
            val rp5Array: Array<Int> = rp5.split(",").map { it.toInt() }.toTypedArray()
            val rp7Array: Array<Int> = rp7.split(",").map { it.toInt() }.toTypedArray()
            val redP = RedBallPartition(rp5Array, rp5t, rp7Array, rp7t)
            val blueP = BlueBallPartition(bBalls)
            Lottery(id, balls, missArray, rBalls, bBalls, redP, blueP, jackpot, date)
        }

    /**
     * 获取所有的Lottery记录
     * @return List<Lottery>
     */
    @JvmStatic
    fun findAll(): List<Lottery> = SQL.instance.use {
        select(LotteryTableDef.NAME).parseList(rowParser)
    }

    /**
     * 获取最新的Lottery记录，也可能没有记录
     */
    @JvmStatic
    fun findLatest(): Lottery? = SQL.instance.use {
        select(LotteryTableDef.NAME)
            .orderBy(LotteryTableDef.ID, SqlOrderDirection.DESC)
            .limit(1)
            .parseOpt(rowParser)
    }

    @JvmStatic
    fun findById(id: String): Lottery = SQL.instance.use {
        select(LotteryTableDef.NAME)
            .whereArgs("${LotteryTableDef.ID} = {id}", "id" to id)
            .parseSingle(rowParser)
    }

    /**
     * 获取一定日期范围内的Lottery记录
     * @param date 日期格式字符串，格式为： YYYY-MM-dd
     */
    @JvmStatic
    fun findByDate(date: String): List<Lottery> = SQL.instance.use {
        select(LotteryTableDef.NAME)
            .whereArgs("${LotteryTableDef.DATE} LIKE {year}", "year" to "$date%")
            .parseList(rowParser)
    }

    @JvmStatic
    fun findByLimit(limit: Int): List<Lottery> = SQL.instance.use {
        if (limit <= 0) return@use emptyList()
        select(LotteryTableDef.NAME)
            .orderBy(LotteryTableDef.ID, SqlOrderDirection.DESC)
            .limit(limit)
            .parseList(rowParser)
    }

    @JvmStatic
    fun count(): Int = SQL.instance.use {
        select(LotteryTableDef.NAME, "COUNT(${LotteryTableDef.ID})").parseSingle(IntParser)
    }

    @JvmStatic
    fun countByYear(year: String): Int = SQL.instance.use {
        select(LotteryTableDef.NAME, "COUNT(${LotteryTableDef.ID})")
            .whereArgs("${LotteryTableDef.DATE} LIKE {year}", "year" to "$year%")
            .parseSingle(IntParser)
    }

    /**
     * 清除所有纪录
     */
    @JvmStatic
    fun delete() {
        SQL.instance.use {
            delete(LotteryTableDef.NAME)
        }
    }
}

object MapTable {

    private val rowParser =
        rowParser { id: Int, key: String, value: String, date: Long ->
            MapKV(id, key, value, date)
        }

    @JvmStatic
    fun save(kv: MapKV) = SQL.instance.use {
        insert(
            MapTableDef.NAME,
            MapTableDef.KEY to kv.key,
            MapTableDef.VALUE to kv.value,
            MapTableDef.DATE to kv.date
        )
    }

    @JvmStatic
    fun findAll(): List<MapKV> = SQL.instance.use {
        select(MapTableDef.NAME).parseList(rowParser)
    }

    @JvmStatic
    fun findById(id: Int): MapKV? = SQL.instance.use {
        select(MapTableDef.NAME)
            .whereArgs("${MapTableDef.ID} = {id}", "id" to id)
            .limit(1)
            .parseOpt(rowParser)
    }

    @JvmStatic
    fun findByKey(key: String): MapKV = SQL.instance.use {
        select(MapTableDef.NAME)
            .distinct()
            .whereArgs("${MapTableDef.KEY} = {key}", "key" to key)
            .orderBy(MapTableDef.DATE, SqlOrderDirection.DESC)
            .parseOpt(rowParser) ?: MapKV(0, key, "0", 0)
    }

    @JvmStatic
    fun update(mapKV: MapKV) = SQL.instance.use {
        update(
            MapTableDef.NAME,
            MapTableDef.KEY to mapKV.key,
            MapTableDef.VALUE to mapKV.value,
            MapTableDef.DATE to mapKV.date
        ).whereArgs("${MapTableDef.ID} = {id}", "id" to mapKV.id)
            .exec()
    }

    /**
     * 统计所有数据条目
     */
    @JvmStatic
    fun count() = SQL.instance.use {
        select(MapTableDef.NAME, "COUNT(${MapTableDef.ID})").parseSingle(IntParser)
    }

    /**
     * 清除所有纪录
     */
    @JvmStatic
    fun delete() = SQL.instance.use {
        delete(MapTableDef.NAME)
    }

    @JvmStatic
    fun deleteById(id: Int) = SQL.instance.use {
        delete(MapTableDef.NAME, "${MapTableDef.ID} = {id}", "id" to id)
    }

    @JvmStatic
    fun deleteByKey(key: String) = SQL.instance.use {
        delete(MapTableDef.NAME, "${MapTableDef.KEY} = {key}", "key" to key)
    }
}

/**
 * 双色球数据库表对象，用于存储每期开奖记录
 */
object SSQTable {

    @JvmStatic
    fun save(lotteries: List<SSQ>) {
        SQL.instance.use {
            try {
                beginTransaction()
                lotteries.forEach { ssq ->
                    val i = insert(
                        SSQTableDef.NAME,
                        SSQTableDef.ID to ssq.id,
                        SSQTableDef.BALLS to ssq.balls,
                        SSQTableDef.MISS to ssq.miss.joinToString(","),
                        SSQTableDef.RED_BALLS to ssq.redBalls.joinToString(","),
                        SSQTableDef.BLUE_BALLS to ssq.blueBalls.joinToString(","),
                        SSQTableDef.JACKPOT to ssq.jackpot,
                        SSQTableDef.DATE to ssq.date,
                    )
                    if (i == -1L) throw SQLException("Error inserting $ssq")
                }
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
    }

    private val rowParser =
        rowParser { id: String, balls: String, miss: String, rballs: String, bballs: String,
                    jackpot: String, date: String ->
            val missArray = miss.split(",").map { it.toInt() }
            val rBalls = rballs.split(",").map { it.toInt() }
            val bBalls = bballs.split(",").map { it.toInt() }
            SSQ(id, balls, missArray, rBalls, bBalls, jackpot, date)
        }

    /**
     * 获取所有的SSQ记录
     * @return List<SSQ>
     */
    @JvmStatic
    fun findAll(): List<SSQ> = SQL.instance.use {
        select(SSQTableDef.NAME).parseList(rowParser)
    }

    /**
     * 获取最新的SSQ记录，也可能没有记录
     */
    @JvmStatic
    fun findLatest(): SSQ? = SQL.instance.use {
        select(SSQTableDef.NAME)
            .orderBy(SSQTableDef.ID, SqlOrderDirection.DESC)
            .limit(1)
            .parseOpt(rowParser)
    }

    @JvmStatic
    fun findById(id: String): SSQ = SQL.instance.use {
        select(SSQTableDef.NAME)
            .whereArgs("${SSQTableDef.ID} = {id}", "id" to id)
            .parseSingle(rowParser)
    }

    /**
     * 获取一定日期范围内的SSQ记录
     * @param date 日期格式字符串，格式为： YYYY-MM-dd
     */
    @JvmStatic
    fun findByDate(date: String): List<SSQ> = SQL.instance.use {
        select(SSQTableDef.NAME)
            .whereArgs("${SSQTableDef.DATE} LIKE {year}", "year" to "$date%")
            .parseList(rowParser)
    }

    @JvmStatic
    fun findByLimit(limit: Int): List<SSQ> = SQL.instance.use {
        if (limit == 0) return@use emptyList()
        select(SSQTableDef.NAME)
            .orderBy(SSQTableDef.ID, SqlOrderDirection.DESC)
            .limit(limit)
            .parseList(rowParser)
    }

    @JvmStatic
    fun count(): Int = SQL.instance.use {
        select(SSQTableDef.NAME, "COUNT(${SSQTableDef.ID})").parseSingle(IntParser)
    }

    /**
     * 清除所有纪录
     */
    @JvmStatic
    fun delete() {
        SQL.instance.use {
            delete(SSQTableDef.NAME)
        }
    }
}

/**
 * 排列三数据库表对象，用于存储每期开奖记录
 */
object PLSTable {

    @JvmStatic
    fun save(list: List<PLS>) {
        SQL.instance.use {
            try {
                beginTransaction()
                list.forEach { pls ->
                    val i = insert(
                        PLSTableDef.NAME,
                        PLSTableDef.ID to pls.id,
                        PLSTableDef.BALLS to pls.balls,
                        PLSTableDef.MISS to pls.miss.joinToString(","),
                        PLSTableDef.RED_BALLS to pls.redBalls.joinToString(","),
                        PLSTableDef.SALE_AMOUNT to pls.saleAmount,
                        PLSTableDef.DATE to pls.date,
                    )
                    if (i == -1L) throw SQLException("Error inserting $pls")
                }
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
    }

    private val rowParser =
        rowParser { id: String, balls: String, miss: String, rballs: String, amount: String, date: String ->
            val missArray = emptyList<Int>()//miss.split(",").map { it.toInt() }
            val rBalls = rballs.split(",").map { it.toInt() }
            PLS(id, balls, missArray, rBalls, amount, date)
        }

    /**
     * 获取所有的排列三记录
     * @return List<PLS>
     */
    @JvmStatic
    fun findAll(): List<PLS> = SQL.instance.use {
        select(PLSTableDef.NAME).parseList(rowParser)
    }

    /**
     * 获取最新的排列三记录，也可能没有记录
     */
    @JvmStatic
    fun findLatest(): PLS? = SQL.instance.use {
        select(PLSTableDef.NAME)
            .orderBy(PLSTableDef.ID, SqlOrderDirection.DESC)
            .limit(1)
            .parseOpt(rowParser)
    }

    @JvmStatic
    fun findById(id: String): PLS = SQL.instance.use {
        select(PLSTableDef.NAME)
            .whereArgs("${PLSTableDef.ID} = {id}", "id" to id)
            .parseSingle(rowParser)
    }

    /**
     * 获取一定日期范围内的排列三记录
     * @param date 日期格式字符串，格式为： YYYY-MM-dd
     */
    @JvmStatic
    fun findByDate(date: String): List<PLS> = SQL.instance.use {
        select(PLSTableDef.NAME)
            .whereArgs("${PLSTableDef.DATE} LIKE {year}", "year" to "$date%")
            .parseList(rowParser)
    }

    @JvmStatic
    fun findByLimit(limit: Int): List<PLS> = SQL.instance.use {
        if (limit == 0) return@use emptyList()
        select(PLSTableDef.NAME)
            .orderBy(PLSTableDef.ID, SqlOrderDirection.DESC)
            .limit(limit)
            .parseList(rowParser)
    }

    @JvmStatic
    fun count(): Int = SQL.instance.use {
        select(PLSTableDef.NAME, "COUNT(${PLSTableDef.ID})").parseSingle(IntParser)
    }

    /**
     * 清除所有纪录
     */
    @JvmStatic
    fun delete() {
        SQL.instance.use {
            delete(PLSTableDef.NAME)
        }
    }
}

/**
 * 排列五数据库表对象，用于存储每期开奖记录
 */
object PLWTable {

    @JvmStatic
    fun save(list: List<PLW>) {
        SQL.instance.use {
            try {
                beginTransaction()
                list.forEach { plw ->
                    val i = insert(
                        PLWTableDef.NAME,
                        PLWTableDef.ID to plw.id,
                        PLWTableDef.BALLS to plw.balls,
                        PLWTableDef.MISS to plw.miss.joinToString(","),
                        PLWTableDef.RED_BALLS to plw.redBalls.joinToString(","),
                        PLWTableDef.SALE_AMOUNT to plw.saleAmount,
                        PLWTableDef.DATE to plw.date,
                    )
                    if (i == -1L) throw SQLException("Error inserting $plw")
                }
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
        }
    }

    private val rowParser =
        rowParser { id: String, balls: String, miss: String, rballs: String, amount: String, date: String ->
            val missArray = emptyList<Int>()//miss.split(",").map { it.toInt() }
            val rBalls = rballs.split(",").map { it.toInt() }
            PLW(id, balls, missArray, rBalls, amount, date)
        }

    /**
     * 获取所有的排列五记录
     * @return List<PLS>
     */
    @JvmStatic
    fun findAll(): List<PLW> = SQL.instance.use {
        select(PLWTableDef.NAME).parseList(rowParser)
    }

    /**
     * 获取最新的排列五记录，也可能没有记录
     */
    @JvmStatic
    fun findLatest(): PLW? = SQL.instance.use {
        select(PLWTableDef.NAME)
            .orderBy(PLWTableDef.ID, SqlOrderDirection.DESC)
            .limit(1)
            .parseOpt(rowParser)
    }

    @JvmStatic
    fun findById(id: String): PLW = SQL.instance.use {
        select(PLWTableDef.NAME)
            .whereArgs("${PLWTableDef.ID} = {id}", "id" to id)
            .parseSingle(rowParser)
    }

    /**
     * 获取一定日期范围内的排列五记录
     * @param date 日期格式字符串，格式为： YYYY-MM-dd
     */
    @JvmStatic
    fun findByDate(date: String): List<PLW> = SQL.instance.use {
        select(PLWTableDef.NAME)
            .whereArgs("${PLWTableDef.DATE} LIKE {year}", "year" to "$date%")
            .parseList(rowParser)
    }

    @JvmStatic
    fun findByLimit(limit: Int): List<PLW> = SQL.instance.use {
        if (limit == 0) return@use emptyList()
        select(PLWTableDef.NAME)
            .orderBy(PLWTableDef.ID, SqlOrderDirection.DESC)
            .limit(limit)
            .parseList(rowParser)
    }

    @JvmStatic
    fun count(): Int = SQL.instance.use {
        select(PLWTableDef.NAME, "COUNT(${PLWTableDef.ID})").parseSingle(IntParser)
    }

    /**
     * 清除所有纪录
     */
    @JvmStatic
    fun delete() {
        SQL.instance.use {
            delete(PLWTableDef.NAME)
        }
    }
}