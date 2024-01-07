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

package zilu.lottery.analysis.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.MainScope
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import zilu.lottery.analysis.BuildConfig
import zilu.lottery.analysis.R
import zilu.lottery.analysis.bean.UpdateInfo
import zilu.lottery.analysis.data.Download
import zilu.lottery.analysis.data.DownloadCompleteReceiver
import zilu.lottery.analysis.gson
import zilu.lottery.analysis.widget.UpdateInfoDialog
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val TAG = "MainAc-->"
    private var downloadCompleteReceiver: DownloadCompleteReceiver? = null
    private var mainScope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        checkVersionUpdate()
    }

    override fun onDestroy() {
        if (downloadCompleteReceiver != null) {
            unregisterReceiver(downloadCompleteReceiver)
        }
        contentObserver?.let {
            contentResolver.unregisterContentObserver(it)
        }
        super.onDestroy()
    }

    private fun checkVersionUpdate() {
        doAsync {
            try {
                val json = URL(BuildConfig.UPDATE_PROXY + BuildConfig.RAW_URL).readText()
                Log.d(TAG, "updateInfo: $json")
                val updateInfo = gson.fromJson(json, UpdateInfo::class.java)
                if (updateInfo.code > BuildConfig.VERSION_CODE) {
                    uiThread {
                        doUpdate(updateInfo)
                    }
                }
            } catch (e: Exception) {
                uiThread { toast(R.string.toast_check_update_error) }
                e.printStackTrace()
            }
        }
    }

    private var contentObserver: DownloadChangeObserver? = null
    private fun doUpdate(updateInfo: UpdateInfo) {
        val formatConfirmText = getString(R.string.dialog_confirm_text, updateInfo.size / 1048576f)
        UpdateInfoDialog(this).apply {
            setCancelable(false)
            setTitle("软件更新提示")
            setSecondTitle("版本号: v${updateInfo.name}")
            setContentDesc(updateInfo.des)
            setConfirmBtnText(formatConfirmText)
            setOnConfirmListener { downloadApk(BuildConfig.UPDATE_PROXY + updateInfo.url, this) }
        }.show()
    }

    private fun downloadApk(url: String, dialog: UpdateInfoDialog) {
        val taskId = Download.apk(this, url)
//        Log.d(TAG, "taskId: $taskId")
        contentObserver = DownloadChangeObserver(this, taskId).also { observer ->
            observer.setCallback(object : DownloadChangeObserver.Callback {
                override fun onProgress(total: Int, current: Int) {
                    dialog.setProgress(total, current)
                    dialog.setCancelBtnEnable(false)
                    dialog.setConfirmBtnEnable(false)
                }

                override fun onFail(id: Long) {
//                    unregisterReceiver(downloadCompleteReceiver)
//                    downloadCompleteReceiver = null
                    contentResolver.unregisterContentObserver(observer)
                    Log.e(TAG, "download Fail: $id")
                    dialog.setConfirmBtnEnable(true)
                    dialog.setCancelBtnEnable(true)
                    this@MainActivity.toast(R.string.toast_download_fail)
                }

                override fun onSuccess(uri: Uri, id: Long) {
                    Log.e(TAG, "download Success")
//                    unregisterReceiver(downloadCompleteReceiver)
//                    downloadCompleteReceiver = null
                    contentResolver.unregisterContentObserver(observer)
                    dialog.setConfirmBtnEnable(true)
                    dialog.setCancelBtnEnable(true)
                    dialog.setConfirmBtnText("立即安装")
                    dialog.setOnConfirmListener { installApk(uri) }
                    this@MainActivity.longToast(R.string.toast_download_success)
                }
            })
            contentResolver.registerContentObserver(
                Uri.parse("content://downloads/my_downloads/$taskId"),
                false, observer
            )
            observer.onChange(true)
        }
    }

    fun installApk(apkPath: Uri?) {
        startActivity(
            Intent(Intent.ACTION_VIEW)
                .setDataAndType(apkPath, "application/vnd.android.package-archive")
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        )
    }

    class DownloadChangeObserver(ctx: Context, private val taskId: Long) :
        ContentObserver(Handler(Looper.getMainLooper())) {
        private val TAG = "DownloadObserver-->"
        private val manager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        private var callback: Callback? = null

        override fun onChange(selfChange: Boolean) {
            manager.query(DownloadManager.Query().setFilterById(taskId)).use { cursor ->
                if (cursor.moveToFirst()) {
                    val status =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_RUNNING -> {
                            val total =
                                cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            val current =
                                cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            Log.d(TAG, "total: $total & current: $current")
                            callback?.onProgress(total, current)
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            val total =
                                cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            val fileUri = manager.getUriForDownloadedFile(taskId)
                            Log.d(TAG, "download success! path: ${fileUri.path}, total: $total")
                            callback?.onProgress(total, total)
                            callback?.onSuccess(fileUri, taskId)
                        }
                        //下载失败
                        DownloadManager.STATUS_FAILED -> {
                            Log.w(TAG, "download failed! $taskId")
                            //remove download record & retry to download
                            manager.remove(taskId)
                            callback?.onFail(taskId)
                        }
                    }
                }
            }
        }

        fun setCallback(callback: Callback?) {
            this.callback = callback
        }

        interface Callback {
            fun onProgress(total: Int, current: Int)
            fun onSuccess(uri: Uri, id: Long)
            fun onFail(id: Long)
        }
    }
}
