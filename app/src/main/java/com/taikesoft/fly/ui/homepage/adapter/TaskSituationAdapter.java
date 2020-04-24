package com.taikesoft.fly.ui.homepage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.ui.homepage.entity.TaskSituationEntity;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 今日护理室完成任务情况适配器
 */

public class TaskSituationAdapter extends RecyclerView.Adapter<TaskSituationAdapter.MyViewHolder>{

    private final List<TaskSituationEntity> mDatas;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private OnItemClickListener listener;

    private View VIEW_HEADER;
    private int TYPE_NORMAL = 1000;
    private int TYPE_HEADER = 1001;

    public TaskSituationAdapter(Context context, List<TaskSituationEntity> datas) {
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
            View view = mInflater.inflate(R.layout.item_group_record, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if ((!isHeaderView(position))) {
            TaskSituationEntity situationEntity = mDatas.get(position - 1);
            holder.tvGroupName.setText(situationEntity.getNurseGroup());
            holder.tvSum.setText(String.valueOf(situationEntity.getSum()));
            holder.tvFinishedNum.setText(String.valueOf(situationEntity.getFinishedNum()));
            holder.tvNotFinishedNum.setText(String.valueOf(situationEntity.getNotFinishedNum()));
            holder.tvClosedNum.setText(String.valueOf(situationEntity.getClosedNum()));
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

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvGroupName;
        TextView tvSum;
        TextView tvFinishedNum;
        TextView tvNotFinishedNum;
        TextView tvClosedNum;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.tv_group);
            tvSum = (TextView) itemView.findViewById(R.id.tv_sum);
            tvFinishedNum = (TextView) itemView.findViewById(R.id.tv_finished_num);
            tvNotFinishedNum = (TextView) itemView.findViewById(R.id.tv_not_finished_num);
            tvClosedNum = (TextView) itemView.findViewById(R.id.tv_closed_num);

        }
    }
}
