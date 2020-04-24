package com.taikesoft.fly.business.common.view.children;


import com.taikesoft.fly.business.entity.ChildInfoEntity;

import java.util.Comparator;

public class PinyinComparator implements Comparator<ChildInfoEntity> {

	public int compare(ChildInfoEntity o1, ChildInfoEntity o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
