package com.taikesoft.fly.ui.nurse.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.view.CircleImageView;
import com.taikesoft.fly.business.config.AppConfig;
import com.taikesoft.fly.business.entity.ChildInfoEntity;
import com.taikesoft.fly.business.common.utils.StringUtils;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 儿童列表项的头部适配器
 */

public class SearchHeaderAdapter extends RecyclerView.Adapter<SearchHeaderAdapter.MyViewHolder> {

    private final int width;
    private Context mContext;
    private List<ChildInfoEntity> mDatas;
    private LayoutInflater mInflater;

    private DisplayImageOptions mOption = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.load_pho_default_image)
            .showImageForEmptyUri(R.drawable.load_pho_default_image)
            .showImageOnFail(R.drawable.load_pho_default_image)
            .cacheInMemory(true)
            .cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    public SearchHeaderAdapter(Context context, List<ChildInfoEntity> datas, int width) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
        this.width = width;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_child, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = width / 8;
        view.setLayoutParams(layoutParams);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String name = mDatas.get(position).getName();
        String backGroundColor = mDatas.get(position).getBackgroundColor();
        String facePhotoUrl = mDatas.get(position).getFacePhoto();
        if (StringUtils.isEmpty(facePhotoUrl)) {
            holder.tvPerson.setVisibility(View.VISIBLE);
            holder.ivHeader.setVisibility(View.GONE);
            GradientDrawable myGrad = (GradientDrawable) holder.tvPerson.getBackground();
            if (!StringUtils.isEmpty(backGroundColor)) {
                myGrad.setColor(Color.parseColor(backGroundColor));
            } else {
                myGrad.setColor(Color.parseColor("#38AFF7"));
            }
            String subtring = "";
            if (!StringUtils.isEmpty(name)) {
                if (name.length() > 1) {
                    subtring = name.substring(name.length() - 2, name.length());
                } else {
                    subtring = name;
                }
            }
            holder.tvPerson.setText(subtring);
        } else {
            holder.ivHeader.setVisibility(View.VISIBLE);
            holder.tvPerson.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(AppConfig.IMAGE_FONT_URL + facePhotoUrl, holder.ivHeader, mOption);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLisener.setOnClickListener(holder.itemView, position);
            }
        });
    }

    private OnClickLisenerI onClickLisener;

    public void setOnItemClickListener(OnClickLisenerI onClickLisener) {
        this.onClickLisener = onClickLisener;
    }

    public interface OnClickLisenerI{
        void setOnClickListener(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvPerson;
        CircleImageView ivHeader;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPerson = (TextView) itemView.findViewById(R.id.tv_person_header);
            ivHeader = (CircleImageView) itemView.findViewById(R.id.iv_person_header);
        }
    }
}
