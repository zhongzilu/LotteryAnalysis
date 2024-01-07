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
 * MapTable数据表字段定义
 * Create by zilu 2023/08/15
 */
public @interface MapTableDef {
    /**
     * 表名
     */
    String NAME = "Map";

    /**
     * 自增长序号ID
     */
    String ID = "id";

    /**
     * 统计条件值，不能为空，可以是任意的关键词组成的字符串，
     * 比如： `2023.All.RedBalls`, 也可以是单个关键词，比如按年统计，这里存储的就是年份号`2023`
     */
    String KEY = "key";

    /**
     * 统计结果值
     */
    String VALUE = "value";

    /**
     * 记录日期时间戳
     */
    String DATE = "date";
}
