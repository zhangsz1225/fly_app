package com.taikesoft.fly.ui.nurse;


import com.taikesoft.fly.ui.nurse.entity.MedicineInfoEntity;

import java.util.Comparator;

public class PinyinMedicineComparator implements Comparator<MedicineInfoEntity> {

	public int compare(MedicineInfoEntity o1, MedicineInfoEntity o2) {
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
