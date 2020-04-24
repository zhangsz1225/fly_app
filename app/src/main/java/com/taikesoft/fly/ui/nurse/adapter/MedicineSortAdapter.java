package com.taikesoft.fly.ui.nurse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.view.CircleImageView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.ui.nurse.entity.MedicineInfoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 药品列表排序适配器
 */
public class MedicineSortAdapter extends BaseAdapter implements SectionIndexer {
    private List<MedicineInfoEntity> mList;
    private List<MedicineInfoEntity> mSelectedList;
    private Context mContext;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public MedicineSortAdapter(Context mContext, List<MedicineInfoEntity> list, List<MedicineInfoEntity> childData) {
        this.mContext = mContext;
        mSelectedList = new ArrayList<>();
        mSelectedList.addAll(childData);
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     */
    public void updateListView(/*List<MedicineInfoEntity> list*/List<MedicineInfoEntity> selectedList) {
        /*if (list == null) {
			this.mList = new ArrayList<>();
		} else {
			this.mList = list;
		}*/
        mSelectedList.clear();
        mSelectedList.addAll(selectedList);
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
        final MedicineInfoEntity mContent = mList.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_medicine, null);
            viewHolder.tvTitle = (TextView) view.findViewById(R.id.tv_title_name);
            viewHolder.tvHeader = (TextView) view.findViewById(R.id.tv_medicine_header);
            viewHolder.ivHeader = (CircleImageView) view.findViewById(R.id.iv_medicine_header);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.cbChecked = (CheckBox) view.findViewById(R.id.cbChecked);
            viewHolder.viewDivider = view.findViewById(R.id.view_divider);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.viewDivider.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
            viewHolder.viewDivider.setVisibility(View.GONE);
        }
        String name = this.mList.get(position).getName();
        String backGroundColor = this.mList.get(position).getBackgroundColor();
        String facePhotoUrl = this.mList.get(position).getFacePhoto();
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
        viewHolder.tvTitle.setText(name);
        viewHolder.cbChecked.setChecked(isSelected(mContent));

        return view;

    }

    public static class ViewHolder {
        public TextView tvLetter;
        public TextView tvTitle;
        public TextView tvHeader;
        public CircleImageView ivHeader;
        public CheckBox cbChecked;
        public View viewDivider;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    private boolean isSelected(MedicineInfoEntity model) {
        for (int i = 0; i < mSelectedList.size(); i++) {
            if (TextUtils.equals(mSelectedList.get(i).getId(), model.getId())) {
                return true;
            }
        }
        return false;
    }

    public void toggleChecked(int position) {
        if (isSelected(mList.get(position))) {
            removeSelected(position);
        } else {
            setSelected(position);
        }

    }

    private void setSelected(int position) {
        mSelectedList.add(mList.get(position));
    }

    private void removeSelected(int position) {
        for (int i = 0; i < mSelectedList.size(); i++) {
            if (TextUtils.equals(mSelectedList.get(i).getId(), mList.get(position).getId())) {
                mSelectedList.remove(mSelectedList.get(i));
                break;
            }
        }
    }

    public List<MedicineInfoEntity> getSelectedList() {
        return mSelectedList;
    }
}