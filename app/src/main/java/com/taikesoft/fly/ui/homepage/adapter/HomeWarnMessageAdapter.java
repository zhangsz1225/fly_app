package com.taikesoft.fly.ui.homepage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.MyBaseAdapter;
import com.taikesoft.fly.ui.myinfo.entity.WarnMsgEntity;

/**
 * 首页推送消息适配器
 */

public class HomeWarnMessageAdapter extends MyBaseAdapter {
    private Context mContext;
    private onIDoListener mOnIDoListener;

    @Override
    protected View getRealView(final int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        ViewHolder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.activity_warnmsg_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (_data != null && _data.size() > 0) {
            WarnMsgEntity mWarnMsgEntity = (WarnMsgEntity) _data.get(position);

            holder.tvWarnDev.setText("消息类型：" + mWarnMsgEntity.getWarnType());
            holder.tvWarnContent.setText("消息内容：" + mWarnMsgEntity.getWarnContent());

            holder.tvWarnTime.setText("发送时间：" + mWarnMsgEntity.getWarnDate());
            holder.llIDo.setTag(R.id.llIDo);
            holder.llIDo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mOnIDoListener.onIDoClick(position);
                }
            });


        }
        return convertView;
    }


    public interface onIDoListener {
        void onIDoClick(int position);
    }

    public void setOnIDoListener(onIDoListener onIDoListener) {
        this.mOnIDoListener = onIDoListener;
    }

    public class ViewHolder {

        TextView tvWarnDev, tvWarnContent, tvWarnTime;
        LinearLayout llIDo;

        public ViewHolder(View itemView) {
            llIDo = (LinearLayout) itemView.findViewById(R.id.llIDo);
            tvWarnDev = (TextView) itemView.findViewById(R.id.tvWarnDev);
            tvWarnContent = (TextView) itemView.findViewById(R.id.tvWarnContent);
            tvWarnTime = (TextView) itemView.findViewById(R.id.tvWarnTime);

        }
    }

}
