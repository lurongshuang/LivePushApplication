package com.lrs.livepushapplication.application

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Camera
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.view.inputmethod.InputMethodManager
import androidx.multidex.MultiDex
import cn.bmob.v3.Bmob
import com.alivc.live.pusher.*
import com.lrs.livepushapplication.BuildConfig
import com.lrs.livepushapplication.utils.Common
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferenceUtils
import com.lrs.livepushapplication.utils.thread.ThreadUtils
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.xuexiang.xui.XUI
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.unit.Subunits
import me.jessyan.autosize.utils.ScreenUtils
import java.io.File

/**
 * @description 作用:
 * @date: 2020/4/14
 * @author: 卢融霜
 */
class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
        XUI.init(this)
        XUI.debug(true)
        //布局auto
        initAutoSize()
        //初始化云存储
        Bmob.initialize(this, "1ad4db4503319e703ac4127b78827df5")
        MultiDex.install(this)
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(ConnectivityChangedReceiver(), filter)
        if (BuildConfig.DEBUG) {
            LogUtil.enalbeDebug()
        } else {
            LogUtil.disableDebug()
        }
        ThreadUtils.runThread {
            val cb: PreInitCallback = object : PreInitCallback {
                override fun onCoreInitFinished() {}
                override fun onViewInitFinished(b: Boolean) {}
            }
            QbSdk.initX5Environment(applicationContext, cb)
            //不兼容Android Q 10
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                QbSdk.forceSysWebView()
            }
        }
    }

    internal inner class ConnectivityChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {}
    }

    private fun initAutoSize() {
        AutoSizeConfig.getInstance() //是否让框架支持自定义 Fragment 的适配参数, 由于这个需求是比较少见的, 所以须要使用者手动开启
                //如果没有这个需求建议不开启
                .setCustomFragment(true) //是否屏蔽系统字体大小对 AndroidAutoSize 的影响, 如果为 true, App 内的字体的大小将不会跟随系统设置中字体大小的改变
                //如果为 false, 则会跟随系统设置中字体大小的改变, 默认为 false
                .setExcludeFontScale(true) //区别于系统字体大小的放大比例, AndroidAutoSize 允许 APP 内部可以独立于系统字体大小之外，独自拥有全局调节 APP 字体大小的能力
                //当然, 在 APP 内您必须使用 sp 来作为字体的单位, 否则此功能无效, 不设置或将此值设为 0 则取消此功能
                //                .setPrivateFontScale(0.8f)
                //屏幕适配监听器
                .setOnAdaptListener(object : onAdaptListener {
                    override fun onAdaptBefore(target: Any, activity: Activity) {
                        //                    //使用以下代码, 可支持 Android 的分屏或缩放模式, 但前提是在分屏或缩放模式下当用户改变您 App 的窗口大小时
//                    //系统会重绘当前的页面, 经测试在某些机型, 某些情况下系统不会重绘当前页面, ScreenUtils.getScreenSize(activity) 的参数一定要不要传 Application!!!
                        AutoSizeConfig.getInstance().screenWidth = ScreenUtils.getScreenSize(activity)[0]
                        AutoSizeConfig.getInstance().screenHeight = ScreenUtils.getScreenSize(activity)[1]
                    }

                    override fun onAdaptAfter(target: Any, activity: Activity) {}
                })
                .unitsManager
                .setSupportDP(true)
                .setSupportSP(true).supportSubunits = Subunits.NONE

        //是否打印 AutoSize 的内部日志, 默认为 true, 如果您不想 AutoSize 打印日志, 则请设置为 false
//                .setLog(false)
        //是否使用设备的实际尺寸做适配, 默认为 false, 如果设置为 false, 在以屏幕高度为基准进行适配时
        //AutoSize 会将屏幕总高度减去状态栏高度来做适配
        //设置为 true 则使用设备的实际屏幕高度, 不会减去状态栏高度
        //在全面屏或刘海屏幕设备中, 获取到的屏幕高度可能不包含状态栏高度, 所以在全面屏设备中不需要减去状态栏高度，所以可以 setUseDeviceSize(true)
//                .setUseDeviceSize(true)

        //是否全局按照宽度进行等比例适配, 默认为 true, 如果设置为 false, AutoSize 会全局按照高度进行适配
//                .setBaseOnWidth(false)

        //设置屏幕适配逻辑策略类, 一般不用设置, 使用框架默认的就好
//                .setAutoAdaptStrategy(new AutoAdaptStrategy())

        //当 App 中出现多进程, 并且您需要适配所有的进程, 就需要在 App 初始化时调用 initCompatMultiProcess()
        AutoSize.initCompatMultiProcess(this)
    }

    companion object {
        @JvmStatic
        var application: Application? = null
            private set
        var manager: InputMethodManager? = null
        var mAlivcLivePushConfig: AlivcLivePushConfig? = null
        var mAsyncValue = true
        var mAudioOnlyPush = false
        var mVideoOnlyPush = false
        var mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT
        var mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        var isFlash = false
        var mAuthTimeStr = ""
        var mPrivacyKeyStr = ""
        var mMixStream = false
    }

    fun initAliManger() {
        if (mAlivcLivePushConfig == null) {
            Common.copyAsset(applicationContext)
            Common.copyAll(applicationContext)
            mAlivcLivePushConfig = AlivcLivePushConfig()
            if (mAlivcLivePushConfig?.previewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.orientation || mAlivcLivePushConfig?.previewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.orientation) {
                mAlivcLivePushConfig?.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
                mAlivcLivePushConfig?.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
            } else {
                mAlivcLivePushConfig?.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network.png"
                mAlivcLivePushConfig?.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push.png"
            }
            AlivcLivePushConfig.setMediaProjectionPermissionResultData(null)
        }
        var qm = SharedPreferenceUtils.getQualityMode(applicationContext);
        //如果清晰度优先
        if (qm == AlivcQualityModeEnum.QM_RESOLUTION_FIRST.qualityMode) {
            mAlivcLivePushConfig!!.qualityMode = AlivcQualityModeEnum.QM_RESOLUTION_FIRST
            //分辨率
            var targetBit = SharedPreferenceUtils.getHintMinBit(applicationContext);
            when (targetBit) {
                AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    //分辨率
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_180P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    //目标码率
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    //最小码率
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    //初始码率
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                }
                AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_240P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_360P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_720P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                0 -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
            }
        } else {
            //流畅度优先
            mAlivcLivePushConfig!!.qualityMode = AlivcQualityModeEnum.QM_FLUENCY_FIRST
            //分辨率
            var targetBit = SharedPreferenceUtils.getHintMinBit(applicationContext);
            when (targetBit) {
                AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    //分辨率
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_180P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    //目标码率
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    //最小码率
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    //初始码率
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                }
                AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_240P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_360P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_720P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                0 -> {
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
            }
        }
        //音频码率
        mAlivcLivePushConfig!!.audioBitRate = 1000 * 64;
        //最小帧率
        var fps = SharedPreferenceUtils.getFpsBit(applicationContext);
        if (fps == "FPS_8") {
            mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_8)
            SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_8.name);
        } else {
            mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.valueOf(fps));
        }
        // 最小帧率
        mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_25);

        //音频采样率
        mAlivcLivePushConfig!!.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000)
//        var audioSR = SharedPreferenceUtils.getAudioSamepleRate(applicationContext);
//        if (audioSR == 0) {
//            mAlivcLivePushConfig!!.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000)
//            SharedPreferenceUtils.setAudioSamepleRate(applicationContext, AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000.audioSampleRate);
//        } else {
//            mAlivcLivePushConfig!!.setAudioSamepleRate(audioSR as AlivcAudioSampleRateEnum);
//        }
        //音频编码
        mAlivcLivePushConfig!!.audioProfile = AlivcAudioAACProfileEnum.AAC_LC
//        var audioProFile = SharedPreferenceUtils.getAudioProFile(applicationContext);
//        if (audioProFile == 0) {
//            mAlivcLivePushConfig!!.audioProfile = AlivcAudioAACProfileEnum.AAC_LC
//        } else {
//            mAlivcLivePushConfig!!.audioProfile = audioProFile as AlivcAudioAACProfileEnum;
//        }
        //声道
        mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO);
//        var audioChannels = SharedPreferenceUtils.getAudioChannels(applicationContext);
//        if (audioChannels == 0) {
//            mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO)
//        } else {
//            mAlivcLivePushConfig!!.setAudioChannels(audioChannels as AlivcAudioChannelEnum);
//        }

        //屏幕方向
        var orienTation = AlivcPreviewOrientationEnum.valueOf(SharedPreferenceUtils.getOrienTation(applicationContext))

        when (orienTation) {
            AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT -> {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT
                if (mAlivcLivePushConfig!!.pausePushImage != null && mAlivcLivePushConfig!!.pausePushImage != "") {
                    mAlivcLivePushConfig!!.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push.png"
                }
                if (mAlivcLivePushConfig!!.networkPoorPushImage != null && mAlivcLivePushConfig!!.networkPoorPushImage != "") {
                    mAlivcLivePushConfig!!.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network.png"
                }

                SharedPreferenceUtils.setOrienTation(applicationContext, AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT.name)
            }
            AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT -> {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT
                if (mAlivcLivePushConfig!!.pausePushImage != null && mAlivcLivePushConfig!!.pausePushImage != "") {
                    mAlivcLivePushConfig!!.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
                }
                if (mAlivcLivePushConfig!!.networkPoorPushImage != null && mAlivcLivePushConfig!!.networkPoorPushImage != "") {
                    mAlivcLivePushConfig!!.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
                }
                SharedPreferenceUtils.setOrienTation(applicationContext, AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.name)
            }
            AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT -> {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT
                if (mAlivcLivePushConfig!!.pausePushImage != null && mAlivcLivePushConfig!!.pausePushImage != "") {
                    mAlivcLivePushConfig!!.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
                }
                if (mAlivcLivePushConfig!!.networkPoorPushImage != null && mAlivcLivePushConfig!!.networkPoorPushImage != "") {
                    mAlivcLivePushConfig!!.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
                }
                SharedPreferenceUtils.setOrienTation(applicationContext, AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.name)
            }
        }
        //GOp 帧率
        mAlivcLivePushConfig!!.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_TWO);

        //重连时长
        mAlivcLivePushConfig!!.setConnectRetryInterval(AlivcLivePushConstants.DEFAULT_VALUE_INT_RETRY_INTERVAL);
        //重连次数
        mAlivcLivePushConfig!!.setConnectRetryCount(AlivcLivePushConstants.DEFAULT_VALUE_INT_AUDIO_RETRY_COUNT)
        //直播类型
        var liveType = SharedPreferenceUtils.getLiveType(applicationContext);
        when (liveType) {
            0 -> {
                mAlivcLivePushConfig!!.setAudioOnly(false)
                mAlivcLivePushConfig!!.setVideoOnly(false)
                mAudioOnlyPush = false
                mVideoOnlyPush = false;
                SharedPreferenceUtils.setLiveType(applicationContext, 0);
            }
            1 -> {
                mVideoOnlyPush = true;
                mAlivcLivePushConfig!!.setVideoOnly(true)
            }
            2 -> {
                mAudioOnlyPush = true;
                mAlivcLivePushConfig!!.setAudioOnly(true)

            }
        }
        //是否开启美颜
        var beautyOn = SharedPreferenceUtils.getBeautyOn(applicationContext);
        mAlivcLivePushConfig!!.setBeautyOn(beautyOn);
    }
}