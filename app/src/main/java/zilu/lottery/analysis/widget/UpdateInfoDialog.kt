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

package zilu.lottery.analysis.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import zilu.lottery.analysis.R

/**
 * 更新提示Dialog
 * Create by zilu 2023/10/01
 */
class UpdateInfoDialog(ctx: Context) : Dialog(ctx, R.style.AppTheme_Dialog) {

    private var confirmListener: ((DialogInterface) -> Unit)? = null
    private var dialogTitle: TextView
    private var dialogSecondTitle: TextView
    private var dialogContent: TextView
    private var dialogCancelBtn: TextView
    private var dialogConfirmBtn: TextView
    private var dialogDownloadProgressBar: ProgressBar

    init {
        setContentView(R.layout.widget_update_dialog)
        dialogTitle = findViewById(R.id.dialogTitle)
        dialogSecondTitle = findViewById(R.id.dialogSecondTitle)
        dialogContent = findViewById(R.id.dialogContent)
        dialogCancelBtn = findViewById(R.id.dialogCancelBtn)
        dialogConfirmBtn = findViewById(R.id.dialogConfirmBtn)
        dialogDownloadProgressBar = findViewById(R.id.dialogDownloadProgress)

        dialogCancelBtn.setOnClickListener { cancel() }
        dialogConfirmBtn.setOnClickListener {
            confirmListener?.invoke(this)
        }
    }

    fun setOnConfirmListener(listener: ((DialogInterface) -> Unit)?) = apply {
        this.confirmListener = listener
    }

    fun setContentDesc(desc: CharSequence?) = apply {
        dialogContent.text = desc
    }

    fun setContentTextColor(color: Int) = apply {
        dialogContent.setTextColor(color)
    }

    fun setContentTextSize(size: Float) = apply {
        dialogContent.textSize = size
    }

    override fun setTitle(title: CharSequence?) {
        dialogTitle.text = title
    }

    override fun setTitle(titleId: Int) {
        dialogTitle.setText(titleId)
    }

    fun setTitleTextSize(size: Float) = apply {
        dialogTitle.textSize = size
    }

    fun setTitleTextColor(color: Int) = apply {
        dialogTitle.setTextColor(color)
    }

    fun showSecondTitle(show: Boolean) = apply {
        dialogSecondTitle.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setSecondTitle(title: CharSequence?) = apply {
        dialogSecondTitle.text = title
    }

    fun setSecondTitle(titleId: Int) = apply {
        dialogSecondTitle.setText(titleId)
    }

    fun setSecondTitleTextColor(color: Int) = apply {
        dialogSecondTitle.setTextColor(color)
    }

    fun setSecondTitleTextSize(size: Float) = apply {
        dialogSecondTitle.textSize = size
    }

    fun setCancelBtnText(text: CharSequence?) = apply {
        dialogCancelBtn.text = text
    }

    fun setCancelBtnTextColor(color: Int) = apply {
        dialogCancelBtn.setTextColor(color)
    }

    fun setCancelBtnEnable(enable: Boolean) = apply {
        dialogCancelBtn.isEnabled = enable
    }

    fun setConfirmBtnText(text: CharSequence?) = apply {
        dialogConfirmBtn.text = text
    }

    fun setConfirmBtnTextColor(color: Int) = apply {
        dialogConfirmBtn.setTextColor(color)
    }

    fun setConfirmBtnEnable(enable: Boolean) = apply {
        dialogConfirmBtn.isEnabled = enable
    }

    fun setProgress(total: Int, current: Int) = with(dialogDownloadProgressBar) {
        if (total <= 0 || current < 0) return@with
        if (!isVisible) isVisible = true
        max = total
        progress = current
    }
}