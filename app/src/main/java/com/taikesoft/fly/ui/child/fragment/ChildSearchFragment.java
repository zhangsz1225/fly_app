package com.taikesoft.fly.ui.child.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.popup.QMUIQuickAction;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.basebean.BaseListData;
import com.taikesoft.fly.business.common.utils.TLog;
import com.taikesoft.fly.business.common.view.SideBarView;
import com.taikesoft.fly.business.common.view.children.CharacterParser;
import com.taikesoft.fly.business.common.view.children.PinyinComparator;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.webapi.MainApi;
import com.taikesoft.fly.business.widget.children.CNPinyin;
import com.taikesoft.fly.business.widget.children.CNPinyinFactory;
import com.taikesoft.fly.business.widget.children.CNPinyinIndex;
import com.taikesoft.fly.business.widget.children.CNPinyinIndexFactory;
import com.taikesoft.fly.ui.child.ChildDetailActivity;
import com.taikesoft.fly.ui.child.NurseFillActivity;
import com.taikesoft.fly.ui.myinfo.NurseRecordActivity;
import com.taikesoft.fly.ui.nurse.adapter.ChildrenSortAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchChildrenAdapter;
import com.taikesoft.fly.ui.nurse.adapter.SearchHeaderAdapter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChildSearchFragment extends Fragment {
    public static final String TAG = "ChildSearchFragment";
    private SideBarView sideBarView;
    private TextView dialog;
    ListView mListView;
    private ChildrenSortAdapter childrenSortadapter;
    //private ChildrenSortBtnAdapter childrenSortBtnadapter;含按钮
    public static final int MSG_INITS_RESULTS = 0x0000;
    public static final int MSG_SEARCH_RESULTS = 0x0001;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<ChildInfoEntity> sourceDateList = new ArrayList<>();
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

    private ArrayList<ChildInfoEntity> mChildDatas = new ArrayList<>();
    private LinearLayout mLayout;
    private LinearLayout llLoading;
    private TextView tvLoading;
    private ProgressBar pbLoading;
    private String nursingRoom;
    private List<ChildInfoEntity> mChildren;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_search, container, false);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;
        mChildren = (ArrayList<ChildInfoEntity>) getArguments().getSerializable("mChildren");
        nursingRoom = getArguments().getString("nursingRoom");
        llLoading = view.findViewById(R.id.ll_loading);
        tvLoading = view.findViewById(R.id.tv_loading);
        pbLoading = view.findViewById(R.id.pb_loading);
        sideBarView = view.findViewById(R.id.sidrbar);
        dialog = view.findViewById(R.id.dialog);
        sideBarView.setTextView(dialog);
        mListView = view.findViewById(R.id.lv_contacts);
        //搜索图标
        mIvSearchIcon = view.findViewById(R.id.iv_search_icon);
        //搜索recyclerView
        mRecyclerView = view.findViewById(R.id.recyclerview_header);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        //搜索editText
        mEtSearch = view.findViewById(R.id.et_search_person);
        //搜索记录布局
        mLlSearchResults = view.findViewById(R.id.ll_search_results);
        //搜索记录
        mSearchListView = view.findViewById(R.id.lv_search_contacts);

        //无数据页面
        mLayout = view.findViewById(R.id.ll_content);
        mLayout.setVisibility(View.GONE);
        //没有搜索记录时
        mTvNoData = view.findViewById(R.id.tv_no_search_data);
        rlNoData = view.findViewById(R.id.rl_no_data);
        ivNoData = view.findViewById(R.id.iv_no_data);
        tvNoData = view.findViewById(R.id.tv_no_data);
        initNoDataView();

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        //搜索记录listView
        searchChildrenAdapter = new SearchChildrenAdapter(getContext(), filterDataList);
        mSearchListView.setAdapter(searchChildrenAdapter);
        //搜索头部添加recyclerView
        searchHeaderAdapter = new SearchHeaderAdapter(getContext(), mSearchHeaderList, width);
        mRecyclerView.setAdapter(searchHeaderAdapter);
        // 设置右侧[A-Z]快速导航栏触摸监听
        sideBarView.setOnTouchingLetterChangedListener(new SideBarView.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if (childrenSortadapter != null && mListView != null) {
                    //
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
                if (viewHolder.cbChecked.isChecked()) {
                    List<ChildInfoEntity> searchSelectedList = childrenSortadapter.getSelectedList();
                    ChildInfoEntity child = searchSelectedList.get(searchSelectedList.size() - 1);
                    QMUIPopups.quickAction(getContext(),
                            QMUIDisplayHelper.dp2px(getContext(), 56),
                            QMUIDisplayHelper.dp2px(getContext(), 56))
                            .shadow(true)
                            .edgeProtection(QMUIDisplayHelper.dp2px(getContext(), 20))
                            .addAction(new QMUIQuickAction.Action().icon(R.drawable.icon_quick_action_dict).text("查看").onClick(
                                    new QMUIQuickAction.OnClickListener() {
                                        @Override
                                        public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                            quickAction.dismiss();
                                            Intent intent =
                                                    new Intent(getContext(), ChildDetailActivity.class);
                                            intent.putExtra("childId", child.getId());
                                            closeKeyboard();
                                            startActivity(intent);
                                        }
                                    }
                            ))
                            .addAction(new QMUIQuickAction.Action().icon(R.drawable.icon_quick_action_copy).text("已护理").onClick(
                                    new QMUIQuickAction.OnClickListener() {
                                        @Override
                                        public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                            quickAction.dismiss();
                                            Intent intent =
                                                    new Intent(getContext(), NurseRecordActivity.class);
                                            intent.putExtra("child", (Serializable) child);
                                            closeKeyboard();
                                            startActivity(intent);
                                        }
                                    }
                            ))
                            .addAction(new QMUIQuickAction.Action().icon(R.drawable.icon_quick_action_share).text("去护理").onClick(
                                    new QMUIQuickAction.OnClickListener() {
                                        @Override
                                        public void onClick(QMUIQuickAction quickAction, QMUIQuickAction.Action action, int position) {
                                            quickAction.dismiss();
                                            Intent intent = new Intent(getContext(), NurseFillActivity.class);
                                            List<ChildInfoEntity> mChildData = new ArrayList<>();
                                            mChildData.add(child);
                                            intent.putExtra("mChildData", (Serializable) mChildData);
                                            closeKeyboard();
                                            startActivity(intent);
                                        }
                                    }
                            ))
                            .show(view);
                }
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
        if (mChildren != null && mChildren.size() != 0) {
            if (mLayout != null && mLayout.getVisibility() != View.VISIBLE) {
                mLayout.setVisibility(View.VISIBLE);
            }
            if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                rlNoData.setVisibility(View.GONE);
            }
            sourceDateList = filledData(mChildren);
            //集合中所有拼音的初始化，考虑在子线程中运行
            CNPinyinFactory.createCNPinyinList(sourceDateList, MSG_INITS_RESULTS, mHandler);
            // 根据a-z进行排序源数据
            Collections.sort(sourceDateList, pinyinComparator);
            //列表listView
            childrenSortadapter = new ChildrenSortAdapter(AppContext.mContext, sourceDateList, mChildDatas);
            mListView.setAdapter(childrenSortadapter);
            initHeadList();
        } else {
            getChildren();
        }
        return view;
    }
    private void getChildren() {

        HttpEntity entity = null;
        JSONObject pData = new JSONObject();
        try {
            pData.put("nursingRoom", nursingRoom);
            entity = new StringEntity(pData.toString(), "UTF-8");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MainApi.requestCommon(this.getContext(), AppConfig.CHILDREN, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String httpResult = new String(bytes);
                TLog.log(AppConfig.CHILDREN + "-->" + httpResult);
                Type type = new TypeToken<BaseListData<ChildInfoEntity>>() {
                }.getType();
                BaseListData<ChildInfoEntity> datas = new Gson().fromJson(httpResult, type);
                mChildren = datas.getData();
                if (mChildren == null) {
                    mChildren = new ArrayList<>();
                    initNoData();
                } else {
                    if (mLayout != null && mLayout.getVisibility() != View.VISIBLE) {
                        mLayout.setVisibility(View.VISIBLE);
                    }
                    if (rlNoData != null && rlNoData.getVisibility() == View.VISIBLE) {
                        rlNoData.setVisibility(View.GONE);
                    }
                    sourceDateList = filledData(mChildren);
                    //集合中所有拼音的初始化，考虑在子线程中运行
                    CNPinyinFactory.createCNPinyinList(sourceDateList, MSG_INITS_RESULTS, mHandler);
                    // 根据a-z进行排序源数据
                    Collections.sort(sourceDateList, pinyinComparator);
                    //列表listView
                    childrenSortadapter = new ChildrenSortAdapter(AppContext.mContext, sourceDateList, mChildDatas);
                    mListView.setAdapter(childrenSortadapter);
                    initHeadList();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    protected boolean hasLoading() {
        return false;
    }

    protected void initLoading(String msg) {
        try {
            if (hasLoading()) {
                llLoading.setVisibility(View.VISIBLE);
                tvLoading.setText(msg);
                llLoading.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void hideLoading() {
        if (llLoading != null && llLoading.getVisibility() == View.VISIBLE)
            System.out.println("hide");
        llLoading.setVisibility(View.GONE);
    }

    //有网无数据
    private void initNoDataView() {
        if (ivNoData == null) return;
        rlNoData.setBackground(ContextCompat.getDrawable(getContext(), R.color.white));
        ivNoData.setImageResource(R.drawable.pop_window_choose_no_content);
        tvNoData.setText(AppContext.mContext.getString(R.string.child_list_no_data));
    }

    //网络异常
    private void initNoInternetView() {
        if (ivNoData == null) return;
        ivNoData.setImageResource(R.drawable.no_internet_icon);
        tvNoData.setText(AppContext.mContext.getString(R.string.please_check_network));
    }

    private void initNoData() {
        if (rlNoData == null) return;
        if (rlNoData != null && rlNoData.getVisibility() != View.VISIBLE) {
            rlNoData.setVisibility(View.VISIBLE);
        }
        if (mLayout != null && mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }
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
    private List<ChildInfoEntity> filledData(List<ChildInfoEntity> personList) {
        List<ChildInfoEntity> mSortList = new ArrayList<ChildInfoEntity>();
        for (int i = 0; i < personList.size(); i++) {
            ChildInfoEntity sortModel = new ChildInfoEntity();
            ChildInfoEntity personInfo = personList.get(i);
            sortModel.setName(personInfo.getName());
            sortModel.setNursingRoom(personInfo.getNursingRoom());
            sortModel.setProjectGroup(personInfo.getProjectGroup());
            sortModel.setNurseGroup(personInfo.getNurseGroup());
            sortModel.setFacePhoto(personInfo.getFacePhoto());
            sortModel.setId(personInfo.getId());
            sortModel.setGender(personInfo.getGender());
            sortModel.setIdNumber(personInfo.getIdNumber());
            sortModel.setBackgroundColor(personInfo.getBackgroundColor());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(personList.get(i).getName());
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

    private void closeKeyboard() {
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public ChildSearchFragment() {
    }

    public static ChildSearchFragment newInstance(List<ChildInfoEntity> list, String nursingRoom) {
        ChildSearchFragment fragment = new ChildSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("nursingRoom", nursingRoom);
        bundle.putSerializable("mChildren", (Serializable) list);
        fragment.setArguments(bundle);
        return fragment;
    }
}
