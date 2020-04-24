package com.taikesoft.fly.ui.child.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taikesoft.fly.R;
import com.taikesoft.fly.business.base.MyBaseAdapter;
import com.taikesoft.fly.business.entity.SelectItemEntity;

import androidx.core.content.ContextCompat;


/**
 * 查询护理记录时，查询的护理项;
 */

public class SelectItemsAdapter extends MyBaseAdapter {

    private final Context context;

    public SelectItemsAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(
                    R.layout.item_select_type, null);
            holder = new ViewHolder(convertView);
        } else {
            holder =  (ViewHolder) convertView.getTag();
        }
        if (_data != null && _data.size() > 0) {
            SelectItemEntity entity = (SelectItemEntity) _data.get(position);
            boolean isSelect = entity.isSelect();
            holder.tvName.setText(entity.getNurseItem() + (entity.getNurseTime() == null ? "" : "," + entity.getNurseTime()));
            if (isSelect) {
                holder.ivSelect.setVisibility(View.VISIBLE);
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.common_blue_color));
            } else {
                holder.ivSelect.setVisibility(View.GONE);
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }
        return convertView;
    }

    public class ViewHolder {

        private final TextView tvName;
        private final ImageView ivSelect;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.textView);
            ivSelect = (ImageView) view.findViewById(R.id.imageViewCheckMark);
            view.setTag(this);
        }
    }
}
