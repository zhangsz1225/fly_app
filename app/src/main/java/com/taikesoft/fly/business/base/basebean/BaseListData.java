package com.taikesoft.fly.business.base.basebean;

import java.util.ArrayList;

public class BaseListData<T> extends Base {

	private ArrayList<T> data;

	public ArrayList<T> getData() {
		return data;
	}

	public void setData(ArrayList<T> data) {
		this.data = data;
	}
	
}
