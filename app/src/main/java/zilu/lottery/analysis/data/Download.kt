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

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log

/**
 *
 * Create by zilu 2021/08/12
 */
object Download {

    @JvmStatic
    private val TAG = "Download-->"

    @JvmStatic
    val CONTENT_DISPOSITION = "Content-Disposition: attachment"

    /**
     * @param ctx
     * @param url
     * @return Task uuid
     */
//    @JvmStatic
//    fun audio(context: Context, url: String) =
//        inBackground(context, url, CONTENT_DISPOSITION, "audio/*")

    @JvmStatic
    fun apk(ctx: Context, url: String) =
        inBackground(ctx, url, CONTENT_DISPOSITION, "application/octet-stream")

    @JvmStatic
    private fun inBackground(
        ctx: Context,
        url: String,
        contentDisposition: String,
        mimeType: String
    ): Long {
        if (TextUtils.isEmpty(url)) {
            Log.d(TAG, "url is null or empty")
            return -1
        }

        val manager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val fileName = parseFileName(url)
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE
//                            or DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                )
                .setTitle(fileName)
                .setMimeType(mimeType)
                .setAllowedOverRoaming(false)
                .setDestinationUri(
                    Uri.withAppendedPath(
                        Uri.fromFile(ctx.getExternalFilesDir("")),
                        fileName
                    )
                )
//                .setDestinationInExternalFilesDir(ctx, Environment.DIRECTORY_DOWNLOADS, "")

            Log.d(TAG, url)
            return manager.enqueue(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return -1
    }

//    @JvmStatic
//    fun parseFileName(url: String?, contentDisposition: String?, mimeType: String?): String {
//        return URLUtil.guessFileName(url, contentDisposition, mimeType)
//    }

    @JvmStatic
    fun parseFileName(url: String): String {
        val i = url.lastIndexOf("/")
        return if (i > 0) url.substring(i + 1) else url
    }
}

class DownloadCompleteReceiver : BroadcastReceiver() {
    private val TAG = "DownReceiver-->"
    private var callback: Callback? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE != intent.action) return

        val taskId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query().setFilterById(taskId)
        //根据下载ID过滤结果
        val cursor = manager.query(query)
        if (!cursor.moveToFirst()) {
            cursor.close()
            return
        }
        Log.d(TAG, "check status")
        //下载请求的状态
        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

        /*
         * 特别注意: 查询获取到的 localFileName 才是下载文件真正的保存路径，在创建
         * 请求时设置的保存路径不一定是最终的保存路径，因为当设置的路径已是存在的文件时，
         * 下载器会自动重命名保存路径，例如: .../demo-1.apk, .../demo-2.apk
         *
         * 下载失败时，获取该值为null
         */
//        val localFileName: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
//        } else {
//            cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME))
//        }

        cursor.close()
        when (status) {
            //下载成功
            DownloadManager.STATUS_SUCCESSFUL -> {
                val fileUri = manager.getUriForDownloadedFile(taskId)
                Log.i(TAG, "download success! path: ${fileUri.path}")
//                DownloadTask.success(taskId)
                callback?.onSuccess(fileUri, taskId)
            }
            //下载失败
            DownloadManager.STATUS_FAILED -> {
                Log.w(TAG, "download failed! $taskId")
                //remove download record & retry to download
                manager.remove(taskId)
                callback?.onFail(taskId)
            }
            DownloadManager.STATUS_PENDING,
            DownloadManager.STATUS_RUNNING,
            DownloadManager.STATUS_PAUSED -> Unit
        }
    }

    fun setCallback(callback: Callback?) = apply {
        this.callback = callback
    }

    interface Callback {
        fun onSuccess(uri: Uri, id: Long)
        fun onFail(id: Long)
    }
}