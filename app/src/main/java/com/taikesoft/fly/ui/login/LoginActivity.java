package com.taikesoft.fly.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.BaseData;
import com.taikesoft.fly.business.common.utils.Base64Util;
import com.taikesoft.fly.business.common.utils.MD5;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TDevice;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.ui.homepage.MainActivity;
import com.taikesoft.fly.ui.login.bean.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity implements View.OnLayoutChangeListener {

    private static final String TAG = "login";
    @BindView(R.id.autocompletetv_username)
    AutoCompleteTextView etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;
    @BindView(R.id.email_login_form)
    LinearLayout llLoginForm;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.iv_clear_username)
    ImageView ivClearUsername;
    @BindView(R.id.iv_clear_password)
    ImageView ivClearPassword;
    @BindView(R.id.rl_login)
    RelativeLayout rlLogin;
    @BindView(R.id.ll_error_notification)
    LinearLayout llErrorNotification;
    @BindView(R.id.tv_error)
    TextView mTvError;
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.view_username)
    View mViewUsername;
    @BindView(R.id.view_password)
    View mViewPwd;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.ll_content_bottom)
    LinearLayout llBottom;
    int screenHeight, keyBordHeight;
    View mChildOfContent;
    private boolean cleanUser = false;

    @Override
    public void setView() {
        PushManager.startWork(AppContext.mContext, PushConstants.LOGIN_TYPE_API_KEY, AppConfig.API_KEY);
        setContentView(R.layout.activity_login);
        //绑定控件
        ButterKnife.bind(this);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void setBack() {
        super.setBack();
    }

    @Override
    public void initView() {
        cleanUser = getIntent().getBooleanExtra("pwdClean", false);
        if (cleanUser) {
            etUsername.setText("");
            etPassword.setText("");
        } else {
            etUsername.setText(SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            etPassword.setText(SharedPreferencesManager.getString(ResultStatus.PWD));
        }
        //获取屏幕高度
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;
        //阀值设置为屏幕高度的1/3
        keyBordHeight = screenHeight / 3;
        etPassword.setOnKeyListener(onKey);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.rl_login);
        //contentlayout是最外层布局
        mChildOfContent = rootView.getChildAt(0);
        mChildOfContent.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        possiblyResizeChildOfContent();
                    }
                });
    }

    int usableHeightPrevious;

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 键盘弹出
                llBottom.setVisibility(View.GONE);
            } else {
                // 键盘收起
                llBottom.setVisibility(View.VISIBLE);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }

    @Override
    public void setListener() {
        if (btLogin == null) return;
        btLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        tvTel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + tvTel.getText().toString()));
                startActivity(phoneIntent);
            }
        });
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initIvClearName(s.toString());
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                initIvClearPass(s.toString());
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    mViewUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.common_blue_color));
                    initIvClearName(etUsername.getText().toString());
                } else {//失去焦点
                    mViewUsername.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_gray_divider));
                }
            }
        });
        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {//获得焦点
                    mViewPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.common_blue_color));
                    initIvClearPass(etPassword.getText().toString());
                } else {//失去焦点
                    mViewPwd.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_gray_divider));
                }
            }
        });

        ivClearUsername.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setText("");
                ivClearUsername.setVisibility(View.GONE);
            }
        });
        ivClearPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setText("");
                ivClearPassword.setVisibility(View.GONE);
            }
        });
        etUsername.requestFocus();
    }

    private void initIvClearName(String s) {
        if (!StringUtils.isEmpty(s.toString())) {
            ivClearUsername.setVisibility(View.VISIBLE);
            if (llErrorNotification.getVisibility() == View.VISIBLE) {
                llErrorNotification.setVisibility(View.INVISIBLE);
            }
        } else {
            ivClearUsername.setVisibility(View.GONE);
        }
    }

    private void initIvClearPass(String s) {
        if (!StringUtils.isEmpty(s)) {
            ivClearPassword.setVisibility(View.VISIBLE);
            if (llErrorNotification.getVisibility() == View.VISIBLE) {
                llErrorNotification.setVisibility(View.INVISIBLE);
            }
        } else {
            ivClearPassword.setVisibility(View.GONE);
        }
    }


    private void attemptLogin() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        //检测用户名是否为空
        if (TextUtils.isEmpty(username)) {
            AppContext.showToast(R.string.error_please_input_username);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            AppContext.showToast(R.string.error_please_input_password);
            return;
        }
        MD5 md5 = new MD5();
        String passmd5 = md5.getMD5(password);
        Log.i("login", "MD5是：" + passmd5);
        String pwEncrypt = Base64Util.getBase64(passmd5);
        Log.i("login", "最终密码是：" + pwEncrypt);
        initLoading("");
        //retrofit实现
        JSONObject obj = new JSONObject();
        try {
            JSONObject obj0 = new JSONObject();
            obj.put("password", password);
            obj.put("username", username);
            obj.put("channelId", SharedPreferencesManager.getString(ResultStatus.CHANNEL_ID));
            obj0.put("params", obj);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), obj0.toString());
            AppContext.getInstance().initRetrofit();
            Call<ResponseBody> data = AppContext.getInstance().getApiService().getMessage(body);
            data.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String httpResult = response.body().string();
                        BaseData<LoginInfo> baseData = new Gson().fromJson(httpResult,
                                new TypeToken<BaseData<LoginInfo>>() {
                                }.getType());

                        if (StringUtils.equals(baseData.getState(), ResultStatus.SUCCESS)) {
                            if (baseData.getData() != null) {
                                if (!StringUtils.isEmpty(baseData.getData().getId())) {
                                    SharedPreferencesManager.putString(ResultStatus.USER_ID, baseData.getData().getId());
                                }
                                if (!StringUtils.isEmpty(baseData.getData().getEmployeeId())) {
                                    SharedPreferencesManager.putString(ResultStatus.EMPLOYEE_ID, baseData.getData().getEmployeeId());
                                }
                                if (!StringUtils.isEmpty(baseData.getData().getRealName())) {
                                    SharedPreferencesManager.putString(ResultStatus.REAL_NAME, baseData.getData().getRealName());
                                }
                                if (!StringUtils.isEmpty(baseData.getData().getToken())) {
                                    SharedPreferencesManager.putString(ResultStatus.TOKEN, baseData.getData().getToken());
                                }
                                if (!StringUtils.isEmpty(baseData.getData().getPhone())) {
                                    SharedPreferencesManager.putString(ResultStatus.PHONE, baseData.getData().getPhone());
                                }
                                if (!StringUtils.isEmpty(baseData.getData().getDept())) {
                                    SharedPreferencesManager.putString(ResultStatus.DEPT, baseData.getData().getDept());
                                }
                                SharedPreferencesManager.putString(ResultStatus.LOGIN_STATUS, ResultStatus.LOGINED);
                                SharedPreferencesManager.putString(ResultStatus.PWD, etPassword.getText().toString());
                                SharedPreferencesManager.putString(ResultStatus.USER_NAME, etUsername.getText().toString());
                                AppContext.getInstance().initRetrofit();
                                AppContext.getInstance().reinitWebApi();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                return;
                            }
                        } else if (StringUtils.equals(baseData.getState(), ResultStatus.FAIL)) {
                            if (StringUtils.equals(baseData.getError(), ResultStatus.PW_ERROR)) {
                                AppContext.showToast(R.string.login_pw_error);
                                llErrorNotification.setVisibility(View.VISIBLE);
                                hideLoading();
                                return;
                            } else if (StringUtils.equals(baseData.getError(), ResultStatus.USER_ERROR)) {
                                AppContext.showToast(R.string.login_user_error);
                                llErrorNotification.setVisibility(View.VISIBLE);
                                mTvError.setText(R.string.error_user_please_input_again);
                                hideLoading();
                                return;
                            }
                        } else {
                            AppContext.showToast(baseData.getMessage());
                            hideLoading();
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    AppContext.showToast(R.string.login_failed);
                    hideLoading();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    AppContext.showToast(R.string.login_failed);
                    hideLoading();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyBordHeight)) {
            llTop.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    240
            ));
            llTop.setGravity(Gravity.CENTER);

        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyBordHeight)) {
            llTop.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    500
            ));
            llTop.setGravity(Gravity.CENTER);
        }
    }

    /**
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                //若不可视区域高度大于100，则键盘显示
                if (rootInvisibleHeight > 100) {
                    int[] location = new int[2];
                    //获取scrollToView在窗体的坐标
                    scrollToView.getLocationInWindow(location);
                    //计算root滚动高度，使scrollToView在可见区域
                    int srollHeight = (location[1] + scrollToView.getHeight() + (int) (10 * TDevice.getDensity(LoginActivity.this))) - rect.bottom;
                    if (srollHeight != 0) {
                        root.scrollTo(0, srollHeight);
                    }

                } else {
                    //键盘隐藏
                    root.scrollTo(0, 0);
                }
            }
        });
    }

    View.OnKeyListener onKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    attemptLogin();
                }
                return true;
            }
            return false;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        rlLogin.addOnLayoutChangeListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

