package com.taikesoft.fly.ui.myinfo;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.Base;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ModifyActivity extends BaseActivity {
    private RecordBean mRecordBean;
    private WebView mWebview;
    private List<DictionaryBean> clothesTypes = new ArrayList<>();
    private List<DictionaryBean> patrolTypes = new ArrayList<>();
    private List<DictionaryBean> snackTypes = new ArrayList<>();
    private ArrayList<DictionaryBean> mTypes = new ArrayList<>();

    @Override
    public void setView() {
        setContentView(R.layout.activity_nurse_modify);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    public void initView() {
        super.initView();
        setTitle(AppContext.mResource.getString(R.string.nurse_record_detail_title));
        mWebview = findViewById(R.id.webview);
        mWebview.addJavascriptInterface(this, "android");
        //localStorage  允许存储
        mWebview.getSettings().setDomStorageEnabled(true);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebview.getSettings().setAppCachePath(appCachePath);
        mWebview.getSettings().setAllowFileAccess(true);
        mWebview.getSettings().setAppCacheEnabled(true);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mRecordBean = (RecordBean) getIntent().getSerializableExtra("mRecordBean");
        setWebViewClient();
    }

    @Override
    public void setListener() {

    }

    @JavascriptInterface
    public void finishActivity() {
        finish();
    }

    @JavascriptInterface
    public void showToast(String msg) {
        AppContext.showToast(msg);
    }

    @JavascriptInterface
    public List<DictionaryBean> getClothesTypes() {
        return clothesTypes;
    }
    @JavascriptInterface
    public void submitNurseRecord(String url, HttpEntity entity) {
        MainApi.requestCommon(this, url, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    String httpResult = new String(bytes);
                    TLog.log(url + "-->" + httpResult);
                    Base base = new Gson().fromJson(httpResult, Base.class);
                    if (StringUtils.equals(base.getState(), ResultStatus.SUCCESS)) {
                        AppContext.showToast(R.string.submit_success);
                        finish();
                    } else {
                        AppContext.showToast(base.getMessage());
                        finish();
                    }
                    return;
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
                AppContext.showToast(R.string.submit_fail);
                finish();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.submit_fail);
            }
        });
    }
    private void setWebViewClient() {
        mWebview.setWebViewClient(new WebViewClient());
        mWebview.setWebChromeClient(new WebChromeClient());
        mWebview.loadUrl("http://192.168.0.197:8080/");
    }

    @Override
    protected void initWebData() {
        requestTypes(AppConfig.LIST_TYPES);
    }

    private void requestTypes(String url) {
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            JSONObject obj0 = new JSONObject();
            obj0.put("params", obj);
            entity = new StringEntity(obj0.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, url, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    String httpResult = new String(bytes);
                    TLog.log(url + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<DictionaryBean>>() {
                    }.getType();
                    BaseListData<DictionaryBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        mTypes = datas.getData();
                        for (DictionaryBean bean : mTypes) {
                            if (StringUtils.equals("ReplaceProject", bean.getParentId())) {
                                clothesTypes.add(bean);
                            } else if (StringUtils.equals("PatorlStatus", bean.getParentId())) {
                                patrolTypes.add(bean);
                            } else {
                                snackTypes.add(bean);
                            }
                        }
                        return;
                    } else {
                        AppContext.showToast(datas.getMessage());
                        return;
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                AppContext.showToast(R.string.please_check_network);
            }
        });
    }
}
