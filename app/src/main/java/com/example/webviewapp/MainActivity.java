package com.example.webviewapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String API_URL = "https://api.example.com/geturl"; // 替换为实际的API地址

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
                            webView.loadUrl(url);
                        } else {
                            Toast.makeText(MainActivity.this, "无法获取URL", Toast.LENGTH_LONG).show();
                            // 加载默认页面
                            webView.loadUrl("http://8.216.128.47/");
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
            loadingText.setText("正在加载中..."); // 自定义加载文字
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
            loadingText.setText("加载失败，请检查网络连接");
            progressBar.setVisibility(View.GONE);
        }
    }

    private String getUrlFromApi() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(API_URL);
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
                return response.toString().trim();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
