package com.taikesoft.fly.business.base.basebean;

import java.util.ArrayList;

public class BaseListTree<T> extends Base {

    private ArrayList<T> tree;

    public ArrayList<T> getTree() {
        return tree;
    }

    public void setTree(ArrayList<T> tree) {
        this.tree = tree;
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
