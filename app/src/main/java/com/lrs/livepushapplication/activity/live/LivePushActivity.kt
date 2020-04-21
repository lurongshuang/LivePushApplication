package com.lrs.livepushapplication.activity.live

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Camera
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.view.View.OnTouchListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alivc.component.custom.AlivcLivePushCustomDetect
import com.alivc.component.custom.AlivcLivePushCustomFilter
import com.alivc.live.detect.TaoFaceFilter
import com.alivc.live.filter.TaoBeautyFilter
import com.alivc.live.pusher.*
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.activity.live.fragment.LivePushFragment
import com.lrs.livepushapplication.activity.live.fragment.PushDiagramStatsFragment
import com.lrs.livepushapplication.activity.live.fragment.PushTextStatsFragment
import com.lrs.livepushapplication.activity.live.http.NetWorkUtils
import com.lrs.livepushapplication.application.Application
import com.lrs.livepushapplication.application.Application.*
import com.lrs.livepushapplication.application.Application.Companion.mAlivcLivePushConfig
import com.lrs.livepushapplication.application.Application.Companion.mAsyncValue
import com.lrs.livepushapplication.application.Application.Companion.mOrientationEnum
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class LivePushActivity : AppCompatActivity() {
    var previewView: SurfaceView? = null
    private var mViewPager: ViewPager? = null
    private var mFragmentList: MutableList<Fragment?>? = ArrayList()
    private var mFragmentAdapter: FragmentAdapter? = null
    private var mDetector: GestureDetector? = null
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mLivePushFragment: LivePushFragment? = null
    private var mPushTextStatsFragment: PushTextStatsFragment? = null
    private var mPushDiagramStatsFragment: PushDiagramStatsFragment? = null

    var livePusher: AlivcLivePusher? = null
        private set
    private var mPushUrl: String? = null
    private var mAsync = false
    private var mOrientation = AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT.ordinal
    private var mSurfaceStatus = SurfaceStatus.UNINITED
    private var isPause = false
    var alivcLivePushStatsInfo: AlivcLivePushStatsInfo? = null
    var taoBeautyFilter: TaoBeautyFilter? = null
    var taoFaceFilter: TaoFaceFilter? = null
    private var videoThreadOn = false
    private var audioThreadOn = false
    private var mNetWork = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_push)
        mPushUrl = intent.getStringExtra(URL_KEY)
//        livePusher = AlivcLivePusher()
        initpar();
        mLivePushFragment = LivePushFragment.newInstance(mPushUrl)
        mLivePushFragment?.setAlivcLivePusher(livePusher)
        mLivePushFragment?.setStateListener(mStateListener)
        mPushTextStatsFragment = PushTextStatsFragment()
        mPushDiagramStatsFragment = PushDiagramStatsFragment()
        mNetWork = NetWorkUtils.getAPNType(this)
        initViewPager()
    }

    fun initpar() {
        if (livePusher != null) {
            try {
                livePusher!!.destroy()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        livePusher = AlivcLivePusher()
        initView();
        mScaleDetector = ScaleGestureDetector(applicationContext, mScaleGestureDetector)
        mDetector = GestureDetector(applicationContext, mGestureDetector)
        mAsync = mAsyncValue
        mOrientation = mOrientationEnum.orientation
        setOrientation(mOrientation)
        mAlivcLivePushConfig = mAlivcLivePushConfig;
        initConfig();
    }


    private fun initConfig() {
        try {
            livePusher!!.init(applicationContext, mAlivcLivePushConfig)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            showDialog(this, e.message)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            showDialog(this, e.message)
        }
        livePusher!!.setCustomDetect(object : AlivcLivePushCustomDetect {
            override fun customDetectCreate() {
                taoFaceFilter = TaoFaceFilter(applicationContext)
                taoFaceFilter!!.customDetectCreate()
            }

            override fun customDetectProcess(data: Long, width: Int, height: Int, rotation: Int, format: Int, extra: Long): Long {
                return if (taoFaceFilter != null) {
                    taoFaceFilter!!.customDetectProcess(data, width, height, rotation, format, extra)
                } else 0
            }

            override fun customDetectDestroy() {
                if (taoFaceFilter != null) {
                    taoFaceFilter!!.customDetectDestroy()
                }
            }
        })
        livePusher!!.setCustomFilter(object : AlivcLivePushCustomFilter {
            override fun customFilterCreate() {
                taoBeautyFilter = TaoBeautyFilter()
                taoBeautyFilter!!.customFilterCreate()
            }

            override fun customFilterUpdateParam(fSkinSmooth: Float, fWhiten: Float, fWholeFacePink: Float, fThinFaceHorizontal: Float, fCheekPink: Float, fShortenFaceVertical: Float, fBigEye: Float) {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter!!.customFilterUpdateParam(fSkinSmooth, fWhiten, fWholeFacePink, fThinFaceHorizontal, fCheekPink, fShortenFaceVertical, fBigEye)
                }
            }

            override fun customFilterSwitch(on: Boolean) {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter!!.customFilterSwitch(on)
                }
            }

            override fun customFilterProcess(inputTexture: Int, textureWidth: Int, textureHeight: Int, extra: Long): Int {
                return if (taoBeautyFilter != null) {
                    taoBeautyFilter!!.customFilterProcess(inputTexture, textureWidth, textureHeight, extra)
                } else inputTexture
            }

            override fun customFilterDestroy() {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter!!.customFilterDestroy()
                }
                taoBeautyFilter = null
            }
        })
    }

    fun initView() {
        previewView = findViewById<View>(R.id.preview_view) as SurfaceView
        previewView!!.holder.addCallback(mCallback)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViewPager() {
        mViewPager = findViewById(R.id.tv_pager)
        mFragmentList!!.add(mLivePushFragment)
        mFragmentAdapter = FragmentAdapter(this.supportFragmentManager, mFragmentList)
        mViewPager?.adapter = mFragmentAdapter
        mViewPager?.setOnTouchListener(OnTouchListener { _, motionEvent ->
            if (motionEvent.pointerCount >= 2 && mScaleDetector != null) {
                mScaleDetector!!.onTouchEvent(motionEvent)
            } else if (motionEvent.pointerCount == 1 && mDetector != null) {
                mDetector!!.onTouchEvent(motionEvent)
            }
            false
        })
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setOrientation(orientation: Int) {
        if (orientation == AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT.ordinal) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (orientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.ordinal) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (orientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.ordinal) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
    }

    private val mGestureDetector: GestureDetector.OnGestureListener = object : GestureDetector.OnGestureListener {
        override fun onDown(motionEvent: MotionEvent): Boolean {
            return false
        }

        override fun onShowPress(motionEvent: MotionEvent) {}
        override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
            if (previewView!!.width > 0 && previewView!!.height > 0) {
                val x = motionEvent.x / previewView!!.width
                val y = motionEvent.y / previewView!!.height
                try {
                    livePusher!!.focusCameraAtAdjustedPoint(x, y, true)
                } catch (e: IllegalStateException) {
                }
            }
            return true
        }

        override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
            return false
        }

        override fun onLongPress(motionEvent: MotionEvent) {}
        override fun onFling(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
            if (motionEvent == null || motionEvent1 == null) {
                return false
            }
            if (motionEvent.x - motionEvent1.x > FLING_MIN_DISTANCE
                    && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling left
            } else if (motionEvent1.x - motionEvent.x > FLING_MIN_DISTANCE
                    && Math.abs(v) > FLING_MIN_VELOCITY) {
                // Fling right
            }
            return false
        }
    }
    private var scaleFactor = 1.0f
    private val mScaleGestureDetector: ScaleGestureDetector.OnScaleGestureListener = object : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            if (scaleGestureDetector.scaleFactor > 1) {
                scaleFactor += 0.5f
            } else {
                scaleFactor -= 2f
            }
            if (scaleFactor <= 1) {
                scaleFactor = 1f
            }
            try {
                if (scaleFactor >= livePusher!!.maxZoom) {
                    scaleFactor = livePusher!!.maxZoom.toFloat()
                }
                livePusher!!.setZoom(scaleFactor.toInt())
            } catch (e: IllegalStateException) {
            }
            return false
        }

        override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(scaleGestureDetector: ScaleGestureDetector) {}
    }
    var mCallback: SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            if (mSurfaceStatus == SurfaceStatus.UNINITED) {
                mSurfaceStatus = SurfaceStatus.CREATED
                if (livePusher != null) {
                    try {
                        if (mAsync) {
                            livePusher!!.startPreviewAysnc(previewView)
                        } else {
                            livePusher!!.startPreview(previewView)
                        }
                        if (mAlivcLivePushConfig!!.isExternMainStream) {
                            startYUV(applicationContext)
                        }
                    } catch (e: IllegalArgumentException) {
                        e.toString()
                    } catch (e: IllegalStateException) {
                        e.toString()
                    }
                }
            } else if (mSurfaceStatus == SurfaceStatus.DESTROYED) {
                mSurfaceStatus = SurfaceStatus.RECREATED
            }
        }

        override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
            mSurfaceStatus = SurfaceStatus.CHANGED
            if (mLivePushFragment != null) {
                mLivePushFragment!!.setSurfaceView(previewView)
            }
        }

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            mSurfaceStatus = SurfaceStatus.DESTROYED
        }
    }

    override fun onResume() {
        super.onResume()
        if (livePusher != null) {
            try {
                if (!isPause) {
                    if (mAsync) {
                        livePusher!!.resumeAsync()
                    } else {
                        livePusher!!.resume()
                    }
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        //        if(mViewPager.getCurrentItem() != 1) {
//            mHandler.post(mRunnable);
//        }
    }

    override fun onPause() {
        super.onPause()
        if (livePusher != null) {
            try {
                if (livePusher != null) {
                    livePusher!!.pause()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        videoThreadOn = false
        audioThreadOn = false
        if (livePusher != null) {
            try {
                livePusher!!.destroy()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
        mFragmentList = null
        previewView = null
        mViewPager = null
        mFragmentAdapter = null
        mDetector = null
        mScaleDetector = null
        mLivePushFragment = null
        mPushTextStatsFragment = null
        mPushDiagramStatsFragment = null
        mAlivcLivePushConfig = null
        livePusher = null
        alivcLivePushStatsInfo = null
        super.onDestroy()
    }

    inner class FragmentAdapter(fm: FragmentManager?, fragmentList: MutableList<Fragment?>?) : FragmentPagerAdapter(fm!!) {
        var fragmentList: MutableList<Fragment?>? = ArrayList()
        override fun getItem(position: Int): Fragment {
            return fragmentList?.get(position)!!
        }

        override fun getCount(): Int {
            return fragmentList!!.size
        }

        init {
            this.fragmentList = fragmentList
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val rotation = windowManager.defaultDisplay.rotation
        val orientationEnum: AlivcPreviewOrientationEnum
        if (livePusher != null) {
            orientationEnum = when (rotation) {
                Surface.ROTATION_90 -> AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT
                Surface.ROTATION_270 -> AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT
                else -> AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT
            }
            try {
                livePusher!!.setPreviewOrientation(orientationEnum)
            } catch (e: IllegalStateException) {
            }
        }
    }

    private fun showDialog(context: Context, message: String?) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.dialog_title))
        dialog.setMessage(message)
        dialog.setNegativeButton(getString(R.string.ok)) { dialogInterface, i -> finish() }
        dialog.show()
    }

    private val mRunnable = Runnable {
        object : AsyncTask<AlivcLivePushStatsInfo?, Void?, AlivcLivePushStatsInfo?>() {
            override fun doInBackground(vararg params: AlivcLivePushStatsInfo?): AlivcLivePushStatsInfo? {
                try {
                    alivcLivePushStatsInfo = livePusher!!.livePushStatsInfo
                } catch (e: IllegalStateException) {
                }
                return alivcLivePushStatsInfo
            }

            override fun onPostExecute(alivcLivePushStatsInfo: AlivcLivePushStatsInfo?) {
                super.onPostExecute(alivcLivePushStatsInfo)
                if (mPushTextStatsFragment != null && mViewPager!!.currentItem == 0) {
                    mPushTextStatsFragment!!.updateValue(alivcLivePushStatsInfo)
                } else if (mPushDiagramStatsFragment != null && mViewPager!!.currentItem == 2) {
                    mPushDiagramStatsFragment!!.updateValue(alivcLivePushStatsInfo)
                }
                //                    mHandler.postDelayed(mRunnable, REFRESH_INTERVAL);
            }
        }.execute()
    }

    interface PauseState {
        fun updatePause(state: Boolean)
    }

    private val mStateListener: PauseState = object : PauseState {
        override fun updatePause(state: Boolean) {
            isPause = state
        }
    }

    internal inner class ConnectivityChangedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                if (mNetWork != NetWorkUtils.getAPNType(context)) {
                    mNetWork = NetWorkUtils.getAPNType(context)
                    if (livePusher != null) {
                        if (livePusher!!.isPushing) {
                            try {
                                livePusher!!.reconnectPushAsync(null)
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    fun startYUV(context: Context?) {
        ScheduledThreadPoolExecutor(1, object : ThreadFactory {
            private val atoInteger = AtomicInteger(0)
            override fun newThread(r: Runnable): Thread {
                val t = Thread(r)
                t.name = "LivePushActivity-readYUV-Thread" + atoInteger.getAndIncrement()
                return t
            }
        }).execute {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            videoThreadOn = true
            var yuv: ByteArray
            var myInput: InputStream? = null
            try {
                val f = File(Environment.getExternalStorageDirectory().path + File.separator + "alivc_resource/capture0.yuv")
                myInput = FileInputStream(f)
                val buffer = ByteArray(1280 * 720 * 3 / 2)
                var length = myInput.read(buffer)
                //发数据
                while (length > 0 && videoThreadOn) {
                    livePusher!!.inputStreamVideoData(buffer, 720, 1280, 720, 1280 * 720 * 3 / 2, System.nanoTime() / 1000, 0)
                    try {
                        Thread.sleep(40)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    //发数据
                    length = myInput!!.read(buffer)
                    if (length <= 0) {
                        myInput.close()
                        myInput = FileInputStream(f)
                        length = myInput.read(buffer)
                    }
                }
                myInput!!.close()
                videoThreadOn = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopYUV() {
        videoThreadOn = false
    }

    private fun stopPcm() {
        audioThreadOn = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mLivePushFragment!!.exit()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val TAG = "LivePushActivity"
        private const val FLING_MIN_DISTANCE = 50
        private const val FLING_MIN_VELOCITY = 0
        private const val URL_KEY = "url_key"
        private const val REQ_CODE_PUSH = 0x1112
        const val CAPTURE_PERMISSION_REQUEST_CODE = 0x1123

        fun startActivity(activity: Activity, alivcLivePushConfig: AlivcLivePushConfig?, url: String) {
            val intent = Intent(activity, LivePushActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable(AlivcLivePushConfig.CONFIG, alivcLivePushConfig)
            bundle.putString(URL_KEY, url)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, REQ_CODE_PUSH)
        }
    }
}