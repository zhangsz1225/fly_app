package com.taikesoft.fly.ui.child;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.BaseActivity;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.view.SideBarView;
import com.taikesoft.fly.business.common.view.children.CharacterParser;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.business.widget.children.CNPinyin;
import com.taikesoft.fly.business.widget.children.CNPinyinFactory;
import com.taikesoft.fly.business.widget.children.CNPinyinIndex;
import com.taikesoft.fly.business.widget.children.CNPinyinIndexFactory;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.PinyinMedicineComparator;
import com.taikesoft.fly.ui.nurse.TakeMedicineActivity;
import com.taikesoft.fly.ui.nurse.adapter.MedicineSortAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchChildrenAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchMedicineAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchMedicineHeaderAdapter;
import com.taikesoft.fly.ui.nurse.bean.DictionaryBean;
import com.taikesoft.fly.ui.nurse.entity.MedicineInfoEntity;
import com.taikesoft.fly.ui.nurse.entity.SelectTaskEntity;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 儿童--选服药--选药品
 */

public class ChooseChildMedicineActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout mLayout;
    private RelativeLayout mRlBack;
    private TextView mTvTitleBarRight;
    private SideBarView sideBarView;
    private TextView dialog;
    ListView mListView;
    private MedicineSortAdapter medicineSortadapter;
    public static final int MSG_INITS_RESULTS = 0x0000;
    public static final int MSG_SEARCH_RESULTS = 0x0001;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<MedicineInfoEntity> sourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinMedicineComparator pinyinMedicineComparator;
    private EditText mEtSearch;
    private List<CNPinyinIndex<MedicineInfoEntity>> filterDataList = new ArrayList<>();
    private ListView mSearchListView;
    private TextView mTvNoData;
    private SearchMedicineAdapter searchMedicineAdapter;
    private ArrayList<CNPinyin<MedicineInfoEntity>> mCnPinyinArrayList;
    private LinearLayout mLlSearchResults;
    private String editText;
    private List<MedicineInfoEntity> mSearchHeaderList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ImageView mIvSearchIcon;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private SearchMedicineHeaderAdapter searchMedicineHeaderAdapter;
    private ArrayList<MedicineInfoEntity> mMedicineDatas = new ArrayList<>();
    private List<ChildInfoEntity> mChildren = new ArrayList<>();
    private DictionaryBean selectedMedicineUnit;
    private String dosage;
    private SelectTaskEntity task;
    private int width;
    private int height;
    private String mTagId;

    @Override
    public void setView() {
        setContentView(R.layout.activity_choose_medicine);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, AppContext.mResource.getColor(R.color.app_color_blue));
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
    protected void initView() {
        mRlBack = findViewById(R.id.rl_back);
        mTvTitleBarRight = findViewById(R.id.tv_titlebar_right);
        mTvTitleBarRight.setText(R.string.choose_finish);
        mTvTitleBarRight.setOnClickListener(this);
        RelativeLayout rlFinish = findViewById(R.id.rl_my_download);
        rlFinish.setOnClickListener(this);
        mTagId = getIntent().getExtras().getString("mTagId");
        MedicineInfoEntity entity = (MedicineInfoEntity) getIntent().getSerializableExtra("mMedicine");
        mMedicineDatas.add(entity);
        selectedMedicineUnit = (DictionaryBean) getIntent().getSerializableExtra("mMedicineUnit");
        dosage = getIntent().getStringExtra("mDosage");
        mChildren = (List<ChildInfoEntity>) getIntent().getSerializableExtra("mChildData");
        if (mChildren == null) {
            mChildren = new ArrayList<>();
        }
        task = (SelectTaskEntity) getIntent().getSerializableExtra("mSelectTaskEntity");
        setTitle(AppContext.mResource.getString(R.string.choose_medicine));
        sideBarView = findViewById(R.id.sidrbar);
        dialog = findViewById(R.id.dialog);
        sideBarView.setTextView(dialog);
        mListView = findViewById(R.id.lv_contacts);
        //搜索图标
        mIvSearchIcon = findViewById(R.id.iv_search_icon);
        //搜索recyclerView
        mRecyclerView = findViewById(R.id.recyclerview_header);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //搜索editText
        mEtSearch = findViewById(R.id.et_search_person);
        //搜索记录布局
        mLlSearchResults = findViewById(R.id.ll_search_results);
        //搜索记录
        mSearchListView = findViewById(R.id.lv_search_contacts);

        //无数据页面
        mLayout = findViewById(R.id.ll_content);
        mLayout.setVisibility(View.GONE);
        //没有搜索记录时
        mTvNoData = findViewById(R.id.tv_no_search_data);
        rlNoData = findViewById(R.id.rl_no_data);
        ivNoData = findViewById(R.id.iv_no_data);
        tvNoData = findViewById(R.id.tv_no_data);
        initNoDataView();

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinMedicineComparator = new PinyinMedicineComparator();

        //搜索记录listView
        searchMedicineAdapter = new SearchMedicineAdapter(this, filterDataList);
        mSearchListView.setAdapter(searchMedicineAdapter);
        //搜索头部添加recyclerView
        searchMedicineHeaderAdapter = new SearchMedicineHeaderAdapter(this, mSearchHeaderList, width);
        mRecyclerView.setAdapter(searchMedicineHeaderAdapter);

        requestContractData();
    }

    private void requestContractData() {
        initLoading("");
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.LIST_MEDICINE, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    Type type = new TypeToken<BaseListData<MedicineInfoEntity>>() {
                    }.getType();
                    BaseListData<MedicineInfoEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        List<MedicineInfoEntity> personList = datas.getData();
                        if (personList != null && personList.size() != 0) {
                            if (mTvTitleBarRight.getVisibility() != View.VISIBLE) {
                                mTvTitleBarRight.setVisibility(View.VISIBLE);
                            }
                            if (mLayout != null && mLayout.getVisibility() != View.VISIBLE) {
                                mLayout.setVisibility(View.VISIBLE);
                            }
                            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                                rlNoData.setVisibility(View.GONE);
                            }
                            sourceDateList = filledData(personList);
                            //集合中所有拼音的初始化，考虑在子线程中运行
                            CNPinyinFactory.createCNPinyinList(sourceDateList, MSG_INITS_RESULTS, mHandler);
                            // 根据a-z进行排序源数据
                            Collections.sort(sourceDateList, pinyinMedicineComparator);
                            //列表listView
                            if (mMedicineDatas == null) {
                                mMedicineDatas = new ArrayList<>();
                            }
                            medicineSortadapter = new MedicineSortAdapter(ChooseChildMedicineActivity.this, sourceDateList, mMedicineDatas);
                            mListView.setAdapter(medicineSortadapter);
                            initHeadList();
                        } else {
                            initNoData();
                        }
                        return;
                    } else {
                        initNoData();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                initNoData();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                hideLoading();
                initNoInternetView();
            }
        });
    }

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(this, R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(AppContext.mContext.getString(R.string.medicine_list_no_data));
    }

    //网络异常
    private void initNoInternetView() {
        if (ivNoData == null) return;
        ivNoData.setImageResource(R.drawable.no_internet_icon);
        tvNoData.setText(AppContext.mContext.getString(R.string.please_check_network));
        if (mTvTitleBarRight.getVisibility() == View.VISIBLE) {
            mTvTitleBarRight.setVisibility(View.GONE);
        }
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }
        if (mTvTitleBarRight.getVisibility() == View.VISIBLE) {
            mTvTitleBarRight.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        // 设置右侧[A-Z]快速导航栏触摸监听
        sideBarView.setOnTouchingLetterChangedListener(new SideBarView.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (medicineSortadapter != null && mListView != null) {
                    /*if (StringUtils.equals(s, "#")) {
                        mListView.setSelection(sourceDateList.size() - 1);
                        return;
                    }*/
                    // 该字母首次出现的位置
                    int position = medicineSortadapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        mListView.setSelection(position);
                    }
                }
            }
        });
        // item事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                MedicineSortAdapter.ViewHolder viewHolder = (MedicineSortAdapter.ViewHolder) view.getTag();
                viewHolder.cbChecked.performClick();
                medicineSortadapter.toggleChecked(position);
                initHeadList();
            }
        });

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    editText = s.toString();
                    mLlSearchResults.setVisibility(View.VISIBLE);
                    //搜索考虑在子线程中运行
                    CNPinyinIndexFactory.indexList(mCnPinyinArrayList, s.toString(), MSG_SEARCH_RESULTS, mHandler);
                } else {
                    mLlSearchResults.setVisibility(View.GONE);
                }
            }
        });

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchChildrenAdapter.ViewHolder viewHolder = (SearchChildrenAdapter.ViewHolder) view.getTag();
                viewHolder.cbChecked.performClick();
                searchMedicineAdapter.toggleChecked(position);
                List<MedicineInfoEntity> searchSelectedList = searchMedicineAdapter.getSelectedList();
                medicineSortadapter.updateListView(searchSelectedList);
                mLlSearchResults.setVisibility(View.GONE);
                mEtSearch.setText("");
                initHeadList();
            }
        });

        //头像点击删除
        searchMedicineHeaderAdapter.setOnItemClickListener(new SearchMedicineHeaderAdapter.OnClickLisenerI() {
            @Override
            public void setOnClickListener(View view, int position) {
                mSearchHeaderList.remove(position);
                medicineSortadapter.updateListView(mSearchHeaderList);
                initHeadList();
                if (mLlSearchResults != null && mLlSearchResults.getVisibility() == View.VISIBLE) {
                    if (mSearchListView != null && mSearchListView.getVisibility() == View.VISIBLE) {
                        searchMedicineAdapter.updateListView2(mSearchHeaderList);
                    }
                }
            }
        });
    }

    private void initHeadList() {
        List<MedicineInfoEntity> contactsSelectedList = medicineSortadapter.getSelectedList();
        if (contactsSelectedList.size() > 0) {
            mIvSearchIcon.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mSearchHeaderList.clear();
            mSearchHeaderList.addAll(contactsSelectedList);
            searchMedicineHeaderAdapter.notifyDataSetChanged();
            int itemCount = searchMedicineHeaderAdapter.getItemCount();
            ViewGroup.LayoutParams layoutParams = mRecyclerView.getLayoutParams();
            if (itemCount >= 6) {
                layoutParams.width = 3 * width / 4;
            } else {
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            }
            mRecyclerView.setLayoutParams(layoutParams);
            mRecyclerView.scrollToPosition(itemCount - 1);
        } else {
            mIvSearchIcon.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_INITS_RESULTS:
                    mCnPinyinArrayList = (ArrayList<CNPinyin<MedicineInfoEntity>>) msg.obj;
                    break;
                case MSG_SEARCH_RESULTS:
                    ArrayList<CNPinyinIndex<MedicineInfoEntity>> cnPinyinIndexArrayList = (ArrayList<CNPinyinIndex<MedicineInfoEntity>>) msg.obj;
                    if (cnPinyinIndexArrayList != null && cnPinyinIndexArrayList.size() != 0) {
                        if (mSearchListView != null && mSearchListView.getVisibility() == View.GONE) {
                            mSearchListView.setVisibility(View.VISIBLE);
                        }
                        if (mTvNoData != null && mTvNoData.getVisibility() == View.VISIBLE) {
                            mTvNoData.setVisibility(View.GONE);
                        }

                        List<MedicineInfoEntity> contactSelectedList = medicineSortadapter.getSelectedList();
                        searchMedicineAdapter.updateListView(cnPinyinIndexArrayList, contactSelectedList);
                    } else {
                        if (mSearchListView != null && mSearchListView.getVisibility() == View.VISIBLE) {
                            mSearchListView.setVisibility(View.GONE);
                        }
                        if (mTvNoData != null && mTvNoData.getVisibility() == View.GONE) {
                            mTvNoData.setVisibility(View.VISIBLE);
                        }
                        mTvNoData.setText(Html.fromHtml("没有找到“" + "<font color='#FD9426' size='20'>" + editText + "</font>" + "”相关的结果"));
                    }
                    break;
            }
        }
    };


    /**
     * 为ListView填充数据
     *
     * @return
     */
    private List<MedicineInfoEntity> filledData(List<MedicineInfoEntity> medicineList) {
        List<MedicineInfoEntity> mSortList = new ArrayList<MedicineInfoEntity>();
        for (int i = 0; i < medicineList.size(); i++) {
            MedicineInfoEntity sortModel = new MedicineInfoEntity();
            MedicineInfoEntity medicine = medicineList.get(i);
            sortModel.setName(medicine.getName());
            sortModel.setFacePhoto(medicine.getFacePhoto());
            sortModel.setId(medicine.getId());
            sortModel.setBackgroundColor(medicine.getBackgroundColor());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(medicineList.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_titlebar_right:
                List<MedicineInfoEntity> selectedList = medicineSortadapter.getSelectedList();
                Intent intentNurse = new Intent(this, NurseFillActivity.class);
                intentNurse.putExtra("mChildData", (Serializable) mChildren);
                intentNurse.putExtra("mSelectTaskEntity", (Serializable) task);
                intentNurse.putExtra("mMedicineData", (Serializable) selectedList);
                intentNurse.putExtra("mDosage", dosage);
                intentNurse.putExtra("mMedicineUnit", (Serializable) selectedMedicineUnit);
                this.setResult(2, intentNurse);
                startActivity(intentNurse);
                closeKeyboard();
                finish();
                if (StringUtils.equals("nurseFill", mTagId)) {
                    NurseFillActivity.mFillActivity.finish();
                }
                break;
            case R.id.rl_my_download:
                List<MedicineInfoEntity> contactsSelectedList = medicineSortadapter.getSelectedList();
                Intent intent = new Intent(this, NurseFillActivity.class);
                intent.putExtra("mChildData", (Serializable) mChildren);
                intent.putExtra("mSelectTaskEntity", (Serializable) task);
                intent.putExtra("mMedicineData", (Serializable) contactsSelectedList);
                intent.putExtra("mDosage", dosage);
                intent.putExtra("mMedicineUnit", (Serializable) selectedMedicineUnit);
                this.setResult(2, intent);
                startActivity(intent);
                closeKeyboard();
                finish();
                if (StringUtils.equals("nurseFill", mTagId)) {
                    NurseFillActivity.mFillActivity.finish();
                }
                break;
        }
    }

    @Override
    protected boolean hasLoading() {
        return true;
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
