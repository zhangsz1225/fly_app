package com.taikesoft.fly.ui.myinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.view.CircleImageView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.ui.login.LoginActivity;
import com.taikesoft.fly.ui.myinfo.bean.NoticeBean;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MineFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "MineFragment";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.tv_person_header)
    TextView tvPersonHeader;
    @BindView(R.id.iv_portrait)
    CircleImageView ivPortrait;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    @BindView(R.id.qmui_item_record)
    QMUICommonListItemView mRecord;
    @BindView(R.id.qmui_item_msg)
    QMUICommonListItemView mMsg;
    @BindView(R.id.qmui_item_notice)
    QMUICommonListItemView mNotice;
    @BindView(R.id.qmui_item_logout)
    QMUICommonListItemView mLogout;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();
    private NoticeBean notice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        tvTitle.setText(R.string.title_mine);
        if (!StringUtils.isEmpty(SharedPreferencesManager.getString(ResultStatus.USER_IMG))) {
            ivPortrait.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + SharedPreferencesManager.getString(ResultStatus.USER_IMG), ivPortrait, mOption);
            tvPersonHeader.setVisibility(View.GONE);
        } else {
            ivPortrait.setVisibility(View.GONE);
            tvPersonHeader.setVisibility(View.VISIBLE);
            GradientDrawable myGrad = (GradientDrawable) tvPersonHeader.getBackground();
            myGrad.setColor(Color.parseColor("#38AFF7"));
            String realName = SharedPreferencesManager.getString(ResultStatus.REAL_NAME);
            if (!StringUtils.isEmpty(realName)) {
                tvPersonHeader.setText(realName);
            } else {
                tvPersonHeader.setText(SharedPreferencesManager.getString(ResultStatus.USER_NAME));
            }
        }
        tvUserName.setText(SharedPreferencesManager.getString(ResultStatus.USER_NAME));
        rl_back.setVisibility(View.GONE);
        mMsg.setText("我的消息");
        mRecord.setText("护理记录");
        mNotice.setText("通知公告");
        mLogout.setText("退出登录");
        mMsg.setOnClickListener(this);
        mRecord.setOnClickListener(this);
        mNotice.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        return view;
    }

    public MineFragment() {
    }

    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.qmui_item_record:
                Intent intent = new Intent(getContext(), NurseRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.qmui_item_msg:
                Intent intentMsg = new Intent(getContext(), WarnmsgHistoryActivity.class);
                startActivity(intentMsg);
                break;
            case R.id.qmui_item_notice:
                Intent intentNotice = new Intent(getContext(), NoticeHistoryActivity.class);
                startActivity(intentNotice);
                break;
            case R.id.qmui_item_logout:
                final QMUIDialog.CheckBoxMessageDialogBuilder builder = new QMUIDialog.CheckBoxMessageDialogBuilder(getActivity());
                builder.setTitle("退出后是否删除账号信息?")
                        .setMessage("删除账号信息")
                        .setChecked(true)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("退出", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.putExtra("pwdClean", builder.isChecked());
                                startActivity(intent);
                            }
                        })
                        .create(R.style.QMUI_Dialog).show();
                break;
        }
    }
}
