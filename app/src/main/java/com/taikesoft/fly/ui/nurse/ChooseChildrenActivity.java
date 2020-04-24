package com.taikesoft.fly.ui.nurse;

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
import com.taikesoft.fly.business.common.utils.DateUtils;
import com.taikesoft.fly.business.common.utils.StatusBarUtil;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.view.SideBarView;
import com.taikesoft.fly.business.common.view.children.CharacterParser;
import com.taikesoft.fly.business.common.view.children.PinyinComparator;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.constant.ResultStatus;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.entity.FillEntity;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.business.widget.children.CNPinyin;
import com.taikesoft.fly.business.widget.children.CNPinyinFactory;
import com.taikesoft.fly.business.widget.children.CNPinyinIndex;
import com.taikesoft.fly.business.widget.children.CNPinyinIndexFactory;
import com.taikesoft.fly.ui.child.NurseFillActivity;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;
import com.taikesoft.fly.ui.nurse.adapter.ChildrenSortAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchChildrenAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchHeaderAdapter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 选护理主页的护理项后，选择儿童
 */

public class ChooseChildrenActivity extends BaseActivity implements View.OnClickListener {

    private SideBarView sideBarView;
    private TextView dialog;
    ListView mListView;
    private ChildrenSortAdapter childrenSortadapter;
    public static final int MSG_INITS_RESULTS = 0x0000;
    public static final int MSG_SEARCH_RESULTS = 0x0001;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<ChildInfoEntity> sourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private EditText mEtSearch;
    private List<CNPinyinIndex<ChildInfoEntity>> filterDataList = new ArrayList<>();
    private ListView mSearchListView;
    private TextView mTvNoData;
    private SearchChildrenAdapter searchChildrenAdapter;
    private ArrayList<CNPinyin<ChildInfoEntity>> mCnPinyinArrayList;
    private LinearLayout mLlSearchResults;
    private String editText;
    private List<ChildInfoEntity> mSearchHeaderList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ImageView mIvSearchIcon;
    private RelativeLayout rlNoData;
    private ImageView ivNoData;
    private TextView tvNoData;
    private SearchHeaderAdapter searchHeaderAdapter;
    private int width;
    private int height;
    private String mTagId;
    private NurseItemBean mNurseItemBean;
    private ArrayList<ChildInfoEntity> mChildDatas;
    private LinearLayout mLayout;
    private RelativeLayout mRlBack;
    private TextView mTvTitleBarRight;

    @Override
    public void setView() {
        setContentView(R.layout.activity_choose_children);
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
        mNurseItemBean = (NurseItemBean) getIntent().getSerializableExtra("mNurseItemBean");
        mChildDatas = (ArrayList<ChildInfoEntity>) getIntent().getSerializableExtra("childData");
        setTitle(mNurseItemBean.getNurseItem() + AppContext.mResource.getString(R.string.choose_children));
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
        pinyinComparator = new PinyinComparator();

        //搜索记录listView
        searchChildrenAdapter = new SearchChildrenAdapter(this, filterDataList);
        mSearchListView.setAdapter(searchChildrenAdapter);
        //搜索头部添加recyclerView
        searchHeaderAdapter = new SearchHeaderAdapter(this, mSearchHeaderList, width);
        mRecyclerView.setAdapter(searchHeaderAdapter);

        requestContractData();
    }

    private void requestContractData() {
        initLoading("");
        JSONObject obj = new JSONObject();
        HttpEntity entity = null;
        try {
            obj.put("taskDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
            obj.put("itemId", mNurseItemBean.getId());
            obj.put("nurseTime", mNurseItemBean.getNurseTime());
            entity = new StringEntity(obj.toString(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        MainApi.requestCommon(this, AppConfig.TASK_CHILDREN, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                hideLoading();
                try {
                    String httpResult = new String(bytes);
                    Type type = new TypeToken<BaseListData<ChildInfoEntity>>() {
                    }.getType();
                    BaseListData<ChildInfoEntity> datas = new Gson().fromJson(httpResult, type);
                    if (StringUtils.equals(datas.getState(), ResultStatus.SUCCESS)) {
                        List<ChildInfoEntity> personList = datas.getData();
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
                            Collections.sort(sourceDateList, pinyinComparator);
                            //列表listView
                            if (mChildDatas == null) {
                                mChildDatas = new ArrayList<>();
                            }
                            childrenSortadapter = new ChildrenSortAdapter(ChooseChildrenActivity.this, sourceDateList, mChildDatas);
                            mListView.setAdapter(childrenSortadapter);
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
        tvNoData.setText(AppContext.mContext.getString(R.string.child_list_no_data));
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
                if (childrenSortadapter != null && mListView != null) {
                    /*if (StringUtils.equals(s, "#")) {
                        mListView.setSelection(sourceDateList.size() - 1);
                        return;
                    }*/
                    // 该字母首次出现的位置
                    int position = childrenSortadapter.getPositionForSection(s.charAt(0));
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
                ChildrenSortAdapter.ViewHolder viewHolder = (ChildrenSortAdapter.ViewHolder) view.getTag();
                viewHolder.cbChecked.performClick();
                childrenSortadapter.toggleChecked(position);
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
                searchChildrenAdapter.toggleChecked(position);
                List<ChildInfoEntity> searchSelectedList = searchChildrenAdapter.getSelectedList();
                childrenSortadapter.updateListView(searchSelectedList);
                mLlSearchResults.setVisibility(View.GONE);
                mEtSearch.setText("");
                initHeadList();
            }
        });

        //头像点击删除
        searchHeaderAdapter.setOnItemClickListener(new SearchHeaderAdapter.OnClickLisenerI() {
            @Override
            public void setOnClickListener(View view, int position) {
                mSearchHeaderList.remove(position);
                childrenSortadapter.updateListView(mSearchHeaderList);
                initHeadList();
                if (mLlSearchResults != null && mLlSearchResults.getVisibility() == View.VISIBLE) {
                    if (mSearchListView != null && mSearchListView.getVisibility() == View.VISIBLE) {
                        searchChildrenAdapter.updateListView2(mSearchHeaderList);
                    }
                }
            }
        });
    }

    private void initHeadList() {
        List<ChildInfoEntity> contactsSelectedList = childrenSortadapter.getSelectedList();
        if (contactsSelectedList.size() > 0) {
            mIvSearchIcon.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mSearchHeaderList.clear();
            mSearchHeaderList.addAll(contactsSelectedList);
            searchHeaderAdapter.notifyDataSetChanged();
            int itemCount = searchHeaderAdapter.getItemCount();
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
                    mCnPinyinArrayList = (ArrayList<CNPinyin<ChildInfoEntity>>) msg.obj;
                    break;
                case MSG_SEARCH_RESULTS:
                    ArrayList<CNPinyinIndex<ChildInfoEntity>> cnPinyinIndexArrayList = (ArrayList<CNPinyinIndex<ChildInfoEntity>>) msg.obj;
                    if (cnPinyinIndexArrayList != null && cnPinyinIndexArrayList.size() != 0) {
                        if (mSearchListView != null && mSearchListView.getVisibility() == View.GONE) {
                            mSearchListView.setVisibility(View.VISIBLE);
                        }
                        if (mTvNoData != null && mTvNoData.getVisibility() == View.VISIBLE) {
                            mTvNoData.setVisibility(View.GONE);
                        }

                        List<ChildInfoEntity> contactSelectedList = childrenSortadapter.getSelectedList();
                        searchChildrenAdapter.updateListView(cnPinyinIndexArrayList, contactSelectedList);
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
    private List<ChildInfoEntity> filledData(List<ChildInfoEntity> children) {
        List<ChildInfoEntity> mSortList = new ArrayList<ChildInfoEntity>();
        for (int i = 0; i < children.size(); i++) {
            ChildInfoEntity sortModel = new ChildInfoEntity();
            ChildInfoEntity child = children.get(i);
            sortModel.setTaskId(child.getTaskId());
            sortModel.setName(child.getName());
            sortModel.setProjectGroup(child.getProjectGroup());
            sortModel.setNursingRoom(child.getNursingRoom());
            sortModel.setNurseGroup(child.getNurseGroup());
            sortModel.setFacePhoto(child.getFacePhoto());
            sortModel.setId(child.getId());
            sortModel.setIdNumber(child.getIdNumber());
            sortModel.setBackgroundColor(child.getBackgroundColor());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(children.get(i).getName());
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
                List<ChildInfoEntity> selectedList = childrenSortadapter.getSelectedList();
                if (StringUtils.equals("喝水喂奶", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, DrinkWaterActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) selectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else if (StringUtils.equals("更换", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, ChangeClothesActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) selectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else if (StringUtils.equals("体温", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, MeasureTemperatureActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) selectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else if (StringUtils.equals("服药", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, TakeMedicineActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) selectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                    if (StringUtils.equals("takeMedicine", mTagId)) {
                        TakeMedicineActivity.mTakeActivity.finish();
                    }
                } else if (StringUtils.equals("水果点心", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, HaveSnackActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) selectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else {
                    Intent intent = new Intent(this, NoNeedFillInActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) selectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                }
                break;
            case R.id.rl_my_download:
                List<ChildInfoEntity> contactsSelectedList = childrenSortadapter.getSelectedList();
                if (StringUtils.equals("喝水喂奶", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, DrinkWaterActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) contactsSelectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else if (StringUtils.equals("更换", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, ChangeClothesActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) contactsSelectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else if (StringUtils.equals("体温", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, MeasureTemperatureActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) contactsSelectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else if (StringUtils.equals("服药", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, TakeMedicineActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) contactsSelectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                    if (StringUtils.equals("takeMedicine", mTagId)) {
                        TakeMedicineActivity.mTakeActivity.finish();
                    }
                } else if (StringUtils.equals("水果点心", mNurseItemBean.getNurseItem())) {
                    Intent intent = new Intent(this, HaveSnackActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) contactsSelectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
                } else {
                    Intent intent = new Intent(this, NoNeedFillInActivity.class);
                    intent.putExtra("mNurseItemBean", mNurseItemBean);
                    intent.putExtra("mChildData", (Serializable) contactsSelectedList);
                    this.setResult(1, intent);
                    startActivity(intent);
                    closeKeyboard();
                    finish();
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
