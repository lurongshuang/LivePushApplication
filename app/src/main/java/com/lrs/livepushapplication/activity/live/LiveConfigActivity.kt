package com.lrs.livepushapplication.activity.live

import android.os.Environment
import android.widget.*
import com.alivc.live.pusher.*
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.application.Application
import com.lrs.livepushapplication.application.Application.*
import com.lrs.livepushapplication.application.Application.Companion.mAlivcLivePushConfig
import com.lrs.livepushapplication.application.Application.Companion.mAudioOnlyPush
import com.lrs.livepushapplication.application.Application.Companion.mCameraId
import com.lrs.livepushapplication.application.Application.Companion.mOrientationEnum
import com.lrs.livepushapplication.application.Application.Companion.mVideoOnlyPush
import com.lrs.livepushapplication.base.BaseActivity
import com.lrs.livepushapplication.utils.Common
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferenceUtils
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner
import java.io.File
import java.util.*

class LiveConfigActivity : BaseActivity() {

    private var mDefinition: AlivcResolutionEnum = AlivcResolutionEnum.RESOLUTION_540P
//    private val PROGRESS_0 = 0
//    private val PROGRESS_16 = 16
//    private val PROGRESS_20 = 20
//    private val PROGRESS_33 = 33
//    private val PROGRESS_40 = 40
//    private val PROGRESS_50 = 50
//    private val PROGRESS_60 = 60
//    private val PROGRESS_66 = 66
//    private val PROGRESS_75 = 75
//    private val PROGRESS_80 = 80
//    private val PROGRESS_100 = 100
//
//    private val PROGRESS_AUDIO_320 = 30
//    private val PROGRESS_AUDIO_441 = 70
//    private val PROGRESS_AUDIO_480 = 100

//    private var mFps: SeekBar? = null
//    private var mMinFps: SeekBar? = null
//    private var mWaterPosition: TextView? = null
//    private var mFpsText: TextView? = null
//    private var mMinFpsText: TextView? = null

//    private var mTargetRate: EditText? = null
//    private var mMinRate: EditText? = null

    //    private var mInitRate: EditText? = null
//    private var mAudioBitRate: EditText? = null
//    private var mRetryInterval: EditText? = null
//    private var mRetryCount: EditText? = null
//    private var mAuthTime: EditText? = null
//    private var mPrivacyKey: EditText? = null

    //    private var mWaterMark: Switch? = null
//    private var mPushMirror: Switch? = null
//    private var mPreviewMirror: Switch? = null

    //    private var mHardCode: Switch? = null
//    private var mAudioHardCode: Switch? = null
    private var mCamera: Switch? = null

    //    private var mAutoFocus: Switch? = null
    private var mBeautyOn: Switch? = null
//    private var mAsync: Switch? = null
//    private var mFlash: Switch? = null
//    private var mLog: Switch? = null
//    private var mBitrate: Switch? = null
//    private var mVariableResolution: Switch? = null
//    private var mExtern: Switch? = null

    //private Switch mExternMix;
//    private var mPauseImage: Switch? = null
//    private var mNetworkImage: Switch? = null
//    private var mAudioRadio: RadioGroup? = null
    private var mQualityMode: RadioGroup? = null

    //    private var mGop: RadioGroup? = null
    private var mOrientation: RadioGroup? = null
    private var mDisplayMode: RadioGroup? = null
//    private var mBeautyLevel: RadioGroup? = null
//    private var mAudioProfiles: RadioGroup? = null

    //美颜相关数据
//    private var mCheekPinkBar: SeekBar? = null
//    private var mWhiteBar: SeekBar? = null
//    private var mSkinBar: SeekBar? = null
//    private var mRuddyBar: SeekBar? = null
//    private var mSlimFaceBar: SeekBar? = null
//    private var mShortenFaceBar: SeekBar? = null
//    private var mBigEyeBar: SeekBar? = null
//
//    private var mCheekpink: TextView? = null
//    private var mWhite: TextView? = null
//    private var mSkin: TextView? = null
//    private var mRuddy: TextView? = null
//    private var mSlimFace: TextView? = null
//    private var mShortenFace: TextView? = null
//    private var mBigEye: TextView? = null
    private var msReList: MaterialSpinner? = null

    //    private var audioReList: MaterialSpinner? = null
    private var audio_Type: MaterialSpinner? = null

//    private var mWaterLinear: LinearLayout? = null

    private val mQualityModeEnum: AlivcQualityModeEnum = AlivcQualityModeEnum.QM_RESOLUTION_FIRST

    private val waterMarkInfos: ArrayList<WaterMarkInfo> = ArrayList<WaterMarkInfo>()


    //    private var cm: ClipboardManager? = null
    private val mShowDebugView = false


    override fun initData() {
        setTitle(true, "直播设置");
        initView()
        setClick()
        if (mAlivcLivePushConfig == null) {
            Common.copyAsset(this)
            Common.copyAll(this)
            mAlivcLivePushConfig = AlivcLivePushConfig()
            //屏幕方向
            AlivcLivePushConfig.setMediaProjectionPermissionResultData(null)
        }
        initState();

    }

    private fun initState() {
//        mResolution!!.progress=SharedPreferenceUtils.get(   applicationContext)
//        mWhite!!.text = String.valueOf(SharedPreferenceUtils.getWhiteValue(applicationContext))
//        mWhiteBar!!.progress = SharedPreferenceUtils.getWhiteValue(applicationContext)
//        mSkin!!.text = String.valueOf(SharedPreferenceUtils.getBuffing(applicationContext))
//        mSkinBar!!.progress = SharedPreferenceUtils.getBuffing(applicationContext)
//        mRuddy!!.text = String.valueOf(SharedPreferenceUtils.getRuddy(applicationContext))
//        mRuddyBar!!.progress = SharedPreferenceUtils.getRuddy(applicationContext)
//        mCheekpink!!.text = String.valueOf(SharedPreferenceUtils.getCheekpink(applicationContext))
//        mCheekPinkBar!!.progress = SharedPreferenceUtils.getCheekpink(applicationContext)
        //        mInitRate?.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//        addWaterMarkInfo()
//        cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //分辨率
        var qm = SharedPreferenceUtils.getQualityMode(Application.application!!.applicationContext);
        //如果清晰度优先
        if (qm == AlivcQualityModeEnum.QM_RESOLUTION_FIRST.qualityMode) {
            mQualityMode?.check(R.id.resolution_first);
            mAlivcLivePushConfig!!.qualityMode = AlivcQualityModeEnum.QM_RESOLUTION_FIRST
            //分辨率  ,
            var definition = AlivcResolutionEnum.valueOf(SharedPreferenceUtils.getDefinition(applicationContext));
            mAlivcLivePushConfig!!.setResolution(definition);
            when (definition) {
                AlivcResolutionEnum.RESOLUTION_180P -> {
                    //分辨率
                    msReList?.selectedIndex = 0;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_180P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    //目标码率
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    //最小码率
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    //初始码率
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                }
                AlivcResolutionEnum.RESOLUTION_240P -> {
                    msReList?.selectedIndex = 1;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_240P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_360P -> {
                    msReList?.selectedIndex = 2;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_360P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!!!.applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_480P -> {
                    msReList?.selectedIndex = 3;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_480P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_540P -> {
                    msReList?.selectedIndex = 4;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_720P -> {
                    msReList?.selectedIndex = 5;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_720P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                else -> {
                    msReList?.selectedIndex = 4;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
            }
        } else {
            //流畅度优先
            mQualityMode?.check(R.id.fluency_first);
            mAlivcLivePushConfig!!.qualityMode = AlivcQualityModeEnum.QM_FLUENCY_FIRST
            //分辨率
            var definition = AlivcResolutionEnum.valueOf(SharedPreferenceUtils.getDefinition(applicationContext));
            mAlivcLivePushConfig!!.setResolution(definition)
            when (definition) {
                AlivcResolutionEnum.RESOLUTION_180P -> {
                    //分辨率
                    msReList?.selectedIndex = 0;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_180P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    //目标码率
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    //最小码率
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    //初始码率
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                }
                AlivcResolutionEnum.RESOLUTION_240P -> {
                    msReList?.selectedIndex = 1;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_240P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_360P -> {
                    msReList?.selectedIndex = 2;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_360P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_540P -> {
                    msReList?.selectedIndex = 3;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_480P -> {
                    msReList?.selectedIndex = 4;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_480P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                AlivcResolutionEnum.RESOLUTION_720P -> {
                    msReList?.selectedIndex = 5;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_720P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
                else -> {
                    msReList?.selectedIndex = 4;
                    mAlivcLivePushConfig!!.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setHintTargetBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                    SharedPreferenceUtils.setHintMinBit(Application.application!!.applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                    mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setTargetBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()));
                    SharedPreferenceUtils.setMinBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()))
                    mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))
                    SharedPreferenceUtils.setInitBit(Application.application!!.applicationContext, Integer.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()))

                }
            }
        }

//        turnOnBitRateFps(false)
//        mPushMirror!!.isChecked = SharedPreferenceUtils.isPushMirror(applicationContext)
//        mPreviewMirror!!.isChecked = SharedPreferenceUtils.isPreviewMirror(applicationContext)

//        mAutoFocus!!.isChecked = SharedPreferenceUtils.isAutoFocus(applicationContext)

        //屏幕方向
        var orienTation = AlivcPreviewOrientationEnum.valueOf(SharedPreferenceUtils.getOrienTation(Application.application!!.applicationContext))
        when (orienTation) {
            AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT -> {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT
                mAlivcLivePushConfig?.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
                mAlivcLivePushConfig?.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
                mOrientation?.check(R.id.portrait)
            }
            AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT -> {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT
                mAlivcLivePushConfig?.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
                mAlivcLivePushConfig?.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
                mOrientation?.check(R.id.home_left)
            }
            else -> {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT
                mAlivcLivePushConfig?.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network.png"
                mAlivcLivePushConfig?.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push.png"
                mOrientation?.check(R.id.home_right)
            }
        }
        //直播类型
        var liveType = SharedPreferenceUtils.getLiveType(Application.application!!.applicationContext);
        when (liveType) {
            0 -> {
                mAudioOnlyPush = false
                mVideoOnlyPush = false;
                mAlivcLivePushConfig!!.setAudioOnly(mAudioOnlyPush)
                mAlivcLivePushConfig!!.setVideoOnly(mVideoOnlyPush)
                SharedPreferenceUtils.setLiveType(Application.application!!.applicationContext, 0);
                audio_Type?.selectedIndex = 0;
            }
            1 -> {
                mAudioOnlyPush = false;
                mVideoOnlyPush = true;
                mAlivcLivePushConfig!!.setAudioOnly(mAudioOnlyPush)
                mAlivcLivePushConfig!!.setVideoOnly(mVideoOnlyPush)
                audio_Type?.selectedIndex = 1;
            }
            2 -> {
                mAudioOnlyPush = true;
                mVideoOnlyPush = false;
                mAlivcLivePushConfig!!.setAudioOnly(mAudioOnlyPush)
                mAlivcLivePushConfig!!.setVideoOnly(mVideoOnlyPush)
                audio_Type?.selectedIndex = 2;

            }
        }

        //是否开启 前置摄像头
        mCamera!!.isChecked = (mCameraId == AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId)
        //是否开启美颜
        mBeautyOn!!.isChecked = SharedPreferenceUtils.isBeautyOn(applicationContext)

        //显示模式
        when (SharedPreferenceUtils.getDisplayFit(applicationContext)) {
            AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL.previewDisplayMode -> {
                mDisplayMode!!.check(R.id.full)
            }
            AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT.previewDisplayMode -> {
                mDisplayMode!!.check(R.id.fit)
            }
            AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL.previewDisplayMode -> {
                mDisplayMode!!.check(R.id.cut)
            }
        }
    }

    private fun initView() {
//        mFps = findViewById(R.id.fps_seekbar)
//        mFpsText = findViewById(R.id.fps_text)
//        mTargetRate = findViewById(R.id.target_rate_edit)
//        mMinRate = findViewById(R.id.min_rate_edit)
//        mInitRate = findViewById(R.id.init_rate_edit)
//        mAudioBitRate = findViewById(R.id.audio_bitrate)
//        mRetryInterval = findViewById(R.id.retry_interval)
//        mRetryCount = findViewById(R.id.retry_count)
//        mAuthTime = findViewById(R.id.auth_time)
//        mPrivacyKey = findViewById(R.id.privacy_key)
//        mMinFps = findViewById(R.id.min_fps_seekbar)
//        mMinFpsText = findViewById(R.id.min_fps_text)
//        mWaterMark = findViewById(R.id.watermark_switch)
//        mWaterPosition = findViewById(R.id.water_position)
//        mPushMirror = findViewById(R.id.push_mirror_switch)
//        mPreviewMirror = findViewById(R.id.preview_mirror_switch)
//        mHardCode = findViewById(R.id.hard_switch)
//        mAudioHardCode = findViewById(R.id.audio_hardenc)
        mCamera = findViewById(R.id.camera_switch)
//        mAutoFocus = findViewById(R.id.autofocus_switch)
        mBeautyOn = findViewById(R.id.beautyOn_switch)
//        mAsync = findViewById(R.id.async_switch)
//        mFlash = findViewById(R.id.flash_switch)
//        mLog = findViewById(R.id.log_switch)
//        mBitrate = findViewById(R.id.bitrate_control)
//        mVariableResolution = findViewById(R.id.variable_resolution)
//        mExtern = findViewById(R.id.extern_video)
//        mPauseImage = findViewById(R.id.pause_image)
//        mNetworkImage = findViewById(R.id.network_image)
//        mAudioRadio = findViewById(R.id.main_audio)
        mQualityMode = findViewById(R.id.quality_modes)
//        mGop = findViewById(R.id.main_gop)
        mOrientation = findViewById(R.id.main_orientation)
        mDisplayMode = findViewById(R.id.setting_display_mode)
//         mBeautyLevel = findViewById(R.id.beauty_modes)
//        mAudioProfiles = findViewById(R.id.audio_profiles)

//        mCheekPinkBar = findViewById(R.id.beauty_cheekpink_seekbar)
//        mWhiteBar = findViewById(R.id.beauty_white_seekbar)
//        mSkinBar = findViewById(R.id.beauty_skin_seekbar)
//        mRuddyBar = findViewById(R.id.beauty_ruddy_seekbar)
//        mSlimFaceBar = findViewById(R.id.beauty_thinface_seekbar)
//        mShortenFaceBar = findViewById(R.id.beauty_shortenface_seekbar)
//        mBigEyeBar = findViewById(R.id.beauty_bigeye_seekbar)
//        mCheekpink = findViewById(R.id.cheekpink)
//        mWhite = findViewById(R.id.white)
//        mSkin = findViewById(R.id.skin)
//        mRuddy = findViewById(R.id.ruddy)
//        mSlimFace = findViewById(R.id.thinface)
//        mShortenFace = findViewById(R.id.shortenface)
//        mBigEye = findViewById(R.id.bigeye)
//        mWaterLinear = findViewById(R.id.water_linear)

        msReList = findViewById(R.id.msReList);
//        audioReList = findViewById(R.id.audioReList);
        audio_Type = findViewById(R.id.audio_Type);
//        mTargetRate?.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//        mMinRate?.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//        mInitRate?.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//        SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
//        SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
//        turnOnBitRateFps(false)

        addList();
    }

    private fun addList() {
        msReList!!.setOnItemSelectedListener { view, position, id, item ->
            var list = resources.getStringArray(R.array.resolution_list);
            when (position) {
                0 -> {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_180P
                    mAlivcLivePushConfig!!.setResolution(mDefinition)
                    SharedPreferenceUtils.setDefinition(applicationContext, AlivcResolutionEnum.RESOLUTION_180P.name);
                    when (mAlivcLivePushConfig!!.qualityMode) {
                        AlivcQualityModeEnum.QM_RESOLUTION_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcQualityModeEnum.QM_FLUENCY_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        else -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
                1 -> {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_240P
                    SharedPreferenceUtils.setDefinition(applicationContext, AlivcResolutionEnum.RESOLUTION_240P.name);
                    when (mAlivcLivePushConfig!!.qualityMode) {
                        AlivcQualityModeEnum.QM_RESOLUTION_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcQualityModeEnum.QM_FLUENCY_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        else -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
                2 -> {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_360P
                    SharedPreferenceUtils.setDefinition(applicationContext, AlivcResolutionEnum.RESOLUTION_360P.name);
                    when (mAlivcLivePushConfig!!.qualityMode) {
                        AlivcQualityModeEnum.QM_RESOLUTION_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcQualityModeEnum.QM_FLUENCY_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        else -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
                3 -> {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_480P
                    SharedPreferenceUtils.setDefinition(applicationContext, AlivcResolutionEnum.RESOLUTION_480P.name);
                    when (mAlivcLivePushConfig!!.qualityMode) {
                        AlivcQualityModeEnum.QM_RESOLUTION_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcQualityModeEnum.QM_FLUENCY_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        else -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
                4 -> {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_540P
                    SharedPreferenceUtils.setDefinition(applicationContext, AlivcResolutionEnum.RESOLUTION_540P.name);
                    when (mAlivcLivePushConfig!!.qualityMode) {
                        AlivcQualityModeEnum.QM_RESOLUTION_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcQualityModeEnum.QM_FLUENCY_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        else -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
                5 -> {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_720P
                    SharedPreferenceUtils.setDefinition(applicationContext, AlivcResolutionEnum.RESOLUTION_720P.name);
                    when (mAlivcLivePushConfig!!.qualityMode) {
                        AlivcQualityModeEnum.QM_RESOLUTION_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcQualityModeEnum.QM_FLUENCY_FIRST -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        else -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
            }
        }
//        audioReList!!.setOnItemSelectedListener { view, position, id, item ->
//            when (position) {
//                0 -> {
//                    mAlivcLivePushConfig!!.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000)
//                    SharedPreferenceUtils.setAudioSamepleRate(applicationContext, AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000.audioSampleRate);
//                }
//                1 -> {
//                    mAlivcLivePushConfig!!.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100)
//                    SharedPreferenceUtils.setAudioSamepleRate(applicationContext, AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100.audioSampleRate);
//                }
//                2 -> {
//                    mAlivcLivePushConfig!!.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_48000)
//                    SharedPreferenceUtils.setAudioSamepleRate(applicationContext, AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_48000.audioSampleRate);
//                }
//            }
//
//        }

        audio_Type!!.setOnItemSelectedListener { view, position, id, item ->
            when (position) {
                0 -> {

                    mAudioOnlyPush = false
                    mVideoOnlyPush = false;
                    mAlivcLivePushConfig!!.setAudioOnly(mAudioOnlyPush)
                    mAlivcLivePushConfig!!.setVideoOnly(mVideoOnlyPush)
                    SharedPreferenceUtils.setLiveType(Application.application!!.applicationContext, 0);
                }
                1 -> {
                    mAudioOnlyPush = false;
                    mVideoOnlyPush = true;
                    mAlivcLivePushConfig!!.setAudioOnly(mAudioOnlyPush)
                    mAlivcLivePushConfig!!.setVideoOnly(mVideoOnlyPush)
                    SharedPreferenceUtils.setLiveType(applicationContext, 1);
                }
                2 -> {
                    mAudioOnlyPush = true;
                    mVideoOnlyPush = false;
                    mAlivcLivePushConfig!!.setAudioOnly(mAudioOnlyPush)
                    mAlivcLivePushConfig!!.setVideoOnly(mVideoOnlyPush)
                    audio_Type?.selectedIndex = 2;
                    SharedPreferenceUtils.setLiveType(applicationContext, 2);
                }
            }
        }
    }

    private fun turnOnBitRateFps(on: Boolean) {
        if (!on) {
//            mFps!!.progress = 83
//            mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_25)
//            mFpsText!!.text = AlivcFpsEnum.FPS_25.fps.toString()
//            mTargetRate!!.isFocusable = false
//            mMinRate!!.isFocusable = false
//            mInitRate!!.isFocusable = false
//            mFps!!.isFocusable = false
//            mTargetRate!!.isFocusableInTouchMode = false
//            mMinRate!!.isFocusableInTouchMode = false
//            mInitRate!!.isFocusableInTouchMode = false
//            mFps!!.isFocusableInTouchMode = false
        } else {
//            mTargetRate!!.isFocusable = true
//            mMinRate!!.isFocusable = true
//            mInitRate!!.isFocusable = true
//            mTargetRate!!.isFocusableInTouchMode = true
//            mMinRate!!.isFocusableInTouchMode = true
//            mInitRate!!.isFocusableInTouchMode = true
//            mTargetRate!!.requestFocus()
//            mInitRate!!.requestFocus()
//            mMinRate!!.requestFocus()
        }
    }

    private fun setClick() {
//        mWaterPosition!!.setOnClickListener(onClickListener)
//        mWaterMark!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mPushMirror!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mPreviewMirror!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mHardCode!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mAudioHardCode!!.setOnCheckedChangeListener(onCheckedChangeListener)
        mCamera!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mAutoFocus!!.setOnCheckedChangeListener(onCheckedChangeListener)
        mBeautyOn!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mExtern!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mFps!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mMinFps!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mAsync!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mFlash!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mLog!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mBitrate!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mVariableResolution!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mPauseImage!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mNetworkImage!!.setOnCheckedChangeListener(onCheckedChangeListener)
//        mAudioRadio!!.setOnCheckedChangeListener(mAudioListener)
        mQualityMode!!.setOnCheckedChangeListener(mQualityListener)
//        mGop!!.setOnCheckedChangeListener(mGopListener)
        mOrientation!!.setOnCheckedChangeListener(mOrientationListener)
        mDisplayMode!!.setOnCheckedChangeListener(mDisplayModeListener)
//        mBeautyLevel!!.setOnCheckedChangeListener(mBeautyLevelListener)
//        mAudioProfiles!!.setOnCheckedChangeListener(mAudioProfileListener)
//        mCheekPinkBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mWhiteBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mSkinBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mRuddyBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mSlimFaceBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mShortenFaceBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
//        mBigEyeBar!!.setOnSeekBarChangeListener(onSeekBarChangeListener)
    }

//    private val onClickListener = View.OnClickListener { view ->
//        val id = view.id
//        when (id) {
//
//            R.id.water_position -> {
//                val pushWaterMarkDialog = PushWaterMarkDialog()
//                pushWaterMarkDialog.setWaterMarkInfo(waterMarkInfos)
//                pushWaterMarkDialog.show(this.getSupportFragmentManager(), "waterDialog")
//            }
//            else -> {
//            }
//        }
//    }

    fun getPushConfig(): AlivcLivePushConfig? {

        mAlivcLivePushConfig!!.setResolution(mDefinition)

//        if (!mInitRate!!.text.toString().isEmpty()) {
//            mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(mInitRate!!.text.toString()))
//        } else {
//            mAlivcLivePushConfig!!.setInitialVideoBitrate(Integer.valueOf(mInitRate!!.hint.toString()))
//        }

//        if (!mAudioBitRate!!.text.toString().isEmpty()) {
//            mAlivcLivePushConfig!!.audioBitRate = 1000 * Integer.valueOf(mAudioBitRate!!.text.toString())
//        } else {
//            mAlivcLivePushConfig!!.audioBitRate = 1000 * Integer.valueOf(mAudioBitRate!!.hint.toString())
//        }
//        if (!mMinRate!!.text.toString().isEmpty()) {
//            mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(mMinRate!!.text.toString()))
//            SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(mMinRate!!.text.toString()))
//        } else {
//            mAlivcLivePushConfig!!.setMinVideoBitrate(Integer.valueOf(mMinRate!!.hint.toString()))
//            SharedPreferenceUtils.setMinBit(applicationContext, Integer.valueOf(mMinRate!!.hint.toString()))
//        }
//        if (!mTargetRate!!.text.toString().isEmpty()) {
//            mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(mTargetRate!!.text.toString()))
//            SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(mTargetRate!!.text.toString()))
//        } else {
//            mAlivcLivePushConfig!!.setTargetVideoBitrate(Integer.valueOf(mTargetRate!!.hint.toString()))
//            SharedPreferenceUtils.setTargetBit(applicationContext, Integer.valueOf(mTargetRate!!.hint.toString()))
//        }
//        if (!mRetryCount!!.text.toString().isEmpty()) {
//            mAlivcLivePushConfig!!.setConnectRetryCount(Integer.valueOf(mRetryCount!!.text.toString()))
//        } else {
//            mAlivcLivePushConfig!!.setConnectRetryCount(AlivcLivePushConstants.DEFAULT_VALUE_INT_AUDIO_RETRY_COUNT)
//        }
//        if (!mRetryInterval!!.text.toString().isEmpty()) {
//            mAlivcLivePushConfig!!.setConnectRetryInterval(Integer.valueOf(mRetryInterval!!.text.toString()))
//        } else {
//            mAlivcLivePushConfig!!.setConnectRetryInterval(AlivcLivePushConstants.DEFAULT_VALUE_INT_RETRY_INTERVAL)
//        }
//        if (mWaterMark!!.isChecked) {
//            for (i in waterMarkInfos.indices) {
//                mAlivcLivePushConfig!!.addWaterMark(waterMarkInfos[i].mWaterMarkPath, waterMarkInfos[i].mWaterMarkCoordX, waterMarkInfos[i].mWaterMarkCoordY, waterMarkInfos[i].mWaterMarkWidth)
//            }
//        }
//        mAuthTimeStr = mAuthTime!!.text.toString()
//        mPrivacyKeyStr = mPrivacyKey!!.text.toString()
        return mAlivcLivePushConfig
    }

    private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        val id = buttonView.id
//        if (id == R.id.watermark_switch) {
//            if (mWaterPosition != null) {
//                mWaterPosition!!.isClickable = isChecked
//                mWaterPosition!!.setTextColor(if (isChecked) resources.getColor(R.color.colorPrimary) else resources.getColor(R.color.color_gray))
//            }
//        } else
        if (id == R.id.push_mirror_switch) {
            mAlivcLivePushConfig!!.setPushMirror(isChecked)
            SharedPreferenceUtils.setPushMirror(applicationContext, isChecked)
        } else if (id == R.id.preview_mirror_switch) {
            mAlivcLivePushConfig!!.setPreviewMirror(isChecked)
            SharedPreferenceUtils.setPreviewMirror(applicationContext, isChecked)
        } else
//                if (id == R.id.hard_switch) {
//            mAlivcLivePushConfig!!.setVideoEncodeMode(if (isChecked) AlivcEncodeModeEnum.Encode_MODE_HARD else AlivcEncodeModeEnum.Encode_MODE_SOFT)
//        } else if (id == R.id.audio_hardenc) {
//            mAlivcLivePushConfig!!.setAudioEncodeMode(if (isChecked) AlivcEncodeModeEnum.Encode_MODE_HARD else AlivcEncodeModeEnum.Encode_MODE_SOFT)
//        } else

            if (id == R.id.camera_switch) {
                mAlivcLivePushConfig!!.setCameraType(if (isChecked) AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT else AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK)
                mCameraId = if (isChecked) AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId else AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK.cameraId

            } else if (id == R.id.autofocus_switch) {
                mAlivcLivePushConfig!!.setAutoFocus(isChecked)
                SharedPreferenceUtils.setAutofocus(applicationContext, isChecked)

            } else if (id == R.id.beautyOn_switch) {
                mAlivcLivePushConfig!!.setBeautyOn(isChecked)
                SharedPreferenceUtils.setBeautyOn(applicationContext, isChecked)
            }
//            else  if (id == R.id.async_switch) {
//                mAsyncValue = isChecked
//            } else if (id == R.id.flash_switch) {
//                mAlivcLivePushConfig!!.setFlash(isChecked)
//                isFlash = isChecked
//            } else if (id == R.id.log_switch) {
//                if (isChecked) {
//                    LogcatHelper.getInstance(applicationContext).start()
//                } else {
//                    LogcatHelper.getInstance(applicationContext).stop()
//                }
//            } else if (id == R.id.bitrate_control) {
//                mAlivcLivePushConfig!!.isEnableAutoResolution = isChecked
//            } else if (id == R.id.variable_resolution) {
//                mAlivcLivePushConfig!!.isEnableAutoResolution = isChecked
//            } else if (id == R.id.extern_video) {
//                mAlivcLivePushConfig!!.setExternMainStream(isChecked, AlivcImageFormat.IMAGE_FORMAT_YUVNV12, AlivcSoundFormat.SOUND_FORMAT_S16)
//                mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE)
//                mAlivcLivePushConfig!!.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100)
//            } else if (id == R.id.pause_image) {
//                if (!isChecked) {
//                    mAlivcLivePushConfig!!.pausePushImage = ""
//                } else {
//                    if (mAlivcLivePushConfig!!.previewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.orientation || mAlivcLivePushConfig!!.previewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.orientation) {
//                        mAlivcLivePushConfig!!.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
//                    } else {
//                        mAlivcLivePushConfig!!.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push.png"
//                    }
//                }
//            } else if (id == R.id.network_image) {
//                if (!isChecked) {
//                    mAlivcLivePushConfig!!.networkPoorPushImage = ""
//                } else {
//                    if (mAlivcLivePushConfig!!.previewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.orientation || mAlivcLivePushConfig!!.previewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.orientation) {
//                        mAlivcLivePushConfig!!.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
//                    } else {
//                        mAlivcLivePushConfig!!.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network.png"
//                    }
//                }
//            }
    }

    private val onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val seekBarId = seekBar.id
//            if (mFps!!.id == seekBarId) {
//                if (mAlivcLivePushConfig!!.qualityMode != AlivcQualityModeEnum.QM_CUSTOM) {
//                    mFps!!.progress = 83
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_25)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_25.fps.toString()
//                    return
//                }
//                if (progress <= PROGRESS_0) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_8)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_8.fps.toString()
//                } else if (progress in (PROGRESS_0 + 1)..PROGRESS_16) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_10)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_10.fps.toString()
//                } else if (progress in (PROGRESS_16 + 1)..PROGRESS_33) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_12)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_12.fps.toString()
//                } else if (progress in (PROGRESS_33 + 1)..PROGRESS_50) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_15)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_15.fps.toString()
//                } else if (progress in (PROGRESS_50 + 1)..PROGRESS_66) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_20)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_20.fps.toString()
//                } else if (progress in (PROGRESS_66 + 1)..PROGRESS_80) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_25)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_25.fps.toString()
//                } else if (progress > PROGRESS_80) {
//                    mAlivcLivePushConfig!!.setFps(AlivcFpsEnum.FPS_30)
//                    mFpsText!!.text = AlivcFpsEnum.FPS_30.fps.toString()
//                }
//            } else

//                if (mMinFps!!.id == seekBarId) {
//                when {
//                    progress <= PROGRESS_0 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_8)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_8.fps.toString();
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_8.name);
//                    }
//                    progress in (PROGRESS_0 + 1)..PROGRESS_16 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_10)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_10.fps.toString()
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_10.name);
//                    }
//                    progress in (PROGRESS_16 + 1)..PROGRESS_33 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_12)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_12.fps.toString()
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_12.name);
//                    }
//                    progress in (PROGRESS_33 + 1)..PROGRESS_50 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_15)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_15.fps.toString()
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_15.name);
//                    }
//                    progress in (PROGRESS_50 + 1)..PROGRESS_66 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_20)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_20.fps.toString()
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_20.name);
//                    }
//                    progress in (PROGRESS_66 + 1)..PROGRESS_80 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_25)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_25.fps.toString()
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_25.name);
//                    }
//                    progress > PROGRESS_80 -> {
//                        mAlivcLivePushConfig!!.setMinFps(AlivcFpsEnum.FPS_30)
//                        mMinFpsText!!.text = AlivcFpsEnum.FPS_30.fps.toString()
//                        SharedPreferenceUtils.setFpsBit(applicationContext, AlivcFpsEnum.FPS_30.name);
//                    }
//                }
//            } else
//            if (mCheekPinkBar!!.id == seekBarId) {
//                mCheekpink!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.setBeautyCheekPink(progress)
//                    SharedPreferenceUtils.setCheekPink(applicationContext, progress)
//                }
//            } else if (mWhiteBar!!.id == seekBarId) {
//                mWhite!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.setBeautyWhite(progress)
//                    SharedPreferenceUtils.setWhiteValue(applicationContext, progress)
//                }
//            } else if (mSkinBar!!.id == seekBarId) {
//                mSkin!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.setBeautyBuffing(progress)
//                    SharedPreferenceUtils.setBuffing(applicationContext, progress)
//                }
//            } else if (mRuddyBar!!.id == seekBarId) {
//                mRuddy!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.setBeautyRuddy(progress)
//                    SharedPreferenceUtils.setRuddy(applicationContext, progress)
//                }
//            } else if (mSlimFaceBar!!.id == seekBarId) {
//                mSlimFace!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.beautyThinFace = progress
//                    SharedPreferenceUtils.setSlimFace(applicationContext, progress)
//                }
//            } else if (mShortenFaceBar!!.id == seekBarId) {
//                mShortenFace!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.beautyShortenFace = progress
//                    SharedPreferenceUtils.setShortenFace(applicationContext, progress)
//                }
//            } else if (mBigEyeBar!!.id == seekBarId) {
//                mBigEye!!.text = progress.toString()
//                if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.beautyBigEye = progress
//                    SharedPreferenceUtils.setBigEye(applicationContext, progress)
//                }
//            }
        }


//        private val mAudioListener = RadioGroup.OnCheckedChangeListener { _, i ->
//            when (i) {
//                R.id.audio_channel_one -> if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE)
//                }
//                R.id.audio_channel_two -> if (mAlivcLivePushConfig != null) {
//                    mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO)
//                }
//                else -> {
//                }
//            }
//        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}
        override fun onStopTrackingTouch(seekBar: SeekBar) {
//            val progress = seekBar.progress
//            if (mFps!!.id == seekBar.id) {
//                when {
//                    progress <= PROGRESS_0 -> {
//                        seekBar.progress = 0
//                    }
//                    progress in (PROGRESS_0 + 1)..PROGRESS_16 -> {
//                        seekBar.progress = PROGRESS_16
//                    }
//                    progress in (PROGRESS_16 + 1)..PROGRESS_33 -> {
//                        seekBar.progress = PROGRESS_33
//                    }
//                    progress in (PROGRESS_33 + 1)..PROGRESS_50 -> {
//                        seekBar.progress = PROGRESS_50
//                    }
//                    progress in (PROGRESS_50 + 1)..PROGRESS_66 -> {
//                        seekBar.progress = PROGRESS_66
//                    }
//                    progress in (PROGRESS_66 + 1)..PROGRESS_80 -> {
//                        seekBar.progress = PROGRESS_80
//                    }
//                    progress > PROGRESS_80 -> {
//                        seekBar.progress = PROGRESS_100
//                    }
//                }
//            }
        }
    }

//    private val mAudioListener = RadioGroup.OnCheckedChangeListener { radioGroup, i ->
//        when (i) {
//            R.id.audio_channel_one -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE)
//                SharedPreferenceUtils.setAudioChannels(applicationContext, AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE.channelCount);
//            }
//            R.id.audio_channel_two -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO)
//                SharedPreferenceUtils.setAudioChannels(applicationContext, AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO.channelCount);
//            }
//            else -> {
//            }
//        }
//    }


    private val mQualityListener = RadioGroup.OnCheckedChangeListener { radioGroup, i ->
        when (i) {
            R.id.resolution_first -> {
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig!!.qualityMode = AlivcQualityModeEnum.QM_RESOLUTION_FIRST
                    SharedPreferenceUtils.setQualityMode(applicationContext, AlivcQualityModeEnum.QM_RESOLUTION_FIRST.qualityMode);
                    when (mDefinition) {
                        AlivcResolutionEnum.RESOLUTION_180P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_240P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_360P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_480P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_540P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_720P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
//                turnOnBitRateFps(false)
            }
            R.id.fluency_first -> {
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig!!.qualityMode = AlivcQualityModeEnum.QM_FLUENCY_FIRST
                    SharedPreferenceUtils.setQualityMode(applicationContext, AlivcQualityModeEnum.QM_FLUENCY_FIRST.qualityMode);
                    when (mDefinition) {
                        AlivcResolutionEnum.RESOLUTION_180P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_240P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_360P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_480P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_540P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                        AlivcResolutionEnum.RESOLUTION_720P -> {
//                            mTargetRate!!.hint = AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate.toString()
//                            mMinRate!!.hint = AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate.toString()
//                            mInitRate!!.hint = AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.bitrate.toString()
                            SharedPreferenceUtils.setHintTargetBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.bitrate)
                            SharedPreferenceUtils.setHintMinBit(applicationContext, AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.bitrate)
                        }
                    }
                }
//                turnOnBitRateFps(false)
            }
            else -> {
            }
        }
    }


    //    private val mGopListener = RadioGroup.OnCheckedChangeListener { radioGroup, i ->
//        when (i) {
//            R.id.gop_one -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_ONE)
//            }
//            R.id.gop_two -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_TWO)
//            }
//            R.id.gop_three -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_THREE)
//            }
//            R.id.gop_four -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_FOUR)
//            }
//            R.id.gop_five -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.setVideoEncodeGop(AlivcVideoEncodeGopEnum.GOP_FIVE)
//            }
//            else -> {
//            }
//        }
//    }
    private val mOrientationListener = RadioGroup.OnCheckedChangeListener { _, i ->
        when (i) {
            R.id.portrait -> if (mAlivcLivePushConfig != null) {
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
            R.id.home_left -> if (mAlivcLivePushConfig != null) {
                mAlivcLivePushConfig!!.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT)
                mOrientationEnum = AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT
                if (mAlivcLivePushConfig!!.pausePushImage != null && mAlivcLivePushConfig!!.pausePushImage != "") {
                    mAlivcLivePushConfig!!.pausePushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/background_push_land.png"
                }
                if (mAlivcLivePushConfig!!.networkPoorPushImage != null && mAlivcLivePushConfig!!.networkPoorPushImage != "") {
                    mAlivcLivePushConfig!!.networkPoorPushImage = Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/poor_network_land.png"
                }
                SharedPreferenceUtils.setOrienTation(applicationContext, AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.name)
            }
            R.id.home_right -> if (mAlivcLivePushConfig != null) {
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
            else -> {
            }
        }
    }


    private val mDisplayModeListener = RadioGroup.OnCheckedChangeListener { radioGroup, i ->
        when (i) {
            R.id.full -> if (mAlivcLivePushConfig != null) {
                mAlivcLivePushConfig!!.previewDisplayMode = AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL
                SharedPreferenceUtils.setDisplayFit(applicationContext, AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL
                        .previewDisplayMode)
            }
            R.id.fit -> if (mAlivcLivePushConfig != null) {
                mAlivcLivePushConfig!!.previewDisplayMode = AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT
                SharedPreferenceUtils.setDisplayFit(applicationContext, AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT
                        .previewDisplayMode)
            }
            R.id.cut -> if (mAlivcLivePushConfig != null) {
                mAlivcLivePushConfig!!.previewDisplayMode = AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL
                SharedPreferenceUtils.setDisplayFit(applicationContext, AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL
                        .previewDisplayMode)
            }
            else -> {
            }
        }
    }


//    private val mBeautyLevelListener = RadioGroup.OnCheckedChangeListener { radioGroup, i ->
//        when (i) {
//            R.id.beauty_normal -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.beautyLevel = AlivcBeautyLevelEnum.BEAUTY_Normal
//            }
//            R.id.beauty_professional -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.beautyLevel = AlivcBeautyLevelEnum.BEAUTY_Professional
//            }
//            else -> {
//            }
//        }
//    }

//    private val mAudioProfileListener = RadioGroup.OnCheckedChangeListener { _, i ->
//        when (i) {
//            R.id.audio_profile_lc -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.audioProfile = AlivcAudioAACProfileEnum.AAC_LC
//                SharedPreferenceUtils.setAudioProFile(applicationContext, AlivcAudioAACProfileEnum.AAC_LC.audioProfile);
//            }
//            R.id.audio_profile_he -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.audioProfile = AlivcAudioAACProfileEnum.HE_AAC
//                SharedPreferenceUtils.setAudioProFile(applicationContext, AlivcAudioAACProfileEnum.HE_AAC.audioProfile);
//            }
//            R.id.audio_profile_hev2 -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.audioProfile = AlivcAudioAACProfileEnum.HE_AAC_v2
//                SharedPreferenceUtils.setAudioProFile(applicationContext, AlivcAudioAACProfileEnum.HE_AAC_v2.audioProfile);
//            }
//            R.id.audio_profile_ld -> if (mAlivcLivePushConfig != null) {
//                mAlivcLivePushConfig!!.audioProfile = AlivcAudioAACProfileEnum.AAC_LD
//                SharedPreferenceUtils.setAudioProFile(applicationContext, AlivcAudioAACProfileEnum.AAC_LD.audioProfile);
//            }
//            else -> {
//            }
//        }
//    }

    private fun addWaterMarkInfo() {
        //添加三个水印，位置坐标不同
        val waterMarkInfo = WaterMarkInfo()
        waterMarkInfo.mWaterMarkPath = Common.waterMark
        val waterMarkInfo1 = WaterMarkInfo()
        waterMarkInfo1.mWaterMarkPath = Common.waterMark
        waterMarkInfo.mWaterMarkCoordY += 0.2f
        val waterMarkInfo2 = WaterMarkInfo()
        waterMarkInfo2.mWaterMarkPath = Common.waterMark
        waterMarkInfo2.mWaterMarkCoordY += 0.4f
        waterMarkInfos.add(waterMarkInfo)
        waterMarkInfos.add(waterMarkInfo1)
        waterMarkInfos.add(waterMarkInfo2)
    }

    override fun clearData() {
        getPushConfig();
    }


    override fun loadView(): Int {
        return R.layout.activity_live_config;
    }
}
