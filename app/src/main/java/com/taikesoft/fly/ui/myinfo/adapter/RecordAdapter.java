package com.taikesoft.fly.ui.myinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.common.utils.StringUtils;
import com.taikesoft.fly.ui.myinfo.bean.RecordBean;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 护理记录
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

    private final List<RecordBean> mDatas;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private OnItemClickListener listener;

    private View VIEW_HEADER;
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;

    public RecordAdapter(Context context, List<RecordBean> datas) {
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        int count = mDatas == null ? 0 : mDatas.size();
        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            MyViewHolder viewHolder = new MyViewHolder(VIEW_HEADER);
            return viewHolder;
        } else {
            View view = mInflater.inflate(R.layout.item_nurse_record, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if ((!isHeaderView(position))) {
            RecordBean operate = mDatas.get(position - 1);
            holder.tvName.setText(operate.getChildName());
            holder.tvNurseType.setText(operate.getNurseType());
            if (StringUtils.equals("体温", operate.getNurseItem()) ||
                    StringUtils.equals("更换", operate.getNurseItem())) {
                holder.tvNurseContent.setText(operate.getOperateTime() + operate.getNurseContent());
            }else{
                holder.tvNurseContent.setText(operate.getNurseContent());
            }
            String operatTime = operate.getCreateTime();
            String[] split = operatTime.split(" ");
            String date = "";
            if (!StringUtils.isEmpty(split[0])) {
                date = split[0];
            }
            String time = "";
            if (!StringUtils.isEmpty(split[1])) {
                time = split[1];
            }
            holder.tvTime.setText(mContext.getResources().getString(R.string.operate_time, date, time));
            setOnClickListener(holder, position - 1);
        }
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

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            notifyItemInserted(0);
        }

    }

    private boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvNurseType;
        TextView tvNurseContent;
        TextView tvTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvNurseType = (TextView) itemView.findViewById(R.id.tv_nurse_type);
            tvNurseContent = (TextView) itemView.findViewById(R.id.tv_nurse_content);
            tvTime = (TextView) itemView.findViewById(R.id.tv_operate_time);
        }
    }
}
