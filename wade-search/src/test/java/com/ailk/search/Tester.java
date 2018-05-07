package com.ailk.search;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.ailk.search.server.SearchServer;

public class Tester extends Thread {

	private String searchCode;
	private String[] keywords;

	public Tester(String searchCode, String[] keywords) {
		this.searchCode = searchCode;
		this.keywords = keywords;
	}

	public void run() {
		
		long tid = Thread.currentThread().getId();
		SearchResponse resp = null;
		long start = 0;
		try {
			for (int i = 0; i < 10000; i++) {
				start = System.nanoTime();
				resp = SearchClient.search(searchCode, keywords[i % this.keywords.length], 0, 10);
				System.out.printf("线程编号:%-4d, 总匹配数:%-4d，当前返回数:%-4d", tid, resp.getNumTotalHits(), resp.getDatas().size());
				System.out.println(" 耗时：" + (System.nanoTime() - start) / 1000000 + "毫秒");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void display(IDataset datas) {
		for (Object obj : datas) {
			IData data = (IData) obj;
			System.out.println(data);
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("==============================>>>>>>");
		System.out.println("==============================>>>>>>");
		System.out.println("==============================>>>>>>");
		
		String[] keywords1 = new String[] { "龙山吉首两地", "保底优惠", "互联网专线固定 工料费", "长沙预存 保底", "高校新生 减免", };
		//String[] keywords2 = new String[] { "情侣", "开心MM", "精品软件", "手机阅读", "实况 胜利", };
		String[] keywords2 = new String[] { "AABB"};
		String[] keywords3 = new String[] {"890", "572", "912", "319", "725"};
		
		//new Tester("TEST", keywords1).start();
		//new Tester("TD_B_DISCNT", keywords1).start();
		//new Tester("TD_B_PLATSVC", keywords2).start();
		//new Tester("TD_B_PACKAGE_ELEMENT", keywords3).start();
		
		//SearchResponse resp = SearchClient.search("SALEACTIVE", "全球通升级银卡", 0, 10);
		//System.out.println(resp.getDatas());
		
		//Map<String, String> data = new HashMap<String, String>();
		//data.put("FEATURE_CODE", "ABAB");
		//SearchResponse resp = SearchClient.search("TD_B_PLATSVC", "风云榜", 0, 10);
		
		for (int i = 0; i < 10000; i++) {
			SearchResponse resp = SearchClient.search("TD_B_SYSTEMGUIMENU", "cpbg", 0, 10);	
			System.out.println("搜索结果数:" + resp.getNumTotalHits());
			System.out.println("==============================>>>>>>");
			
			Thread.sleep(1000);
		}
	}


}