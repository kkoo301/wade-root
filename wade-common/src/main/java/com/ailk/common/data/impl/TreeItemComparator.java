package com.ailk.common.data.impl;

import java.io.Serializable;
import java.util.Comparator;

import com.ailk.common.data.impl.TreeItem;

/**
 * Data Comparator
 * 
 * @author Steven Lin Zhou
 */
public class TreeItemComparator implements Serializable,Comparator<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6803508234732917859L;

	public TreeItemComparator() {
		
	}

	public int compare(Object o1, Object o2) {
		
		TreeItem item1 = (TreeItem) o1;
		TreeItem item2 = (TreeItem) o2;
		
		int order1 = item1.getOrder();
		int order2 = item2.getOrder();
		
		boolean hasChild1=item1.isHasChild();
		boolean hasChild2=item2.isHasChild();

		if(hasChild1 && !hasChild2)return -1;
		if(!hasChild1 && hasChild2)return 1;
		
		return order1 == order2 ? 0 : (order1 < order2 ? -1 : 1);
	}
	
}
