package com.example.sogou.webviewexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import static com.example.sogou.webviewexample.R.id.webView;


public class MainActivity extends Activity {
    private WebView webview;
    public static final String MINGXING_URL = "http://ld.sogou.com/m/cate?cid=102&fromsearch=1&ch=and.launchericon.mingxing.ld";
    public static final String DONGMAN_URL = "http://ld.sogou.com/m/cate?cid=105&fromsearch=1&ch=and.launchericon.dongman.ld";
    public static final String YINGSHI_URL = "http://ld.sogou.com/m/cate?cid=108&fromsearch=1&ch=and.launchericon.yingshi.ld";
    public static final String QINGGAN_URL = "http://ld.sogou.com/m/cate?cid=121&fromsearch=1&ch=and.launchericon.qinggan.ld";
    public static final String YOUXI_URL = "http://ld.sogou.com/m/cate?cid=101&fromsearch=1&ch=and.launchericon.youxi.ld";
    public static final String XIAOSHUO_URL = "http://ld.sogou.com/m/cate?cid=120&fromsearch=1&ch=and.launchericon.xiaoshuo.ld";
    public static final String TIYU_URL = "http://ld.sogou.com/m/cate?cid=148&fromsearch=1&ch=and.launchericon.tiyu.ld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        webview = (WebView) findViewById(webView);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        }

//        SharedPreferences sp = getSharedPreferences("cookies", Context.MODE_PRIVATE); //私有数据
//        String cookies = sp.getString(MINGXING_URL, null);
//        if(cookies != null) {
//            syncCookie(MINGXING_URL, cookies);
//        }

        final WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUserAgentString(webSettings.getUserAgentString() + "luedongapk");
//        Log.e("userAgent ", "ua = " + webSettings.getUserAgentString());
        webSettings.setDatabaseEnabled(true);

        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.e("xxxxx", "url = " + url);
//                if(url.startsWith("http://www.wenwen.sogou.com/login/popLogin?")) {
//                    Log.e("xxxxx", "url 2 = " + url);
//                    view.loadUrl(DONGMAN_URL);
//                    return true;
//                }

                if(url.startsWith("http:") || url.startsWith("https:")) {
//                    view.loadUrl(url);
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });

        new WebViewTask().execute();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private class WebViewTask extends AsyncTask<Void, Void, Boolean> {
        String cookies;
        CookieManager cookieManager;

        @Override
        protected void onPreExecute() {
            CookieSyncManager.createInstance(MainActivity.this);
            cookieManager = CookieManager.getInstance();
            SharedPreferences sp = getSharedPreferences("cookies", Context.MODE_PRIVATE);
            cookies = sp.getString(MINGXING_URL, null);
//            Log.e("onPreExecute", "shared cookies = " + cookies);
            cookieManager.setAcceptCookie(true);
            if (cookies != null) {
                cookieManager.removeAllCookie();
            }

            super.onPreExecute();
        }

        protected Boolean doInBackground(Void... param) {
            /* this is very important - THIS IS THE HACK */
            SystemClock.sleep(500);
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
//            Log.e("onPostExecute", "old cookies = " + cookieManager.getCookie(TIYU_URL));
            if(cookies != null) {
                String[] cookie = cookies.split(";");
                for (int i = 0; i < cookie.length; ++i) {
                    cookieManager.setCookie(MINGXING_URL, cookie[i] + ";Domain=.sogou.com; Path=/");  //设置cookie
                }
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync();
            } else {
                cookieManager.flush();
            }
//            Log.e("onPostExecute", "new cookies = " + cookieManager.getCookie(TIYU_URL));

            webview.loadUrl(MINGXING_URL);
        }
    }

    // 同步cookie
//    public void syncCookie(String url, String cookies) {
//        try {
//            Log.e("syncCookie", "share cookies = " + cookies);
//            CookieManager cookieManager = CookieManager.getInstance();
//            cookieManager.removeSessionCookie();
//            cookieManager.setCookie(url, cookies);
//
//            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
//                CookieSyncManager.createInstance(this);
//                CookieSyncManager.getInstance().sync();
//            } else {
//                cookieManager.flush();
//                Log.e("syncCookie", "cookies flush....");
//            }
//            Log.e("syncCookie", "after cookies = " + cookieManager.getCookie(url));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == 1 && resultCode == RESULT_OK) {
//            Log.e("onActivityResult", "data = " + data.toString());
//        }
//    }


    @Override
    protected void onStop() {
        SharedPreferences sp = getSharedPreferences("cookies", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(MINGXING_URL);
//        Log.e("onStop", "cookies = " + cookies);
        editor.putString(MINGXING_URL, cookies);
        editor.commit();
        super.onStop();
    }
}
