package com.taikesoft.fly.ui.nurse;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.Base;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.CommonUtil;
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.CircleImageView;
import com.taikesoft.fly.business.common.view.LineBreakLayout;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.constant.SysCode;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.storage.SharedPreferencesManager;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 夜间巡视
 */
public class NightPatrolActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.rl_back)
    RelativeLayout mRlBack;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_child_name)
    TextView tvChildName;
    @BindView(R.id.ll_child)
    LinearLayout ll_child;
    @BindView(R.id.tv_person_header)
    TextView tvPersonHeader;
    @BindView(R.id.iv_child)
    CircleImageView ivChild;
    @BindView(R.id.rbOk)
    RadioButton rbOk;
    @BindView(R.id.rbNot)
    RadioButton rbNot;
    @BindView(R.id.tv_nurse_date)
    TextView tvNurseDate;
    @BindView(R.id.lbl_patrol)
    LineBreakLayout lbl_patrol;
    @BindView(R.id.tv_nurse_time)
    TextView tvNurseTime;
    @BindView(R.id.tv_patrol_time)
    TextView tvPatrolTime;
    @BindView(R.id.et_memo)
    EditText etMemo;
    @BindView(R.id.tv_nurse)
    TextView tvNurse;
    @BindView(R.id.btn_save)
    Button mBtnSave;
    private NfcAdapter mNfcAdapter = null;
    private PendingIntent mPendingIntent = null;
    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();
    private CommonUtil mCommonUtil;
    private List<DictionaryBean> selectedLables;
    private List<DictionaryBean> lable;
    private NurseItemBean mNurseItemBean;
    private List<ChildInfoEntity> mChildren = new ArrayList<>();
    private String mSpiritType = "好";
    @Override
    public void setView() {
        setContentView(R.layout.activity_night_patrol);
        ButterKnife.bind(this);
    }

    @Override
    protected void setBack() {
        mRlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                finish();
            }
        });
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
    }

    @Override
    protected void initView() {
        mNurseItemBean = (NurseItemBean) getIntent().getSerializableExtra("mNurseItemBean");
        setTitle(mNurseItemBean.getNurseItem());
        tvTip.setVisibility(View.VISIBLE);
        rbOk.setChecked(true);
        String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        tvNurseDate.setText(dateStr.substring(0,10));
        tvNurse.setText(SharedPreferencesManager.getString(ResultStatus.REAL_NAME));
        tvPatrolTime.setText(dateStr.substring(11, 16));
        tvNurseTime.setText(mNurseItemBean.getNurseTime());
        rbOk.setOnClickListener(this);
        rbNot.setOnClickListener(this);
        etMemo.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mCommonUtil = CommonUtil.getInstance();
    }

    @Override
    public void setListener() {
    }

    private void requestTypes() {
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
        MainApi.requestCommon(this, AppConfig.LIST_PATROL_TYPES, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                // hideLoading();
                try {
                    String httpResult = new String(bytes);
                    TLog.log(AppConfig.LIST_PATROL_TYPES + "-->" + httpResult);
                    Type type = new TypeToken<BaseListData<DictionaryBean>>() {
                    }.getType();
                    BaseListData<DictionaryBean> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), SysCode.SUCCESS)) {
                        lable = datas.getData();
                        //设置标签
                        lbl_patrol.setLables(lable, true);
                        //获取选中的标签
                        selectedLables = lbl_patrol.getSelectedLables();
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
                hideLoading();
            }
        });
    }

    @Override
    protected void initWebData() {
        initLoading("");
        requestTypes();
    }

    /**
     * NFC 刷卡触发事件
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            NdefMessage[] msgs;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }

            handleNdefMessages(msgs);
        }
    }

    /**
     * 解析接收到的NFC消息数据
     *
     * @param p
     * @return
     */
    private String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        String cardId = getDec(id) + "";
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");
        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }
        return cardId;
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    /**
     * 解析并处理读到的 nfc 消息
     *
     * @param msgs nfc消息
     */
    private void handleNdefMessages(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) {
            return;
        }
        // Parse the first MessageEntity in the list
        // Build views for all of the sub records
        NdefRecord[] ndefRecords = msgs[0].getRecords();
        for (int i = 0; i < ndefRecords.length; i++) {
            NdefRecord record = ndefRecords[i];
            String cardId = new String(record.getPayload());
            TLog.log("handleNdefMessages --> " + cardId);
            getChildInfo(cardId);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.rbOk:
                rbOk.setChecked(true);
                mSpiritType = "好";
                break;
            case R.id.rbNot:
                rbNot.setChecked(true);
                mSpiritType = "不好";
                break;
            case R.id.btn_save:
                if (TextUtils.isEmpty(tvChildName.getText())) {
                    AppContext.showToast("请开启NFC找儿童");
                } else {
                    if (selectedLables.size() == 0) {
                        AppContext.showToast("请选择夜间状态");
                    } else {
                        String createTime = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                        List<String> lables = new ArrayList<>();
                        for (DictionaryBean bean : selectedLables) {
                            lables.add(bean.getName());
                        }
                        HttpEntity entity = mCommonUtil.fillHttpEntity(mChildren, mNurseItemBean, createTime, TextUtils.join(",", lables), null, null, etMemo.getText().toString(),mSpiritType);
                        submitNurseRecord(entity, AppConfig.INSERT_NURSE_NOTE);
                    }
                }
                break;
        }
    }

    private void submitNurseRecord(HttpEntity entity, String url) {
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

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getChildInfo(String nfcId) {
        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            pData.put("nfcId", nfcId);
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
                        if (child == null) {
                            AppContext.showToast("未找到儿童信息");
                            if (tvTip.getVisibility() == View.GONE) {
                                tvTip.setVisibility(View.VISIBLE);
                            }
                            if (tvChildName.getVisibility() == View.VISIBLE) {
                                tvChildName.setVisibility(View.GONE);
                            }
                            if (ll_child.getVisibility() == View.VISIBLE) {
                                ll_child.setVisibility(View.GONE);
                            }
                        } else {
                            if (tvTip.getVisibility() == View.VISIBLE) {
                                tvTip.setVisibility(View.GONE);
                            }
                            if (tvChildName.getVisibility() == View.GONE) {
                                tvChildName.setVisibility(View.VISIBLE);
                            }
                            if (ll_child.getVisibility() == View.GONE) {
                                ll_child.setVisibility(View.VISIBLE);
                            }
                            ChildInfoEntity childEntity = new ChildInfoEntity();
                            childEntity.setId(child.getString("id"));
                            childEntity.setName(child.getString("name"));
                            childEntity.setIdNumber(child.getString("idNumber"));

                            tvChildName.setText(child.getString("name"));
                            if (!child.isNull("nursingRoom")) {
                                childEntity.setNursingRoom(child.getString("nursingRoom"));
                            }
                            if (!child.isNull("projectGroup")) {
                                childEntity.setProjectGroup(child.getString("projectGroup"));
                            }
                            if (!child.isNull("nurseGroup")) {
                                childEntity.setNurseGroup(child.getString("nurseGroup"));
                            }
                            if (!child.isNull("facePhoto")) {
                                ivChild.setVisibility(View.VISIBLE);
                                ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + child.getString("facePhoto"), ivChild, mOption);
                                tvPersonHeader.setVisibility(View.GONE);
                            } else {
                                ivChild.setVisibility(View.GONE);
                                tvPersonHeader.setVisibility(View.VISIBLE);
                                GradientDrawable myGrad = (GradientDrawable) tvPersonHeader.getBackground();
                                myGrad.setColor(Color.parseColor("#38AFF7"));
                                tvPersonHeader.setText(child.getString("name"));
                            }
                            mChildren.clear();
                            mChildren.add(childEntity);
                          /*  if(!mChildren.contains(childEntity)){
                                mChildren.add(childEntity);
                            }*/
                            String dateStr = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
                            tvPatrolTime.setText(dateStr.substring(11, 16));
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

    /**
     * 启动Activity，界面可见时
     */
    @Override
    protected void onStart() {
        super.onStart();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //一旦截获NFC消息，就会通过PendingIntent调用窗口
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        if (mNfcAdapter == null) {
            AppContext.showToast("设备不支持NFC,无法刷卡");
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
            startActivity(intent);
            AppContext.showToast("开启NFC方可刷卡");
        }
    }

    /**
     * 获得焦点，按钮可以点击
     */
    @Override
    public void onResume() {
        super.onResume();
        //设置处理优于所有其他NFC的处理
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    /**
     * 暂停Activity，界面获取焦点，按钮可以点击
     */
    @Override
    public void onPause() {
        super.onPause();
        //恢复默认状态
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }
}
