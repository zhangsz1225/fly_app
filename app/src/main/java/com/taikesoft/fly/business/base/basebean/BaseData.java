package com.taikesoft.fly.business.base.basebean;

public class BaseData<T> extends Base{

	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
