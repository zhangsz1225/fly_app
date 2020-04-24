package com.taikesoft.fly.business.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyBaseAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    protected LayoutInflater getLayoutInflater(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    protected List _data = new ArrayList();

    @Override
    public int getCount() {
        return getDataSize();
    }

    public int getDataSize() {
        if (_data != null)
            return _data.size();
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        if (arg0 < 0)
            return null;
        if (_data.size() > arg0) {
            return _data.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public void setData(List data) {
        /*if (_data != null) {
			_data.clear();
			_data = null;
		}*/
        _data = data;
        notifyDataSetChanged();
    }

    public List getData() {
        return _data == null ? (_data = new ArrayList()) : _data;
    }

    public void addData(List data) {
        synchronized (_data) {
            if (_data == null) {
                _data = new ArrayList();
            }
            if (data != null && data.size() > 0) {
                _data.addAll(data);
                notifyDataSetChanged();

            }
        }

    }

    public void setItem(int pos, Object obj) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.set(pos, obj);
        notifyDataSetChanged();
    }

    public void addItem(Object obj) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.add(obj);
        notifyDataSetChanged();
    }

    public void addItem(int pos, Object obj) {
        if (_data == null) {
            _data = new ArrayList();
        }
        _data.add(pos, obj);
        notifyDataSetChanged();
    }

    public void removeItem(Object obj) {
        _data.remove(obj);
        notifyDataSetChanged();
    }

    public void clear() {
        if (_data != null)
            _data.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getRealView(position, convertView, parent);
    }

    protected View getRealView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
