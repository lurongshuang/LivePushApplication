package com.lrs.livepushapplication.activity.live.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.alivc.live.pusher.*
import com.bumptech.glide.Glide
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.activity.live.LiveConfigActivity
import com.lrs.livepushapplication.activity.live.LivePushActivity
import com.lrs.livepushapplication.activity.live.LivePushActivity.PauseState
import com.lrs.livepushapplication.activity.live.dialog.MusicDialog
import com.lrs.livepushapplication.activity.live.dialog.PushAnswerGameDialog
import com.lrs.livepushapplication.activity.live.dialog.PushBeautyDialog
import com.lrs.livepushapplication.activity.live.dialog.PushMoreDialog
import com.lrs.livepushapplication.application.Application
import com.lrs.livepushapplication.utils.net.ToastUtils
import com.lrs.livepushapplication.utils.sharedpreferences.SharedPreferenceUtils
import org.apache.commons.lang3.concurrent.BasicThreadFactory
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.experimental.and

class LivePushFragment : Fragment(), Runnable {
    private val REFRESH_INTERVAL: Long = 2000
    private var mExit: ImageView? = null
    private var ivSeting: ImageView? = null
    private var mMusic: ImageView? = null
    private var mFlash: ImageView? = null
    private var mCamera: ImageView? = null
    private var mSnapshot: ImageView? = null
    private var mBeautyButton: ImageView? = null
    private var mAnswer: TextView? = null
    private var mTopBar: LinearLayout? = null
    private var mUrl: TextView? = null
    private var mIsPushing: TextView? = null
    private var mGuide: LinearLayout? = null
    private var mPreviewButton: Button? = null
    private var mPushButton: Button? = null
    private var mOperaButton: Button? = null
    private var mMore: Button? = null
    private var mRestartButton: Button? = null
    private var mAlivcLivePusher: AlivcLivePusher? = null
    private var mPushUrl: String? = null
    private var mSurfaceView: SurfaceView? = null
    private var mAsync = false
    private var mAudio = false
    private var mVideoOnly = false
    private var isPushing = false
    private val mHandler = Handler()
    private var mStateListener: PauseState? = null
    private var mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
    private var isFlash = false
    private var mMixExtern = false
    private var mMixMain = false
    private var flashState = true
    private val snapshotCount = 0
    private var mQualityMode = 0
    var mExecutorService: ScheduledExecutorService? = ScheduledThreadPoolExecutor(5,
            BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build())
    private val videoThreadOn = false
    private val videoThreadOn2 = false
    private val videoThreadOn3 = false
    private var audioThreadOn = false
    private var mMusicDialog: MusicDialog? = null
    private val mAuthString = "?auth_key=%1\$d-%2\$d-%3\$d-%4\$s"
    private val mMd5String = "%1\$s-%2\$d-%3\$d-%4\$d-%5\$s"
    private var mTempUrl: String? = null
    private var mAuthTime: String? = ""
    private var mPrivacyKey: String? = ""
    private var ivAudio: ImageView? = null
    var mDynamicals = Vector<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPushUrl = arguments!!.getString(URL_KEY)
            mTempUrl = mPushUrl
        }
        initAli()
//        if (mMixExtern) {
//            //startYUV(getActivity());
//            //startYUV2(getActivity());
//            //startYUV3(getActivity());
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.push_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mExit = view.findViewById<View>(R.id.exit) as ImageView
        ivSeting = view.findViewById<View>(R.id.ivSeting) as ImageView
        mMusic = view.findViewById<View>(R.id.music) as ImageView
        mFlash = view.findViewById<View>(R.id.flash) as ImageView
        mFlash!!.isSelected = isFlash
        mCamera = view.findViewById<View>(R.id.camera) as ImageView
        mSnapshot = view.findViewById<View>(R.id.snapshot) as ImageView
        mCamera!!.isSelected = true
        mSnapshot!!.isSelected = true
        mPreviewButton = view.findViewById<View>(R.id.preview_button) as Button
        mPreviewButton!!.isSelected = false
        mPushButton = view.findViewById(R.id.push_button)
        mPushButton?.tag = true
        mOperaButton = view.findViewById<View>(R.id.opera_button) as Button
        mOperaButton!!.isSelected = false
        mMore = view.findViewById<View>(R.id.more) as Button
        mBeautyButton = view.findViewById<View>(R.id.beauty_button) as ImageView
        mBeautyButton!!.isSelected = SharedPreferenceUtils.isBeautyOn(activity!!.applicationContext)
        mAnswer = view.findViewById<View>(R.id.answer_button) as TextView
        mRestartButton = view.findViewById<View>(R.id.restart_button) as Button
        mTopBar = view.findViewById<View>(R.id.top_bar) as LinearLayout
        mUrl = view.findViewById<View>(R.id.push_url) as TextView
        mUrl!!.text = mPushUrl
        mIsPushing = view.findViewById<View>(R.id.isPushing) as TextView
        mIsPushing!!.text = isPushing.toString()
        mGuide = view.findViewById<View>(R.id.guide) as LinearLayout
        ivAudio = view.findViewById(R.id.ivAudio)
        mExit!!.setOnClickListener(onClickListener)
        ivSeting!!.setOnClickListener(onClickListener)
        mMusic!!.setOnClickListener(onClickListener)
        mFlash!!.setOnClickListener(onClickListener)
        mCamera!!.setOnClickListener(onClickListener)
        mSnapshot!!.setOnClickListener(onClickListener)
        mPreviewButton!!.setOnClickListener(onClickListener)
        mPushButton?.setOnClickListener(onClickListener)
        mOperaButton!!.setOnClickListener(onClickListener)
        mBeautyButton!!.setOnClickListener(onClickListener)
        mAnswer!!.setOnClickListener(onClickListener)
        mRestartButton!!.setOnClickListener(onClickListener)
        mMore!!.setOnClickListener(onClickListener)
        initStateIt()
    }

    private fun initAli() {
        mAsync = Application.mAsyncValue
        mAudio = Application.mAudioOnlyPush
        mVideoOnly = Application.mVideoOnlyPush
        mCameraId = mCameraId
        isFlash = Application.isFlash
        flashState = isFlash
        mMixExtern = Application.mMixStream
        mMixMain = Application.mAlivcLivePushConfig!!.isExternMainStream
        mAuthTime = Application.mAuthTimeStr
        mPrivacyKey = Application.mPrivacyKeyStr
        mQualityMode = Application.mAlivcLivePushConfig!!.qualityMode.qualityMode

        if (mAlivcLivePusher == null) {
            mAlivcLivePusher = (activity as LivePushActivity?)!!.livePusher
        }
        mAlivcLivePusher!!.setLivePushInfoListener(mPushInfoListener)
        mAlivcLivePusher!!.setLivePushErrorListener(mPushErrorListener)
        mAlivcLivePusher!!.setLivePushNetworkListener(mPushNetworkListener)
        mAlivcLivePusher!!.setLivePushBGMListener(mPushBGMListener)
        isPushing = mAlivcLivePusher!!.isPushing
    }

    private fun initStateIt() {
        //        if(SharedPreferenceUtils.isGuide(getActivity().getApplicationContext())) {
//            mGuide.setVisibility(View.VISIBLE);
//            mGuide.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View view, MotionEvent motionEvent) {
//                    if(mGuide != null) {
//                        mGuide.setVisibility(View.GONE);
//                        SharedPreferenceUtils.setGuide(getActivity().getApplicationContext(), false);
//                    }
//                    return false;
//                }
//            });
//        }
        if (mVideoOnly) {
            mMusic!!.visibility = View.GONE
        }
        //        if (mAudio) {
//            mPreviewButton.setVisibility(View.GONE);
//        }
        if (mAudio) {
            ivAudio!!.visibility = View.VISIBLE
            ivAudio!!.setBackgroundColor(resources.getColor(R.color.black))
            //            String url = "https://wimg.588ku.com/gif620/19/07/09/033c2b5209be8cc765a6f6e9cbac0105.gif";
            //.asGif().diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            val url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1587116212404&di=17ff50544eb2a80754e282ce05ee47a7&imgtype=0&src=http%3A%2F%2Fimg1.juimg.com%2F160919%2F328221-16091913151281.jpg"
            Glide.with(this).load(url).error(R.drawable.ic_logo).into(ivAudio!!)
        } else {
            ivAudio!!.visibility = View.GONE
        }
        if (mMixMain) {
            mBeautyButton!!.visibility = View.GONE
            mMusic!!.visibility = View.GONE
            mFlash!!.visibility = View.GONE
            mCamera!!.visibility = View.GONE
        }
        //        mMore.setVisibility(mAudio ? View.GONE : View.VISIBLE);
//        mTopBar.setVisibility(mAudio ? View.GONE : View.VISIBLE);
        mBeautyButton!!.visibility = if (mAudio) View.GONE else View.VISIBLE
        mFlash!!.visibility = if (mAudio) View.GONE else View.VISIBLE
        mCamera!!.visibility = if (mAudio) View.GONE else View.VISIBLE
        mFlash!!.isClickable = if (mCameraId == AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId) false else true
    }

    var onClickListener = View.OnClickListener { view ->
        val id = view.id
        mExecutorService!!.execute {
            try {
                when (id) {
                    R.id.exit -> activity!!.runOnUiThread { activity!!.onKeyDown(KeyEvent.KEYCODE_BACK, KeyEvent(1, 1)) }
                    R.id.ivSeting -> {
                        val intent = Intent(activity, LiveConfigActivity::class.java)
                        activity!!.startActivity(intent)
                    }
                    R.id.music -> {
                        if (mMusicDialog == null) {
                            mMusicDialog = MusicDialog.newInstance()
                            mMusicDialog?.setAlivcLivePusher(mAlivcLivePusher)
                        }
                        mMusicDialog!!.show(fragmentManager!!, "beautyDialog")
                    }
                    R.id.flash -> {
                        mAlivcLivePusher!!.setFlash(!mFlash!!.isSelected)
                        flashState = !mFlash!!.isSelected
                        mFlash!!.post { mFlash!!.isSelected = !mFlash!!.isSelected }
                    }
                    R.id.camera -> {
                        mCameraId = if (mCameraId == AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId) {
                            AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK.cameraId
                        } else {
                            AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId
                        }
                        mAlivcLivePusher!!.switchCamera()
                        mFlash!!.post {
                            mFlash!!.isClickable = if (mCameraId == AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId) false else true
                            if (mCameraId == AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.cameraId) {
                                mFlash!!.isSelected = false
                            } else {
                                mFlash!!.isSelected = flashState
                            }
                        }
                    }
                    R.id.preview_button -> {
                        val isPreview = mPreviewButton!!.isSelected
                        if (mSurfaceView == null && activity != null) {
                            mSurfaceView = (activity as LivePushActivity?)!!.previewView
                        }
                        if (!isPreview) {
                            mAlivcLivePusher!!.stopPreview()
                            //stopYUV();
                        } else {
                            if (mAsync) {
                                mAlivcLivePusher!!.startPreviewAysnc(mSurfaceView)
                            } else {
                                mAlivcLivePusher!!.startPreview(mSurfaceView)
                            }
                            /*if(mMixExtern) {
                                        startYUV(getActivity());
                                    }*/
                        }
                        mPreviewButton!!.post {
                            mPreviewButton!!.text = if (isPreview) getString(R.string.stop_preview_button) else getString(R.string.start_preview_button)
                            mPreviewButton!!.isSelected = !isPreview
                        }
                    }
                    R.id.push_button -> {
                        val isPush = mPushButton!!.tag as Boolean
                        if (isPush) {
                            if (mAsync) {
                                mAlivcLivePusher!!.startPushAysnc(getAuthString(mAuthTime))
                            } else {
                                mAlivcLivePusher!!.startPush(getAuthString(mAuthTime))
                            }
                            if (mMixExtern) {
                                //startMixPCM(getActivity());
                            } else if (mMixMain) {
                                startPCM(activity)
                            }
                        } else {
                            mAlivcLivePusher!!.stopPush()
                            stopPcm()
                            mOperaButton!!.post {
                                mOperaButton!!.text = getString(R.string.pause_button)
                                mOperaButton!!.isSelected = false
                            }
                            if (mStateListener != null) {
                                mStateListener!!.updatePause(false)
                            }
                        }
                        mPushButton!!.post {
                            mPushButton!!.text = if (isPush) getString(R.string.stop_button) else getString(R.string.start_button)
                            mPushButton!!.tag = !isPush
                        }
                    }
                    R.id.opera_button -> {
                        val isPause = mOperaButton!!.isSelected
                        if (!isPause) {
                            mAlivcLivePusher!!.pause()
                        } else {
                            if (mAsync) {
                                mAlivcLivePusher!!.resumeAsync()
                            } else {
                                mAlivcLivePusher!!.resume()
                            }
                        }
                        if (mStateListener != null) {
                            mStateListener!!.updatePause(!isPause)
                        }
                        mOperaButton!!.post {
                            mOperaButton!!.text = if (isPause) getString(R.string.pause_button) else getString(R.string.resume_button)
                            mOperaButton!!.isSelected = !isPause
                        }
                    }
                    R.id.beauty_button -> {
                        val pushBeautyDialog = PushBeautyDialog.newInstance(mBeautyButton!!.isSelected)
                        pushBeautyDialog.setAlivcLivePusher(mAlivcLivePusher)
                        pushBeautyDialog.setBeautyListener(mBeautyListener)
                        pushBeautyDialog.show(fragmentManager!!, "beautyDialog")
                    }
                    R.id.answer_button -> {
                        val pushAnswerGameDialog = PushAnswerGameDialog.newInstance()
                        pushAnswerGameDialog.setAlivcLivePusher(mAlivcLivePusher)
                        pushAnswerGameDialog.show(fragmentManager!!, "answerDialog")
                    }
                    R.id.restart_button ->                                 /*if(mMixExtern) {
                                    stopYUV();
                                    stopPcm();
                                }*/if (mAsync) {
                        mAlivcLivePusher!!.restartPushAync()
                    } else {
                        mAlivcLivePusher!!.restartPush()
                    }
                    R.id.more -> {
                        val pushMoreDialog = PushMoreDialog()
                        pushMoreDialog.setAlivcLivePusher(mAlivcLivePusher, object : DynamicListern {
                            override fun onAddDynamic() {
                                if (mAlivcLivePusher != null && mDynamicals.size < 5) {
                                    val startX = 0.1f + mDynamicals.size * 0.2f
                                    val startY = 0.1f + mDynamicals.size * 0.2f
                                    val id = mAlivcLivePusher!!.addDynamicsAddons(Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/qizi/", startX, startY, 0.2f, 0.2f)
                                    if (id > 0) {
                                        mDynamicals.add(id)
                                    } else {
                                        ToastUtils.makeToast(getString(R.string.add_dynamic_failed) + id)
                                    }
                                }
                            }

                            override fun onRemoveDynamic() {
                                if (mDynamicals.size > 0) {
                                    val id = mDynamicals[0]
                                    mAlivcLivePusher!!.removeDynamicsAddons(id)
                                    mDynamicals.removeAt(0)
                                }
                            }
                        })
                        pushMoreDialog.setQualityMode(mQualityMode)
                        pushMoreDialog.setPushUrl(mPushUrl)
                        pushMoreDialog.show(fragmentManager!!, "moreDialog")
                    }
                    R.id.snapshot -> mAlivcLivePusher!!.snapshot(1, 0) { bmp ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SS").format(Date())
                        val f = File("/sdcard/", "snapshot-$dateFormat.png")
                        if (f.exists()) {
                            f.delete()
                        }
                        try {
                            val out = FileOutputStream(f)
                            bmp.compress(CompressFormat.PNG, 90, out)
                            out.flush()
                            out.close()
                        } catch (e: FileNotFoundException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        } catch (e: IOException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()
                        }
                        showDialog("截图已保存：/sdcard/snapshot-$dateFormat.png")
                    }
                    else -> {
                    }
                }
            } catch (e: IllegalArgumentException) {
                showDialog(e.message)
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                showDialog(e.message)
                e.printStackTrace()
            }
        }
    }

    fun exit() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage("退出并且停止直播")
        builder.setTitle("确认退出吗？")
        builder.setPositiveButton("确认") { dialog, which -> activity!!.finish() }
        builder.setNegativeButton("取消") { dialog, which -> dialog.dismiss() }
        builder.show()
    }

    fun setAlivcLivePusher(alivcLivePusher: AlivcLivePusher?) {
        mAlivcLivePusher = alivcLivePusher
    }

    fun setStateListener(listener: PauseState?) {
        mStateListener = listener
    }

    fun setSurfaceView(surfaceView: SurfaceView?) {
        mSurfaceView = surfaceView
    }

    var mPushInfoListener: AlivcLivePushInfoListener = object : AlivcLivePushInfoListener {
        override fun onPreviewStarted(pusher: AlivcLivePusher) {
//            showToast(getString(R.string.start_preview));
        }

        override fun onPreviewStoped(pusher: AlivcLivePusher) {
            showToast(getString(R.string.stop_preview))
        }

        override fun onPushStarted(pusher: AlivcLivePusher) {
            showToast(getString(R.string.start_push))
            setButtonVi(View.GONE)
        }

        override fun onFirstAVFramePushed(pusher: AlivcLivePusher) {}
        override fun onPushPauesed(pusher: AlivcLivePusher) {
//            showToast(getString(R.string.pause_push));
        }

        override fun onPushResumed(pusher: AlivcLivePusher) {
//            showToast(getString(R.string.resume_push));
        }

        override fun onPushStoped(pusher: AlivcLivePusher) {
            showToast(getString(R.string.stop_push))
        }

        /**
         * 推流重启通知
         *
         * @param pusher AlivcLivePusher实例
         */
        override fun onPushRestarted(pusher: AlivcLivePusher) {
            showToast(getString(R.string.restart_success))
        }

        override fun onFirstFramePreviewed(pusher: AlivcLivePusher) {
//            showToast(getString(R.string.first_frame));
        }

        override fun onDropFrame(pusher: AlivcLivePusher, countBef: Int, countAft: Int) {
//            showToast(getString(R.string.drop_frame) + ", 丢帧前：" + countBef + ", 丢帧后：" + countAft);
        }

        override fun onAdjustBitRate(pusher: AlivcLivePusher, curBr: Int, targetBr: Int) {
//            showToast(getString(R.string.adjust_bitrate) + ", 当前码率：" + curBr + "Kps, 目标码率：" + targetBr + "Kps");
        }

        override fun onAdjustFps(pusher: AlivcLivePusher, curFps: Int, targetFps: Int) {
//            showToast(getString(R.string.adjust_fps) + ", 当前帧率：" + curFps + ", 目标帧率：" + targetFps);
        }
    }
    var mPushErrorListener: AlivcLivePushErrorListener = object : AlivcLivePushErrorListener {
        override fun onSystemError(livePusher: AlivcLivePusher, error: AlivcLivePushError) {
            showDialog(getString(R.string.system_error) + error.toString())
        }

        override fun onSDKError(livePusher: AlivcLivePusher, error: AlivcLivePushError) {
            if (error != null) {
                showDialog(getString(R.string.sdk_error) + error.toString())
            }
        }
    }
    var mPushNetworkListener: AlivcLivePushNetworkListener = object : AlivcLivePushNetworkListener {
        override fun onNetworkPoor(pusher: AlivcLivePusher) {
            showNetWorkDialog(getString(R.string.network_poor))
        }

        override fun onNetworkRecovery(pusher: AlivcLivePusher) {
            showToast(getString(R.string.network_recovery))
        }

        override fun onReconnectStart(pusher: AlivcLivePusher) {
            showToastShort(getString(R.string.reconnect_start))
        }

        override fun onReconnectFail(pusher: AlivcLivePusher) {
            showDialog(getString(R.string.reconnect_fail))
        }

        override fun onReconnectSucceed(pusher: AlivcLivePusher) {
            showToast(getString(R.string.reconnect_success))
        }

        override fun onSendDataTimeout(pusher: AlivcLivePusher) {
            showDialog(getString(R.string.senddata_timeout))
        }

        override fun onConnectFail(pusher: AlivcLivePusher) {
            showDialog(getString(R.string.connect_fail))
        }

        override fun onConnectionLost(pusher: AlivcLivePusher) {
            showToast("推流已断开")
        }

        override fun onPushURLAuthenticationOverdue(pusher: AlivcLivePusher): String {
            showDialog("流即将过期，请更换url")
            return getAuthString(mAuthTime)!!
        }

        override fun onSendMessage(pusher: AlivcLivePusher) {
            showToast(getString(R.string.send_message))
        }

        override fun onPacketsLost(pusher: AlivcLivePusher) {
            showToast("推流丢包通知")
        }
    }
    private val mPushBGMListener: AlivcLivePushBGMListener = object : AlivcLivePushBGMListener {
        override fun onStarted() {}
        override fun onStoped() {}
        override fun onPaused() {}
        override fun onResumed() {}
        override fun onProgress(progress: Long, duration: Long) {
            activity!!.runOnUiThread {
                if (mMusicDialog != null) {
                    mMusicDialog!!.updateProgress(progress, duration)
                }
            }
        }

        override fun onCompleted() {}
        override fun onDownloadTimeout() {}
        override fun onOpenFailed() {
            showDialog(getString(R.string.bgm_open_failed))
        }
    }

    override fun onDestroy() {
        //stopPcm();
        //stopYUV();
        super.onDestroy()
        if (mExecutorService != null && !mExecutorService!!.isShutdown) {
            mExecutorService!!.shutdown()
        }
    }

    private fun showToast(text: String?) {
        if (activity == null || text == null) {
            return
        }
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if (activity != null) {
                val toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }

    private fun showToastShort(text: String?) {
        if (activity == null || text == null) {
            return
        }
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if (activity != null) {
                val toast = Toast.makeText(activity, text, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }

    private fun showDialog(message: String?) {
        if (activity == null || message == null) {
            return
        }
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if (activity != null) {
                val dialog = AlertDialog.Builder(activity!!)
                dialog.setTitle(getString(R.string.dialog_title))
                dialog.setMessage(message)
                dialog.setNegativeButton(getString(R.string.ok)) { dialogInterface, i -> }
                dialog.show()
            }
        }
    }

    private fun showNetWorkDialog(message: String?) {
        if (activity == null || message == null) {
            return
        }
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            if (activity != null) {
                val dialog = AlertDialog.Builder(activity!!)
                dialog.setTitle(getString(R.string.dialog_title))
                dialog.setMessage(message)
                dialog.setNegativeButton(getString(R.string.ok)) { dialogInterface, i -> }
                dialog.setNeutralButton(getString(R.string.reconnect)) { dialogInterface, i ->
                    try {
                        mAlivcLivePusher!!.reconnectPushAsync(null)
                    } catch (e: IllegalStateException) {
                    }
                }
                dialog.show()
            }
        }
    }

    override fun run() {
        if (mIsPushing != null && mAlivcLivePusher != null) {
            try {
                isPushing = mAlivcLivePusher!!.isNetworkPushing
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            val error = mAlivcLivePusher!!.lastError
            if (error != AlivcLivePushError.ALIVC_COMMON_RETURN_SUCCESS) {
                mIsPushing!!.text = isPushing.toString() + ", error code : " + error.code
            } else {
                mIsPushing!!.text = isPushing.toString()
            }
        }
        mHandler.postDelayed(this, REFRESH_INTERVAL)
    }

    override fun onResume() {
        super.onResume()
//        Application.application?.initAliManger();

        mHandler.post(this)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(this)
    }

    interface BeautyListener {
        fun onBeautySwitch(beauty: Boolean)
    }

    private val mBeautyListener: BeautyListener = object : BeautyListener {
        override fun onBeautySwitch(beauty: Boolean) {
            if (mBeautyButton != null) {
                mBeautyButton!!.isSelected = beauty
            }
        }
    }

    private fun getMD5(string: String): String? {
        val hash: ByteArray
        hash = try {
            MessageDigest.getInstance("MD5").digest(string.toByteArray(charset("UTF-8")))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b and 0xFF.toByte() < 0x10) {
                hex.append("0")
            }
            hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
        }
        return hex.toString()
    }

    private fun getUri(url: String): String {
        var result = ""
        val temp = url.substring(7)
        if (temp != null && !temp.isEmpty()) {
            result = temp.substring(temp.indexOf("/"))
        }
        return result
    }

    //    private void showTimeDialog() {
    //        final EditText et = new EditText(getActivity());
    //        et.setInputType(InputType.TYPE_CLASS_NUMBER);
    //        new AlertDialog.Builder(getContext()).setTitle("输入流鉴权时间")
    //                .setView(et)
    //                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
    //                    public void onClick(DialogInterface dialog, int which) {
    //                        String input = et.getText().toString();
    //                        getAuthString(input);
    //                    }
    //                })
    //                .setNegativeButton("取消", null)
    //                .show();
    //    }
    private fun getAuthString(time: String?): String? {
//        if (!time.isEmpty() && !mPrivacyKey.isEmpty()) {
//            long tempTime = (System.currentTimeMillis() + Integer.valueOf(time)) / 1000;
//            String tempprivacyKey = String.format(mMd5String, getUri(mPushUrl), tempTime, 0, 0, mPrivacyKey);
//            String auth = String.format(mAuthString, tempTime, 0, 0, getMD5(tempprivacyKey));
//            mTempUrl = mPushUrl + auth;
//        } else {
//            mTempUrl = mPushUrl;
//        }
        return mTempUrl
    }

    /*public void startYUV(final Context context) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mMixMain) {
                    //mAlivcLivePusher.setMainStreamPosition(0.5f, 0.5f, 0.5f, 0.5f);
                }
                videoThreadOn = true;
                byte[] yuv;
                int mixvideId = 0;
                InputStream myInput = null;
                try {
                    File f = new File("/sdcard/alivc_resource/me2.yuv");
                    myInput = new FileInputStream(f);
                    mixvideId = mAlivcLivePusher.addMixVideo(AlivcImageFormat.IMAGE_FORMAT_YUVNV12,1080,720,0,0.35f,0.78f,0.3f,0.2f);
                    byte[] buffer = new byte[1080*720*3/2];
                    int length = myInput.read(buffer);
                    //发数据
                    while(length > 0 && videoThreadOn)
                    {
                        mAlivcLivePusher.inputMixVideoData(mixvideId,buffer,1080,720, 1080*720*3/2,System.nanoTime()/1000,0);
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //发数据
                        length = myInput.read(buffer);
                        if(length < 1080*720*3/2)
                        {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                        }
                    }
                    mAlivcLivePusher.removeMixVideo(mixvideId);
                    myInput.close();
                    videoThreadOn = false;
                    mAlivcLivePusher.removeMixVideo(mixvideId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void startYUV2(final Context context) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int mixvideId = 0;
                videoThreadOn2 = true;
                byte[] yuv;
                InputStream myInput = null;
                try {
                    File f = new File("/sdcard/alivc_resource/screenrecord3.yuv");
                    myInput = new FileInputStream(f);
                    mixvideId = mAlivcLivePusher.addMixVideo(AlivcImageFormat.IMAGE_FORMAT_YUVNV12,720,1064,0,0.7f,0.78f,0.2f,0.2f);
                    byte[] buffer = new byte[1064*720*3/2];
                    int length = myInput.read(buffer);
                    //发数据
                    while(length > 0 && videoThreadOn2)
                    {
                        mAlivcLivePusher.inputMixVideoData(mixvideId,buffer,720, 1064, 720*1064*3/2,System.nanoTime()/1000,0);
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //发数据
                        length = myInput.read(buffer);
                        if(length < 720*1064*3/2)
                        {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                        }
                    }
                    mAlivcLivePusher.removeMixVideo(mixvideId);
                    myInput.close();
                    videoThreadOn2 = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void startYUV3(final Context context) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                videoThreadOn3 = true;
                byte[] yuv;
                int mixvideId = 0;
                int framecount = 0;
                InputStream myInput = null;
                try {
                    File f = new File("/sdcard/alivc_resource/he3.yuv");
                    myInput = new FileInputStream(f);
                    mixvideId = mAlivcLivePusher.addMixVideo(AlivcImageFormat.IMAGE_FORMAT_YUVNV12,1080,720,0,0.0f,0.78f,0.3f,0.2f);
                    byte[] buffer = new byte[1080*720*3/2];
                    int length = myInput.read(buffer);
                    //发数据
                    while(length > 0 && videoThreadOn3)
                    {
                        mAlivcLivePusher.inputMixVideoData(mixvideId,buffer,1080, 720, 1080*720*3/2,System.nanoTime()/1000,0);
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        framecount++;
                        if(framecount == 125) {
                            mAlivcLivePusher.mixStreamRequireMain(mixvideId, true);
                        }
                        //发数据
                        length = myInput.read(buffer);
                        if(length < 1080*720*3/2)
                        {
                            myInput.close();
                            myInput = new FileInputStream(f);
                            length = myInput.read(buffer);
                            //mAlivcLivePusher.removeMixVideo(mixvideId);
                            //break;
                        }
                    }
                    mAlivcLivePusher.removeMixVideo(mixvideId);
                    myInput.close();
                    videoThreadOn3 = false;
                    mAlivcLivePusher.removeMixVideo(mixvideId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }*/
    /*private void stopYUV() {
        videoThreadOn = false;
        videoThreadOn2 = false;
        videoThreadOn3 = false;
    }

    private void startMixPCM(final Context context) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioThreadOn = true;
                byte[] pcm;
                int offset = 0;
                int mixAudioId = 0;
                InputStream myInput = null;
                OutputStream myOutput = null;
                boolean reUse = false;
                try {
                    File f = new File("/sdcard/alivc_resource/441.pcm");
                    myInput = new FileInputStream(f);
                    mixAudioId = mAlivcLivePusher.addMixAudio(1, AlivcSoundFormat.SOUND_FORMAT_S16,44100);
                    byte[] buffer = new byte[2048];
                    int length = myInput.read(buffer,0,2048);
                    offset += length;
                    while(length > 0 && audioThreadOn)
                    {
                        reUse = mAlivcLivePusher.inputMixAudioData(mixAudioId,buffer,length, System.nanoTime()/1000);
                        if(reUse) {
                            //发数据
                            length = myInput.read(buffer);
                            offset += length;
                            if (length < 2048) {
                                myInput.close();
                                offset = 0;
                                myInput = new FileInputStream(f);
                                length = myInput.read(buffer);
                                offset += length;
                            }
                        }
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    myInput.close();
                    mAlivcLivePusher.removeMixAudio(mixAudioId);
                    audioThreadOn = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
    private fun startPCM(context: Context?) {
        ScheduledThreadPoolExecutor(1, object : ThreadFactory {
            private val atoInteger = AtomicInteger(0)
            override fun newThread(r: Runnable): Thread {
                val t = Thread(r)
                t.name = "LivePushActivity-readPCM-Thread" + atoInteger.getAndIncrement()
                return t
            }
        }).execute {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            audioThreadOn = true
            var pcm: ByteArray
            var allSended = 0
            val sizePerSecond = 44100 * 2
            var myInput: InputStream? = null
            val myOutput: OutputStream? = null
            val reUse = false
            val startPts = System.nanoTime() / 1000
            try {
                val f = File("/sdcard/alivc_resource/441.pcm")
                myInput = FileInputStream(f)
                val buffer = ByteArray(2048)
                var length = myInput.read(buffer, 0, 2048)
                while (length > 0 && audioThreadOn) {
                    val pts = System.nanoTime() / 1000
                    mAlivcLivePusher!!.inputStreamAudioData(buffer, length, 44100, 1, pts)
                    allSended += length
                    if (allSended * 1000000L / sizePerSecond - 50000 > pts - startPts) {
                        try {
                            Thread.sleep(45)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    length = myInput!!.read(buffer)
                    if (length < 2048) {
                        myInput.close()
                        myInput = FileInputStream(f)
                        length = myInput.read(buffer)
                    }
                    try {
                        Thread.sleep(3)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                myInput!!.close()
                audioThreadOn = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopPcm() {
        audioThreadOn = false
    }

    interface DynamicListern {
        fun onAddDynamic()
        fun onRemoveDynamic()
    }

    fun setButtonVi(state: Int) {
        activity!!.runOnUiThread { mPushButton!!.visibility = state }
    }

    companion object {
        const val TAG = "LivePushFragment"
        private const val URL_KEY = "url_key"
        fun newInstance(url: String?): LivePushFragment {
            val livePushFragment = LivePushFragment()
            val bundle = Bundle()
            bundle.putString(URL_KEY, url)
            livePushFragment.arguments = bundle
            return livePushFragment
        }

        fun saveBitmap(pBitmap: Bitmap, savePath: File?, fileName: String?, format: CompressFormat?) {
            var format = format
            if (format == null) {
                format = CompressFormat.JPEG
            }
            // 保存图片
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(File(savePath, fileName))
                if (fos != null) {
                    pBitmap.compress(format, 100, fos)
                    fos.flush()
                }
            } catch (pE: IOException) {
            } finally {
                try {
                    fos!!.close()
                } catch (e: IOException) {
                }
            }
        }
    }
}