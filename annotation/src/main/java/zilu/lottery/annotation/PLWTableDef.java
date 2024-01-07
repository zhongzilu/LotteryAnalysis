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
 * PLWTable(排列五)数据表字段定义
 * Create by zilu 2023/09/22
 */
public @interface PLWTableDef {
    /**
     * 表名
     */
    String NAME = "plw";

    /**
     * 期号
     */
    String ID = "id";

    /**
     * 开奖号码
     */
    String BALLS = "balls";

    /**
     * 遗漏数据, 格式：1,2,3,1,"",5
     */
    String MISS = "miss";

    /**
     * 红球组
     */
    String RED_BALLS = "rballs";

    /**
     * 销售总金额
     */
    String SALE_AMOUNT = "amount";

    /**
     * 日期
     */
    String DATE = "date";
}
