package com.taikesoft.fly.ui.myinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.ui.myinfo.bean.NoticeBean;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 *通知公告适配器
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<NoticeBean> mDatas;
    private OnItemClickListener listener;

    public NoticeAdapter(Context context, List<NoticeBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public NoticeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_notice, parent, false);
        NoticeAdapter.MyViewHolder viewHolder = new NoticeAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvTitle.setText(mDatas.get(position).getTitle());
        holder.tvPubOrg.setText(mDatas.get(position).getPubOrg());
        holder.tvPubDate.setText(FromatDate(mDatas.get(position).getPubDate()));
        setOnClickListener(holder, position);

    }

    private String FromatDate(String time) {
        return time.substring(0, 10);
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

        TextView tvTitle, tvPubOrg, tvPubDate;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPubOrg = (TextView) itemView.findViewById(R.id.tvPubOrg);
            tvPubDate = (TextView) itemView.findViewById(R.id.tvPubDate);
        }
    }
}
