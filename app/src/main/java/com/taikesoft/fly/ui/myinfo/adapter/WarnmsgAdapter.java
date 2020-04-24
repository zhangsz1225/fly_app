package com.taikesoft.fly.ui.myinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.taikesoft.fly.R;
import com.taikesoft.fly.ui.myinfo.entity.WarnMsgEntity;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 */

public class WarnmsgAdapter extends RecyclerView.Adapter<WarnmsgAdapter.MyViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<WarnMsgEntity> mDatas;
    private OnItemClickListener listener;

    public WarnmsgAdapter(Context context, List<WarnMsgEntity> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public WarnmsgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_warn_message, parent, false);
        WarnmsgAdapter.MyViewHolder viewHolder = new WarnmsgAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvWarnType.setText("类型：" + mDatas.get(position).getWarnType());
        holder.tvWarnContent.setText("内容：" + mDatas.get(position).getWarnContent());

        holder.tvWarnTime.setText(FromatDate(mDatas.get(position).getWarnDate()));
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

        TextView tvWarnType, tvWarnContent, tvWarnTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvWarnType = (TextView) itemView.findViewById(R.id.tvWarnType);
            tvWarnContent = (TextView) itemView.findViewById(R.id.tvWarnContent);
            tvWarnTime = (TextView) itemView.findViewById(R.id.tvWarnDate);
        }
    }
}
