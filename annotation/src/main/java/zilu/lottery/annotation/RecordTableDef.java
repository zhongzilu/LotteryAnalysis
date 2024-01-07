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
 * 投注优化 - 选号记录历史数据表字段定义
 * Create by zilu 2023/09/22
 */
public @interface RecordTableDef {

    /**
     * 表名
     */
    String NAME = "records";

    /**
     * 自增长id
     */
    String ID = "id";

    /**
     * 红球选号记录
     */
    String R_BALLS = "rb";

    /**
     * 蓝球选号记录
     */
    String B_BALLS = "bb";

    /**
     * 临时数据标记，0: 历史性的(永久性的); 1: 临时性的
     */
    String TEMP = "tmp";

    /**
     * 选择的号码属于什么彩种
     */
    String TYPE = "type";

    /**
     * 创建日期
     */
    String DATE = "date";

    /**
     * 更新日期
     */
    String UPDATE = "udate";
}
