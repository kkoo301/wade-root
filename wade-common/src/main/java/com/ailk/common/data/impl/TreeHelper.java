/**   
* Copyright: Copyright (c) 2012 Asiainfo-Linkage
* 
* @ClassName: TreeHelper.java
* @Description: 该类的功能描述
*
* @version: v1.0.0
* @author: Ben
* @date: 2012-10-17 下午05:36:56 
*
* Modification History:
* Date         Author          Version            Description
*---------------------------------------------------------*
* 2012-10-17     Ben           v1.0.0               修改原因
*/

package com.ailk.common.data.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ben
 *
 */
public class TreeHelper {

	public static TreeItem[] getWholeTreeData(TreeBean[] beans) {
		return getTreeDataByParent(beans, "0", null);
	}
	
	private static TreeItem[] getTreeDataByParent(TreeBean[] beans, String parentId, TreeItem parentItem) {
		List<TreeItem> treeList = new ArrayList<TreeItem>();
		TreeBean[] nodes = getTreeNodesByParentId(beans, parentId, parentItem);
		if (nodes != null && nodes.length > 0) {
			TreeItem item = null;
			TreeBean bean = null;
			for (int i = 0, size = nodes.length; i < size; i++) {
				bean = nodes[i];
				item = new TreeItem(bean.getCode(), parentItem, bean.getLabel(), bean.getValue(), bean.getHref(), bean.isShowCheck());
				if (parentItem == null) {
					treeList.add(item);
				}
				getTreeDataByParent(beans, bean.getId(), item);
			}
		}
		return (TreeItem[])treeList.toArray(new TreeItem[0]);
	}
	
	private static TreeBean[] getTreeNodesByParentId(TreeBean[] beans, String parentId, TreeItem parentItem) {
		TreeBean bean = null;
		List<TreeBean> treeList = new ArrayList<TreeBean>();
		
		for (int i = 0, size = beans.length; i < size; i++) {
			if (parentId.equals(beans[i].getParentId())) {
				bean = beans[i];
				treeList.add(bean);
			}
		}
		
		return (TreeBean[])treeList.toArray(new TreeBean[0]);
	}

}
