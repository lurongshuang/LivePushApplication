package com.lrs.livepushapplication.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lrs.livepushapplication.R;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;

public class ProgressWebView extends WebView {
    ProgressView mProgressview;
    int progressColor = getResources().getColor(R.color.colorPrimary);

    public ProgressWebView(Context context) {
        this(context, null);
        initViews();
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initViews();
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    private void initViews() {
        mProgressview = new ProgressView(getContext());
        mProgressview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
        mProgressview.setDefaultColor(progressColor);
        addView(mProgressview);
        WebSettings webSetting = getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setDatabaseEnabled(true);
        webSetting.setCacheMode(android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK);//存在就用缓存，不存在就通过网络获取
        webSetting.setLoadWithOverviewMode(true);
        setDownloadListener(new MyDownloadStart(getContext()));
        webSetting.setDisplayZoomControls(false);
        setInitialScale(45);
        setProgressColor(R.color.colorPrimary);
        this.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (i == 100) {
                    getSettings().setBlockNetworkImage(false);
                }
                if (progressIt != null) {
                    progressIt.onLoad(webView, i);
                }
            }
        });
    }

    public onWebViewLoadProgress progressIt;

    public void setOnWebViewLoadProgress(onWebViewLoadProgress progress) {
        progressIt = progress;
    }

    public interface onWebViewLoadProgress {
        void onLoad(WebView webView, int progress);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void setWebChromeClient(WebChromeClient webChromeClient) {
        super.setWebChromeClient(new ProgressWebChromeClient(webChromeClient));
    }

    public class ProgressWebChromeClient extends WebChromeClient {
        WebChromeClient readClient;

        public ProgressWebChromeClient(WebChromeClient readClient) {
            this.readClient = readClient == null ? new WebChromeClient() : readClient;
        }

        @Override
        public Bitmap getDefaultVideoPoster() {
            return readClient.getDefaultVideoPoster();
        }

        @Override
        public View getVideoLoadingProgressView() {
            return readClient.getVideoLoadingProgressView();
        }

        @Override
        public void getVisitedHistory(ValueCallback<String[]> valueCallback) {
            readClient.getVisitedHistory(valueCallback);
        }

        @Override
        public void onCloseWindow(WebView webView) {
            readClient.onCloseWindow(webView);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return readClient.onConsoleMessage(consoleMessage);
        }

        @Override
        public boolean onCreateWindow(WebView webView, boolean b, boolean b1, Message message) {
            return readClient.onCreateWindow(webView, b, b1, message);
        }

        @Override
        public void onExceededDatabaseQuota(String s, String s1, long l, long l1, long l2, WebStorage.QuotaUpdater quotaUpdater) {
            readClient.onExceededDatabaseQuota(s, s1, l, l1, l2, quotaUpdater);
        }

        @Override
        public void onGeolocationPermissionsHidePrompt() {
            readClient.onGeolocationPermissionsHidePrompt();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
            super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
            readClient.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
        }

        @Override
        public void onHideCustomView() {
            readClient.onHideCustomView();
        }

        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            return readClient.onJsAlert(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsBeforeUnload(WebView webView, String s, String s1, JsResult jsResult) {
            return readClient.onJsBeforeUnload(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            return readClient.onJsConfirm(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
            return readClient.onJsPrompt(webView, s, s1, s2, jsPromptResult);
        }

        @Override
        public boolean onJsTimeout() {
            return readClient.onJsTimeout();
        }

        @Override
        public void onReachedMaxAppCacheSize(long l, long l1, WebStorage.QuotaUpdater quotaUpdater) {
            readClient.onReachedMaxAppCacheSize(l, l1, quotaUpdater);
        }

        @Override
        public void onReceivedIcon(WebView webView, Bitmap bitmap) {
            readClient.onReceivedIcon(webView, bitmap);
        }

        @Override
        public void onReceivedTitle(WebView webView, String s) {
            readClient.onReceivedTitle(webView, s);
        }

        @Override
        public void onReceivedTouchIconUrl(WebView webView, String s, boolean b) {
            readClient.onReceivedTouchIconUrl(webView, s, b);
        }

        @Override
        public void onRequestFocus(WebView webView) {
            readClient.onRequestFocus(webView);
        }


        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
            readClient.onShowCustomView(view, callback);
        }


        @Override
        public void onShowCustomView(View view, int requestedOrientation, IX5WebChromeClient.CustomViewCallback callback) {
            readClient.onShowCustomView(view, requestedOrientation, callback);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            return readClient.onShowFileChooser(webView, valueCallback, fileChooserParams);
        }

        @Override
        public void onProgressChanged(WebView webView, int newProgress) {
            readClient.onProgressChanged(webView, newProgress);
            mProgressview.setProgress(newProgress);
        }
    }

    public Bitmap bitmap = null;


    public Bitmap createBitmap() {
        Picture snapShot = capturePicture();
        int height = snapShot.getHeight();
        int width = snapShot.getWidth();
        if (height <= 0 || width <= 0) {
            return null;
        }
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        snapShot.draw(canvas);
        return bitmap;
    }


    public class MyDownloadStart implements DownloadListener {
        Context context;

        public MyDownloadStart(Context context) {
            this.context = context;
        }

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            //调用系统浏览器下载
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
}

