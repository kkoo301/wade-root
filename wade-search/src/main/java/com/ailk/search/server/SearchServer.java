package com.ailk.search.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.ailk.org.apache.commons.lang3.SerializationUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import com.wade.container.server.Server;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.rpc.org.jboss.netty.bootstrap.ServerBootstrap;
import com.ailk.rpc.org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import com.ailk.rpc.server.pipeline.ServerPipelineFactory;
import com.ailk.search.SearchMonitorResponse;
import com.ailk.search.StaticFactory;
import com.ailk.search.SearchResponse;
import com.ailk.search.server.index.SearchConfig;
import com.ailk.search.util.ConfigUtil;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: SearchImpl
 * @description: 服务端搜索实现
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2013-5-16
 */
public class SearchServer {
		
	private static final Logger log = Logger.getLogger(SearchServer.class);
	
	private static final Map<String, SearchConfig> configs = new HashMap<String, SearchConfig>();
	private static final Map<String, IndexReader> indexReaders = new HashMap<String, IndexReader>();
	private static final Map<String, IndexSearcher> indexSearchers = new HashMap<String, IndexSearcher>();
	
	private static final int PARSER_QUEUE_SIZE = 100;
	
	// QueryParser是有状态的，不能多线程共享
	private static final LinkedBlockingQueue<QueryParser> parserQueue = new LinkedBlockingQueue<QueryParser>(PARSER_QUEUE_SIZE);
	
	/**
	 * 启动时间
	 */
	private static String bootTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 搜索统计信息
	 */
	private static Map<String, SearchRecorder> searchStatistics = new HashMap<String, SearchRecorder>();
	
	/**
	 * 处理监控请求
	 * 
	 * @return
	 */
	public static SearchMonitorResponse monitor() {
		SearchMonitorResponse response = new SearchMonitorResponse();
		response.setBootTime(bootTime);
		response.setSearchStatistics(searchStatistics);
		return response;
	}
	
	/**
	 * 搜索函数
	 * 
	 * @param searchCode 搜索编码
	 * @param keyword 关键词（需分词处理）
	 * @param start
	 * @param end
	 * @return
	 * @throws Exception
	 */
	public static SearchResponse search(String searchCode, String keyword, Integer start, Integer end) throws Exception {
				
		long startTime = System.currentTimeMillis();
		
		if (StringUtils.isBlank(keyword)) {
			throw new IllegalArgumentException("搜索关键字不可为空！");
		}
		
		SearchResponse rtn = new SearchResponse();
		
		IDataset datas = new DatasetList();
		
		SearchConfig config = configs.get(searchCode);
		if (null == config) {
			String errInfo = "根据搜索编码未找到对应的搜索配置。searchCode=" + searchCode;
			log.error(errInfo, new NullPointerException());
			rtn.setErrInfo(errInfo);
			return rtn;
		}
		
		int limitHits = config.getLimitHits(); // 最大结果集限制
		
		if (null == indexReaders.get(searchCode)) {
			// 索引数据目录
			String indexDataPath = config.getIndexDataPath();
			FSDirectory fsDirectory = FSDirectory.open(new File(indexDataPath));
			//RAMDirectory ramDirectory = new RAMDirectory(fsDirectory); // 用RAMDirectory，比直接用FSDirectory快30%！
			IndexReader reader = IndexReader.open(fsDirectory);
			IndexSearcher searcher = new IndexSearcher(reader);
			
			indexReaders.put(searchCode, reader);
			indexSearchers.put(searchCode, searcher);
		}
		
		QueryParser parser = null;
		try {
			
			parser = parserQueue.take();
			keyword = parser.escape(keyword); // Lucene保留字符转换
			Query query = parser.parse(keyword);
		
			IndexSearcher searcher = indexSearchers.get(searchCode);
			TopDocs results = searcher.search(query, limitHits); // 平均耗时: 400微秒
			ScoreDoc[] hits = results.scoreDocs;
			
			int numTotalHits = results.totalHits; // 总共命中数
			rtn.setNumTotalHits(numTotalHits);
			
			end = Math.min(hits.length, end);
			for (int i = start; i < end; i++) {
				Document doc = searcher.doc(hits[i].doc);
				byte[] bytes = doc.getBinaryValue("DATA");
				
				IData data = (IData) SerializationUtils.deserialize(bytes);
				datas.add(data);
			}
	    
		} finally {
			if (null != parser) {
				parserQueue.add(parser);
			}
		}
		
		rtn.setDatas(datas);
		
		// 递增搜索统计值
		searchStatistics.get(searchCode).getCnt().getAndIncrement();
		searchStatistics.get(searchCode).getTtc().getAndAdd(System.currentTimeMillis() - startTime);
		
		return rtn;
	}

	public static void main(String[] args) throws Exception {
		
		if (6 != args.length) {
			System.err.println("Usage: java " + SearchServer.class.getName() + " --searchPort searchPort --httpPort httpPort --clusterName clusterName ");
			System.exit(255);
		}
		
		int searchPort = Integer.parseInt(args[1]);		
		if (searchPort < 1024 || searchPort > 0xFFFF) {
			throw new IllegalArgumentException("搜索端口值不合法，必须介于 1025 -> 65535 之间！");
		}
		
		int httpPort = Integer.parseInt(args[3]);
		if (httpPort < 1024 || httpPort > 0xFFFF) {
			throw new IllegalArgumentException("HTTP端口值不合法，必须介于 1025 -> 65535 之间！");
		}
		
		init(args[5]);
		
		ServerBootstrap bootstrap = new ServerBootstrap (
			new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), 
				Executors.newCachedThreadPool()
			)
		);

		warmup(); // 预热
		
		bootstrap.setPipelineFactory(new ServerPipelineFactory());
		bootstrap.bind(new InetSocketAddress("0.0.0.0", searchPort));
		startupHttpServer(httpPort);
		
		Map<String, String> env = System.getenv();
		System.out.println(StringUtils.rightPad("环境变量:", StaticFactory.LEN, '-'));
		for (String key : env.keySet()) {
			System.out.printf("%s: %s\n", key, env.get(key));
		}
		
		System.out.println(StringUtils.rightPad("", StaticFactory.LEN, '-'));
		printLogo();
		System.out.println(StringUtils.rightPad("", StaticFactory.LEN, '-'));
		System.out.println("搜索服务器启动成功！");
		System.out.printf("搜索端口：%d\n", searchPort);
		System.out.printf("监控端口: %d\n", httpPort);
		System.out.printf("启动时间：%s\n", bootTime);
		System.out.println(StringUtils.rightPad("", StaticFactory.LEN, '-'));
	}
	
	/**
	 * 启动HTTP服务
	 * @throws Exception 
	 */
	private static void startupHttpServer(int httpPort) throws Exception {
		Server server = new Server(httpPort);
		server.setHandler(new SearchServerMonitorHandler());
		server.start();
		server.join();
	}
	
	/**
	 * 预热
	 */
	private static void warmup () {
		for (String searchCode : configs.keySet()) {
			long start = System.currentTimeMillis();
			try {
				search(searchCode, "WADE", 0, 10);
				System.out.printf("预热: %-50s SUCCESS!, 耗时:%-10d ms.\n", searchCode, (System.currentTimeMillis() - start));
			} catch (Exception e) {
				System.out.printf("预热: %-50s FAILURE!, 耗时:%-10d ms.\n", searchCode, (System.currentTimeMillis() - start));
				e.printStackTrace();
			}
		}
	}
	
	private static final void printLogo() {
		System.out.println("0000011    00    100001     00         00000000001     1000000100000 ");  
		System.out.println(" 00001    1000     00      0000          0001   0000     1000     00 ");  
		System.out.println("  0000    00001    0      000001         0001    10001   1000      1 ");
		System.out.println("   000   011000   00     10 1000         0001     0000   1000   00   ");  
		System.out.println("   0000  0  0001  0      0   0000        0001     0000   100011000   ");  
		System.out.println("    000101   000 01     0011110001       0001     0000   1000   10   ");
		System.out.println("    10000    00000     10     1000       0001     0001   1000      10"); 
		System.out.println("     000      000     100      00001     0000   1000     00001    000"); 
		System.out.println("     101      101    00000   11111001  11011001011     11011101010001");
	}
	
	/**
	 * 初始化
	 * 
	 * @param clusterName
	 */
	private static final void init(String clusterName) {
		List<String> searchCodes = ConfigUtil.getSearchCodeByClusterName(clusterName);
		for (String searchCode : searchCodes) {
			SearchConfig config = ConfigUtil.getConfigure(searchCode);
			configs.put(searchCode, config);
			searchStatistics.put(searchCode, new SearchRecorder());
		}
		
		for (int i = 0; i < PARSER_QUEUE_SIZE; i++) {
			WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_36);
			QueryParser parser = new QueryParser(Version.LUCENE_36, StaticFactory.INDEX_FIELD_NAME, analyzer);
			parser.setAllowLeadingWildcard(true);	
			parser.setDefaultOperator(QueryParser.AND_OPERATOR);
			parserQueue.add(parser);
		}
	}

}
