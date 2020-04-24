package com.taikesoft.fly.ui.nurse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.view.CircleImageView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.widget.children.CNPinyinIndex;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索儿童适配器
 */

public class SearchChildrenAdapter extends BaseAdapter {
    private List<CNPinyinIndex<ChildInfoEntity>> mList;
    private List<ChildInfoEntity> mSelectedList;
    private Context mContext;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public SearchChildrenAdapter(Context mContext, List<CNPinyinIndex<ChildInfoEntity>> list) {
        this.mContext = mContext;
        mSelectedList = new ArrayList<>();
        this.mList = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(List<CNPinyinIndex<ChildInfoEntity>> newDatas, List<ChildInfoEntity> contactSelectedList) {
        this.mList.clear();
        this.mList.addAll(newDatas);
        mSelectedList.clear();
        mSelectedList.addAll(contactSelectedList);
        notifyDataSetChanged();
    }

    public void updateListView2(List<ChildInfoEntity> contactSelectedList) {
        mSelectedList.clear();
        mSelectedList.addAll(contactSelectedList);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        CNPinyinIndex<ChildInfoEntity> cnPinyinIndex = mList.get(position);
        ChildInfoEntity personInfo = cnPinyinIndex.cnPinyin.data;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_child, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title_name);
            viewHolder.tvHeader = (TextView) view.findViewById(R.id.tv_person_header);
            viewHolder.ivHeader = (CircleImageView) view.findViewById(R.id.iv_person_header);
            viewHolder.tvGroup = (TextView) view.findViewById(R.id.tv_content_group);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.cbChecked = (CheckBox) view.findViewById(R.id.cbChecked);
            viewHolder.viewDivider = view.findViewById(R.id.view_divider);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tvLetter.setVisibility(View.GONE);
        viewHolder.viewDivider.setVisibility(View.GONE);
        String name = personInfo.getName();
        String backGroundColor = personInfo.getBackgroundColor();
        String facePhotoUrl = personInfo.getFacePhoto();
        if (StringUtils.isEmpty(facePhotoUrl)) {
            viewHolder.tvHeader.setVisibility(View.VISIBLE);
            viewHolder.ivHeader.setVisibility(View.GONE);
            GradientDrawable myGrad = (GradientDrawable) viewHolder.tvHeader.getBackground();
            if (!StringUtils.isEmpty(backGroundColor)) {
                myGrad.setColor(Color.parseColor(backGroundColor));
            } else {
                myGrad.setColor(Color.parseColor("#38AFF7"));
            }
            if (!StringUtils.isEmpty(name) && name.length() >= 2) {
                viewHolder.tvHeader.setText(name.substring(name.length() - 2, name.length()));
            } else {
                viewHolder.tvHeader.setText(name);
            }
        } else {
            viewHolder.ivHeader.setVisibility(View.VISIBLE);
            viewHolder.tvHeader.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + facePhotoUrl, viewHolder.ivHeader, mOption);
        }
        int startText = cnPinyinIndex.startText;
        int endText = cnPinyinIndex.endText;
        int startContent = cnPinyinIndex.startContent;
        int endContent = cnPinyinIndex.endContent;
        if (endText != 0 && endText >= startText) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(personInfo.chineseText());
            ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#FD9426"));
            ssb.setSpan(span, startText, endText, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tvTitle.setText(ssb);
        } else {
            viewHolder.tvTitle.setText(name);
        }
        if (endContent != 0 && endContent >= startContent) {
            SpannableStringBuilder ssb1 = new SpannableStringBuilder(personInfo.chineseContent());
            ForegroundColorSpan span1 = new ForegroundColorSpan(Color.parseColor("#FD9426"));
            ssb1.setSpan(span1, startContent, endContent, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tvGroup.setText(ssb1);
        } else {
            viewHolder.tvGroup.setText(personInfo.getNursingRoom());
        }
        viewHolder.cbChecked.setChecked(isSelected(personInfo));
        return view;
    }

    public static class ViewHolder {
        public TextView tvLetter;
        public TextView tvTitle;
        public TextView tvHeader;
        public CircleImageView ivHeader;
        public TextView tvGroup;
        public CheckBox cbChecked;
        public View viewDivider;
    }

    private boolean isSelected(ChildInfoEntity personInfo) {
        for (int i = 0; i < mSelectedList.size(); i++) {
            if (StringUtils.equals(mSelectedList.get(i).getId(), personInfo.getId())) {
                return true;
            }
        }
        return false;
    }

    public void toggleChecked(int position) {
        ChildInfoEntity personInfo = mList.get(position).cnPinyin.data;
        if (isSelected(personInfo)) {
            removeSelected(personInfo);
        } else {
            setSelected(personInfo);
        }
    }

    private void setSelected(ChildInfoEntity personInfo) {
        mSelectedList.add(personInfo);
    }

    private void removeSelected(ChildInfoEntity personInfo) {
        for (int i = 0; i < mSelectedList.size(); i++) {
            if (StringUtils.equals(mSelectedList.get(i).getId(), personInfo.getId())) {
                mSelectedList.remove(mSelectedList.get(i));
                break;
            }
        }
    }

    public List<ChildInfoEntity> getSelectedList() {
        return mSelectedList;
    }
}
