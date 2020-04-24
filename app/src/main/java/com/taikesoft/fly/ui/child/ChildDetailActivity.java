package com.taikesoft.fly.ui.child;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.webapi.MainApi;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *儿童详情展示(含头像)
 */
public class ChildDetailActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.tv_titlebar_right)
    TextView mTvTitleBarRight;
    @BindView(R.id.slView)
    ScrollView slView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tvChildNum)
    TextView tvChildNum;
    @BindView(R.id.tvYearNum)
    TextView tvYearNum;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvIdNumber)
    TextView tvIdNumber;
    @BindView(R.id.tvSex)
    TextView tvSex;
    @BindView(R.id.tvAge)
    TextView tvAge;
    @BindView(R.id.tvNurseGroup)
    TextView tvNurseGroup;
    @BindView(R.id.tvParentingType)
    TextView tvParentingType;
    @BindView(R.id.tvEnterDate)
    TextView tvEnterDate;
    @BindView(R.id.tvDischargeDate)
    TextView tvDischargeDate;
    @BindView(R.id.tvDisabilityType)
    TextView tvDisabilityType;
    @BindView(R.id.tvDisabilityLevel)
    TextView tvDisabilityLevel;
    @BindView(R.id.tvSeriousDisease)
    TextView tvSeriousDisease;
    @BindView(R.id.tvAids)
    TextView tvAids;
    @BindView(R.id.ivRealPic)
    ImageView ivRealPic;
    private String strRealPic;
    private String childId;
    @Override
    protected void initView() {
        setContentView(R.layout.activity_child_detail);
        setTitle(AppContext.mContext.getString(R.string.child_detail_title));
        //绑定控件
        ButterKnife.bind(this);
        mTvTitleBarRight.setVisibility(View.GONE);
        strRealPic = "";
        childId = getIntent().getStringExtra("childId");
        ivRealPic.setOnClickListener(ChildDetailActivity.this);
        initPersonInfo();
    }
    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPersonInfo();
    }

    private void initPersonInfo() {
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            pData.put("id", childId);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this, AppConfig.CHILD_INFO, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log("childInfo -->" + httpResult);
                try {
                    JSONObject obj = new JSONObject(httpResult);
                    if (StringUtils.equals(obj.getString("state"), SysCode.SUCCESS)) {
                        JSONObject child = obj.getJSONObject("data");

                        if (!child.isNull("childNumber")) {
                            tvChildNum.setText(child.getString("childNumber"));
                        }
                        if (!child.isNull("yearNumber")) {
                            tvYearNum.setText(child.getString("yearNumber"));
                        }
                        if (!child.isNull("name")) {
                            tvName.setText(child.getString("name"));
                        }
                        if (!child.isNull("idNumber")) {
                            tvIdNumber.setText(child.getString("idNumber"));
                        }
                        if (!child.isNull("gender")) {
                            tvSex.setText(child.getString("gender"));
                        }
                        if (!child.isNull("age")) {
                            tvAge.setText(child.getString("age"));
                        }
                        if (!child.isNull("nursingRoom")) {
                            tvNurseGroup.setText(child.getString("nursingRoom"));
                        }
                        if (!child.isNull("enterDate")) {
                            tvEnterDate.setText(child.getString("enterDate"));
                        }
                        if (!child.isNull("dischargeDate")) {
                            tvDischargeDate.setText(child.getString("dischargeDate"));
                        }
                        if (!child.isNull("disabilityType")) {
                            tvDisabilityType.setText(child.getString("disabilityType"));
                        }
                        if (!child.isNull("disabilityLevel")) {
                            tvDisabilityLevel.setText(child.getString("disabilityLevel"));
                        }
                        if (!child.isNull("seriousDisease")) {
                            tvSeriousDisease.setText(child.getString("seriousDisease"));
                        }
                        if (!child.isNull("aids")) {
                            tvAids.setText(child.getString("aids"));
                        }
                        if (!child.isNull("parentingType")) {
                            tvParentingType.setText(child.getString("parentingType"));
                        }
                        if (!child.isNull("facePhoto")) {
                            strRealPic = child.getString("facePhoto");
                            //儿童头像的相对地址 如/upload/img_1.png
                            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + child.getString("facePhoto"), ivRealPic);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }

    @Override
    public void setView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ivRealPic:
                if(strRealPic.length() > 0){
                    Intent intent = new Intent(ChildDetailActivity.this, ShowImageActivity.class);
                    intent.putExtra("photoPath", strRealPic);
                    startActivity(intent);
                }else{
                    AppContext.showToast(R.string.child_no_img);
                }
                break;
        }
    }
}
