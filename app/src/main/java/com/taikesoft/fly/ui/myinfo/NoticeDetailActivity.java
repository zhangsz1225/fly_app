package com.taikesoft.fly.ui.myinfo;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.ui.child.ShowImageActivity;
import com.taikesoft.fly.ui.myinfo.bean.NoticeBean;

/**
 * 通知公告详情
 */
public class NoticeDetailActivity extends BaseActivity {
    private TextView tvTitle, tvPubDate, tvPubOrg, tvVisitCount;
    private LinearLayout ll_webview;
    private NoticeBean mNoticeBean;

    @Override
    protected void initView() {
        mNoticeBean = (NoticeBean) getIntent().getSerializableExtra("mNoticeBean");
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(AppContext.mResource.getString(R.string.notice_detail_title));
        tvTitle = findViewById(R.id.tvTitle);
        tvPubDate = findViewById(R.id.tvPubDate);
        tvPubOrg = findViewById(R.id.tvPubOrg);
        tvVisitCount = findViewById(R.id.tvVisitCount);
        tvPubOrg.setText("发布单位：" + mNoticeBean.getPubOrg());
        tvVisitCount.setText("访问次数：" + mNoticeBean.getVisitCount());
        tvTitle.setText(mNoticeBean.getTitle());
        tvPubDate.setText(FromatDate(mNoticeBean.getPubDate()));
        ll_webview = findViewById(R.id.ll_webview);
        ll_webview.removeAllViews();
        WebView webView = new WebView(this);
        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        webView.loadDataWithBaseURL("", mNoticeBean.getContent(), "text/html", "utf-8", null);
        //解决图片太大左右滑，宽满屏
        String head = "<style>\nimg{\n" +
                " max-width:100%;\n" +
                " height:auto;\n" +
                "}</style>";

        //手动设置网页图片的点击事件
        String javascript = "function findImg(){" +
                "var objs = document.getElementsByTagName('img');" +
                "for(var i=0;i<objs.length;i++){" +
                "objs[i].onclick=function(){" +
                "window.android.showImg(this.src)" +
                "}" +
                "}}";
        //拼接成一个完成的 HTML，
        String html = "<html><head>" +
                "<script> " + javascript + " </script></head>"
                + "<body>" + head + mNoticeBean.getContent() + "</body></html>";
        initWebView(webView);
        webView.loadDataWithBaseURL("", html, "text/html", "utf-8", null);
        ll_webview.addView(webView);
    }

    /**
     * 设置WebView自适应屏幕
     */
    public void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setLoadWithOverviewMode(true);//设置WebView是否使用预览模式加载界面。
        webView.setVerticalScrollBarEnabled(false);//不能垂直滑动
        webView.setHorizontalScrollBarEnabled(false);//不能水平滑动
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        //设置WebView属性，能够执行Javascript脚本
        webView.getSettings().setJavaScriptEnabled(true);//设置js可用
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new AndroidJavaScript(getApplication()), "android");//设置js接口
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String call = "javascript:findImg()";
                view.loadUrl(call);
                super.onPageFinished(view, url);
            }
        });
    }

    /**
     * AndroidJavaScript
     * 本地与h5页面交互的js类，这里写成内部类了
     * returnAndroid方法上@JavascriptInterface一定不能漏了
     */
    private class AndroidJavaScript {
        Context mContxt;

        public AndroidJavaScript(Context mContxt) {
            this.mContxt = mContxt;
        }

        @JavascriptInterface
        public void showImg(String img) {
            Intent intent = new Intent();
            intent.putExtra("entry", "notice");
            intent.putExtra("photoPath", img);
            intent.setClass(NoticeDetailActivity.this, ShowImageActivity.class);
            startActivity(intent);
        }
    }

    private String FromatDate(String time) {
        return time.substring(0, 4) + "年" + time.substring(5, 7) + "月" + time.substring(8, 10) + "日";
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_notice_detail);
    }

    @Override
    public void setListener() {

    }
}
