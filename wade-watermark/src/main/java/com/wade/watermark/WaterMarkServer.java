package com.wade.watermark;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @className: WaterMarkServer
 * @description: 
 * 
 * @version: v1.0.0
 * @author: zhoulin2
 * @date: 2015-06-16
 */
public final class WaterMarkServer {

	public static final String WATERMARK_PROPERTIES = "watermark.properties";
	
	private static final Logger LOG = Logger.getLogger(WaterMarkServer.class);
	private static final ExecutorService executor = new ThreadPoolExecutor(32, 32, 200L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(2000));
	
	private static byte[] buf = new byte[2048];
	private static DatagramPacket packet = null;
	
	private static final AffineTransform trans = new AffineTransform();
	private static final Font FONT = new Font("verdana", Font.PLAIN, 10);
	//private static final Color COLOR = new Color(0.5F, 0.5F, 0.5F, 0.5F);//new Color(171, 180, 195);
	private static Color COLOR = null;
	private static String timestamp = "";
	private static String directory = "";
		
	public static void main(String[] args) throws Exception {
		
		Properties prop = new Properties();
		InputStream in = WaterMarkServer.class.getClassLoader().getResourceAsStream(WATERMARK_PROPERTIES);
		prop.load(in);
		in.close();
		
		int colorR = Integer.parseInt(prop.getProperty("color.r", "238"));
		int colorG = Integer.parseInt(prop.getProperty("color.g", "238"));
		int colorB = Integer.parseInt(prop.getProperty("color.b", "238"));
		directory = prop.getProperty("server.image.directory");
		int port = Integer.parseInt(prop.getProperty("server.listen.port"));
		
		COLOR = new Color(colorR, colorG, colorB);
		
		trans.rotate(0.62, 125, 100);

		InetSocketAddress socketAddress = new InetSocketAddress(port);
		DatagramSocket ds = new DatagramSocket(socketAddress);
		LOG.info("watermark server start successful!");
		LOG.info("---------------------------------------------------------");
		LOG.info(" listen port(udp): " + port);
		LOG.info(" directory: " + directory);
		LOG.info(" start time: " + new Date());
		LOG.info("---------------------------------------------------------");

		// 启动滴答线程
		TickJob tickJob = new TickJob();
		executor.execute(tickJob);
		
		// 启动清理过期图片线程
		ClearJob clearJob = new ClearJob();
		executor.execute(clearJob);
		
		while (true) {
			
			packet = new DatagramPacket(buf, buf.length);
			ds.receive(packet);
			String info = new String(packet.getData(), 0, packet.getLength());

			ImageCreator job = new ImageCreator(info);
			executor.execute(job);
			
		}

	}
	
	/**
	 * Copyright: Copyright (c) 2015 Asiainfo
	 * 
	 * @className: WaterMarkServer
	 * @description: 
	 * 
	 * @version: v1.0.0
	 * @author: zhoulin2
	 * @date: 2015-06-16
	 */
	private static class TickJob implements Runnable {

		@Override
		public void run() {
			
			LOG.info("Start TickJob Thread...");
			
			while (true) {
				
				timestamp = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
				
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

	/**
	 * Copyright: Copyright (c) 2015 Asiainfo
	 * 
	 * @className: ClearJob
	 * @description: 清理过期图片
	 * 
	 * @version: v1.0.0
	 * @author: zhoulin2
	 * @date: 2015-06-16
	 */
	private static class ClearJob implements Runnable {

		@Override
		public void run() {
			
			LOG.info("Start ClearJob Thread...");
			
			while (true) {
			
				LOG.info("clear expire images...");
				
				long now = System.currentTimeMillis();
				File dir = new File(directory);
				
				for (File file : dir.listFiles()) {					
					if (file.getName().endsWith(".png")) {
						if ((now - file.lastModified()) > (1000 * 86400)) { // 清理一天以前的
							file.delete();
							LOG.info("clear expire images: " + file.getName());
						}
					}
				}
				
				try {
					Thread.sleep(1000 * 3600); // 一小时运行一次
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * Copyright: Copyright (c) 2015 Asiainfo
	 * 
	 * @className: ImageCreator
	 * @description: 图片创建
	 * 
	 * @version: v1.0.0
	 * @author: zhoulin2
	 * @date: 2015-06-16
	 */
	private static class ImageCreator implements Runnable {

		private String info;
		
		public ImageCreator(String info) {
			this.info = info;
		}
		
		@Override
		public void run() {
			
			try {
				
				long start = System.currentTimeMillis();
				
				String[] part = this.info.split(",");
				if (2 != part.length) {
					return;
				}
				
				String code = part[0];
				String ip = part[1];
					
				BufferedImage image = new BufferedImage(250, 200, Transparency.TRANSLUCENT);
				Graphics2D graphics = (Graphics2D) image.getGraphics();
				graphics.setFont(FONT);
				graphics.setColor(COLOR);
				graphics.setTransform(trans);
				//抗锯齿
				graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				
				graphics.drawString("", 10, 10);
	
				graphics.drawString(ip, 62, 58);
				graphics.drawString(code, 50, 175);
				graphics.drawString(timestamp, 27, 118);
				graphics.dispose();
				
				// 写入磁盘
				FileOutputStream fos = new FileOutputStream(directory + code + ".png");
				ImageIO.write(image, "PNG", fos); // 消耗: 40-50毫秒
				fos.flush();
				fos.close();
				
				long cost = System.currentTimeMillis() - start;
				String info = String.format("%10s %20s costtime: %3d ms", code, ip, cost);
				LOG.info(info);
				
			} catch (Exception e) {
				LOG.error(e);
			}
		}
		
	}
	
}
