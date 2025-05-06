package com.example.webviewapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.webkit.WebSettings;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar; // 添加进度条
    private TextView loadingText; // 添加加载文字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 隐藏ActionBar以实现全屏效果
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // 设置全屏显示
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        
        setContentView(R.layout.activity_main);

        // 初始化控件
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar); // 确保在布局文件中添加了这个控件
        loadingText = findViewById(R.id.loading_text); // 确保在布局文件中添加了这个控件
        
        // 配置WebView
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true); // 启用DOM存储API
        webView.getSettings().setDatabaseEnabled(true); // 启用数据库存储API
        webView.getSettings().setAppCacheEnabled(true); // 启用应用缓存API
        webView.getSettings().setLoadsImagesAutomatically(true); // 自动加载图片
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // 允许混合内容
        webView.getSettings().setUseWideViewPort(true); // 使用宽视图
        webView.getSettings().setLoadWithOverviewMode(true); // 加载网页时以概览模式加载
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // 允许JS打开窗口
        webView.getSettings().setSupportMultipleWindows(false); // 不支持多窗口
        webView.getSettings().setAllowFileAccess(true); // 允许访问文件
        // 设置自定义WebViewClient
        webView.setWebViewClient(new CustomWebViewClient());
        
        // 设置WebView填充整个屏幕
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        
        // 在后台线程中获取URL
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String url = getUrlFromApi();
                // 在UI线程中更新WebView
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (url != null && !url.isEmpty()) {
                            webView.loadUrl(url + "?clientType=android");
                        } else {
                            Toast.makeText(MainActivity.this, "Loading failed", Toast.LENGTH_LONG).show();
                            // 加载默认页面
                            webView.loadUrl("https://sss.net?clientType=android");
                        }
                    }
                });
            }
        }).start();
    }

    // 自定义WebViewClient类
    private class CustomWebViewClient extends WebViewClient {
        
        // 页面开始加载时调用
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            // 显示加载进度条和自定义文字
            progressBar.setVisibility(View.VISIBLE);
            loadingText.setVisibility(View.VISIBLE);
            loadingText.setText("Loading..."); // 自定义加载文字
        }
        
        // 页面加载完成时调用
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 隐藏加载进度条和文字
            progressBar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        }
        
        // 加载出错时调用
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            // 显示错误信息
            loadingText.setText("Loading failed, please check the network connection");
            progressBar.setVisibility(View.GONE);
        }
    }

    // 定义多个API地址
    private static final String[] API_URLS = {
        "ss",  // 主要API地址
        "https://backup-api.example.com/geturl",  // 备用API地址1
        "https://fallback-api.example.com/geturl"  // 备用API地址2
    };

    private String getUrlFromApi() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        
        // 依次尝试每个API地址
        for (String apiUrl : API_URLS) {
            try {
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // 检查响应码
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String result = response.toString().trim();
                    if (result != null && !result.isEmpty()) {
                        return result;  // 成功获取URL，立即返回
                    }
                }
            } catch (IOException e) {
                // 记录错误但继续尝试下一个API
                e.printStackTrace();
            } finally {
                // 关闭当前连接和读取器
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    reader = null;  // 重置reader以便下一次循环使用
                }
            }
            
            // 记录尝试下一个API的日志
            android.util.Log.d("WebViewApp", "尝试下一个API地址: " + apiUrl + " 失败，切换到下一个");
        }
        
        // 所有API都失败了，返回null
        android.util.Log.e("WebViewApp", "所有API请求都失败了");
        return null;
    }
}
