package com.hyrc99.a.watercreditplatform.activity.webview

import android.widget.RelativeLayout
import com.lrs.livepushapplication.R
import com.lrs.livepushapplication.base.BaseActivity
import com.lrs.livepushapplication.view.ProgressWebView
import kotlinx.android.synthetic.main.activity_webview.*

class WebviewActivity : BaseActivity() {
    private var url: String? = null;
    private var key: String? = null;
    private var title: String? = null;
    private var prWebview: ProgressWebView? = null;
    override fun loadView(): Int {
        return R.layout.activity_webview;
    }

    override fun onDestroy() {
        llweb.removeAllViews();
        prWebview?.removeAllViews();
        prWebview?.clearHistory();
        prWebview?.destroy();
        prWebview = null;
        super.onDestroy()
    }

    fun initView() {
        prWebview = ProgressWebView(this)
        prWebview?.id = R.id.prWebview
        prWebview?.clearCache(false)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        prWebview?.layoutParams = params
        prWebview?.settings?.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE;
        llweb.addView(prWebview)
        if (!key.isNullOrBlank()) {
//            prWebview?.loadUrl(IURL.STANDARDROOT + "Home/ShowPdfs?KEY=" + key)
        } else {
            prWebview?.loadUrl(url)
        }
    }

    override fun initData() {
        url = intent.extras?.getString("loadUrl");
        key = intent.extras?.getString("loadKey");
        title = intent.extras?.getString("title");
        setTitle(true, title)
        initView();
    }

    override fun clearData() {
    }
}
