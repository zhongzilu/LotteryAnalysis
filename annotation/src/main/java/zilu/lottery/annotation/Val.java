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

package zilu.lottery.annotation;

/**
 * 基本常量定义
 * Create by zilu 2023/08/11
 */
public @interface Val {

    /**
     * 乐透球号总个数(红球1-35，篮球1-12)
     */
    int DLT_BALL_SIZE = 47;

    /**
     * 乐透红球的数量
     */
    int DLT_RED_BALL_SIZE = 35;

    /**
     * 乐透蓝球的数量
     */
    int DLT_BLUE_BALL_SIZE = 12;

    /**
     * 大乐透开奖号的红球个数
     */
    int DLT_RED_BALL_LIMIT = 5;

    /**
     * 大乐透开奖号的蓝球个数
     */
    int DLT_BLUE_BALL_LIMIT = 2;

    /**
     * 双色球号总个数(红球1-33，篮球1-16)
     */
    int SSQ_BALL_SIZE = 49;

    /**
     * 双色球红球的数量
     */
    int SSQ_RED_BALL_SIZE = 33;

    /**
     * 双色球蓝球的数量
     */
    int SSQ_BLUE_BALL_SIZE = 16;

    /**
     * 排列三号码总个数（分为：百位、十位，个位，每位号码范围为0～9）
     * Example:
     * <p>
     * 开奖号码为518（百位是5，十位是1，个位是8）
     * </p>
     */
    int PLS_BALL_SIZE = 10;

    /**
     * 排列三红球数量
     */
    int PLS_RED_BALL_SIZE = 3;

    /**
     * 默认查询条数
     */
    int DEFAULT_QUERY_LIMIT = 30;

    /**
     * 表格单元格垂直padding
     */
    float V_PADDING = 5f;

    /**
     * 表格单元格垂直padding
     */
    float H_PADDING = 6f;
}
