package com.taikesoft.fly.ui.nurse.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.business.common.view.NumberProgressBar;
import com.taikesoft.fly.business.context.AppContext;
import com.taikesoft.fly.ui.myinfo.bean.NurseItemBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 护理主页适配器
 */

public class NurseItemAdapter extends RecyclerView.Adapter<NurseItemAdapter.MyViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<NurseItemBean> mDatas;
    private OnItemClickListener listener;
    private String quickText;
    private static final int MSG_CIRCLE = 0x1001;

    public NurseItemAdapter(Context context, List<NurseItemBean> datas, String quickText) {
        this.mContext = context;
        this.mDatas = datas;
        this.quickText = quickText;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public NurseItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_nurse, parent, false);
        NurseItemAdapter.MyViewHolder viewHolder = new NurseItemAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvNurseItem.setText(mDatas.get(position).getNurseItem());
        holder.tvNurseTime.setText(mDatas.get(position).getNurseTime());
        holder.tvNurseType.setText(mDatas.get(position).getNurseType());
        String sta = AppContext.mResource.getString(R.string.nurse_item_sisution, String.valueOf(mDatas.get(position).getCnt()), String.valueOf(mDatas.get(position).getFinishCnt()), String.valueOf(mDatas.get(position).getClosesCnt()));
        holder.tvFinishSta.setText(generateSpannableText(sta));
        if (mDatas.get(position).getCnt() > 0) {
            holder.mNumberProgressBar.setVisibility(View.VISIBLE);
            holder.mNumberProgressBar.setProgress(mDatas.get(position).getFinishCnt() / mDatas.get(position).getCnt());
        } else {
            holder.mNumberProgressBar.setVisibility(View.GONE);
        }
        mHandler.removeMessages(MSG_CIRCLE);
        mHandler.sendEmptyMessage(MSG_CIRCLE);
        if (quickText != null) {
            if (StringUtils.equals(mDatas.get(position).getNurseType(), quickText) || StringUtils.equals(mDatas.get(position).getNurseItem(), quickText)) {
                holder.itemView.setBackgroundColor(AppContext.mResource.getColor(R.color.antiquewhite));
            } else {
                holder.itemView.setBackgroundColor(AppContext.mResource.getColor(R.color.bg_no_data));
            }
        } else {
            holder.itemView.setBackgroundColor(AppContext.mResource.getColor(R.color.bg_no_data));
        }

        setOnClickListener(holder, position);

    }

    private void setOnClickListener(final MyViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.setOnClick(holder.itemView, position);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void setOnClick(View holder, int position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvNurseItem, tvNurseTime, tvNurseType, tvFinishSta;
        NumberProgressBar mNumberProgressBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvNurseItem = itemView.findViewById(R.id.tvNurseItem);
            tvNurseTime = itemView.findViewById(R.id.tvNurseTime);
            tvNurseType = itemView.findViewById(R.id.tvNurseType);
            tvFinishSta = itemView.findViewById(R.id.tvFinishSta);
            mNumberProgressBar = itemView.findViewById(R.id.progress_number);
        }
    }

    private SpannableString generateSpannableText(String text) {
        SpannableString s = new SpannableString(text);
        int end1 = text.indexOf("，已完成");
        int end2 = text.indexOf("，已关闭");
        s.setSpan(new ForegroundColorSpan(AppContext.mResource.getColor(R.color.app_color_blue)), 2, end1, 0);
        s.setSpan(new ForegroundColorSpan(AppContext.mResource.getColor(R.color.colorAccent)), end1 + 4, end2, 0);
        s.setSpan(new ForegroundColorSpan(AppContext.mResource.getColor(R.color.closed_color)), end2 + 4, text.length(), 0);
        return s;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CIRCLE) {
                mHandler.sendEmptyMessageDelayed(MSG_CIRCLE, 100);
            }
        }
    };
}
