package com.ailk.common.data.impl;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public final class Anchor {
	public static final int[] mark(IDataset dataset, String fix, int fixType) {
		int[] marks = new int[dataset.size() + 1];
		int idx = 0;

		if (fixType == IDataset.TYPE_STRING) {
			String preValue = null;
			String curValue = null;
			for (int i = 0, size = dataset.size(); i < size; i++) {
				IData data = dataset.getData(i);
				curValue = data.getString(fix);
				if (!curValue.equals(preValue)) {
					marks[idx++] = i;
				}
				preValue = curValue;
			}
		} else if (fixType == IDataset.TYPE_INTEGER) {
			int preValue = Integer.MIN_VALUE;
			int curValue = Integer.MIN_VALUE;
			for (int i = 0, size = dataset.size(); i < size; i++) {
				IData data = dataset.getData(i);
				curValue = data.getInt(fix, 0);
				if (curValue != preValue) {
					marks[idx++] = i;
				}
				preValue = curValue;
			}
		} else if (fixType == IDataset.TYPE_DOUBLE) {
			double preValue = Double.NaN;
			double curValue = Double.NaN;
			for (int i = 0, size = dataset.size(); i < size; i++) {
				IData data = dataset.getData(i);
				curValue = data.getDouble(fix, 0);
				if (curValue != preValue) {
					marks[idx++] = i;
				}
				preValue = curValue;
			}
		}
		marks[idx] = dataset.size();
		return trimRight(marks);
	}

	private static final int[] trimRight(int[] marks) {
		int tail = -1;
		for (int i = marks.length - 1; i >= 0; i--) {
			if (marks[i] != 0) {
				tail = i;
				break;
			}
		}

		int[] ms = new int[tail + 1];

		for (int i = 0, size = ms.length; i < size; i++) {
			ms[i] = marks[i];
		}

		return ms;
	}
}
