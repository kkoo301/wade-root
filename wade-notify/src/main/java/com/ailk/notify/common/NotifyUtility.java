package com.ailk.notify.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IDataInput;
import com.ailk.common.data.impl.DataInput;
import com.ailk.service.hessian.io.Hessian2Input;
import com.ailk.service.hessian.io.Hessian2Output;
import com.ailk.service.hessian.io.SerializerFactory;

public class NotifyUtility {
	private static final transient Logger log = Logger.getLogger(NotifyUtility.class);
	
	private static final int QUEUE_NAME_MAX_LENGTH = 10; //主题最大长度
	private static final int SERVER_NAME_MAX_LENGTH = 10; //服务地址名最大长度
	
	private static final int INDEX_FILE_NAME_LENGTH = 8; // 索引文件名最大长度
	private static final int INDEX_OFFSET_LENGTH = 4; // 索引偏移量最大长度
	private static final int MESSAGE_STATUS_LENGTH = 2; // 数据状态长度
	private static final int MESSAGE_START_POSITION_LENGTH = 4; // 数据偏移量最大长度
	private static final int MESSAGE_OFFSET_LENGTH = 4; // 数据偏移量最大长度
	private static final int TIME_LENGTH = 8; // 时间长度
 	private static final int IP_LENGTH = 16; // IP长度
 	private static final int LOG_STATUS = 1; // 发送日志的类型， 0： 生产者；1： 服务端；2：消费者。

 	public static final int INDEX_OFFSET_PREFIX_LENGTH = QUEUE_NAME_MAX_LENGTH + SERVER_NAME_MAX_LENGTH + INDEX_FILE_NAME_LENGTH; // 在返回的消息偏移量中 文件位置偏移量的位置
 	public static final short PRODUCER_REQUEST_SEND = 10; // 标记生产者的请求，用于发送生产的数据
 	public static final short CONSUMER_REQUEST_PULL = 20; // 标记消费者的请求，用于请求获取数据
 	
 	public static final int CACHE_DATA_MAX_SIZE = 100000; // 最大可缓存的数据量，缓存生产者的数据，可供消费者直接调用
 	public static final int TEMP_CACHE_DATA_MAX_SIZE = CACHE_DATA_MAX_SIZE * 100; // 最大可缓存的临时数据量
 	public static final int MOST_REMAIN_PERSIST_SIZE = 1000; // 当持久化的临时数据小于该值时，可强制将数据放入缓存数据中
 
 	public static boolean NEED_BACKUP_MASTER = true; // 是否需要对数据进行备份，Log服务在启动时会修改该值以不备份日志信息
 	
 	public static int CLIENT_SOCKET_BUCKET_SIZE = 10; // 默认客户端针对一个服务ip地址需要在链接桶内保持的链接个数
 	public static final String KEEP_ALIVE_KEY_WORD = "KEEPALIVE"; // 保活发送内容
 	public static final int KEEP_ALIVE_KEY_WORD_LENGTH = KEEP_ALIVE_KEY_WORD.getBytes().length;
 	public static final String KEEP_ALIVE_RESP_KEY_WORD = "OK"; // 保活响应内容
 	public static final int KEEP_ALIVE_RESP_KEY_WORD_LENGTH = KEEP_ALIVE_RESP_KEY_WORD.getBytes().length;
 	public static final int DETECT_MASTER_INTERVAL = 30 * 1000; // 当消息正在向备机发送时，用于检测主机是否可用的时间间隔
 	public static final int DETECT_DEAD_BUCKET_INTERVAL = 30 * 1000; // 校验无效桶是否可用的周期
 	public static final int PRODUCER_SEND_MESSAGE_TIMEOUT = 10 * 60 * 1000;// 生产者生产的消息的超时时间
 	public static final int CONSUMER_MESSAGE_TIMEOUT = 10 * 60 * 1000;// 消费者消费消息的超时时间(ms)
 	public static final int CHECK_CONSUMER_MESSAGE_TIMEOUT_INTERVAL = 2 * 60;// 校验消费者消费超时时间的间隔(s)
 	public static final int CLIENT_INIT_CHANNEL_SIZE = 5; // 客户端初始化的链接数
 	public static final int CONSUMER_CACHE_DATA_SIZE = 100; // 消费者缓存的消息数量
 	
 	public static final int LOG_SEND_THREAD_SIZE = 1;// 发送日志信息的线程数量
 	
 	// Index文件中存储的最大消息块数
 	public static final int INDEX_DATA_MAX_SIZE = 1000000;
 	// 消息偏移量的长度及构成
 	public static final int MESSAGE_OFFSET = QUEUE_NAME_MAX_LENGTH + SERVER_NAME_MAX_LENGTH + INDEX_FILE_NAME_LENGTH + INDEX_OFFSET_LENGTH;

 	// 单个索引块的长度
 	public static final int INDEX_LENGTH = MESSAGE_STATUS_LENGTH + MESSAGE_START_POSITION_LENGTH + MESSAGE_OFFSET_LENGTH;
 	// 生产者发送的日志长度
 	public static final int LOG_PRODUCER_LENGTH = LOG_STATUS + MESSAGE_OFFSET + TIME_LENGTH + IP_LENGTH;
 	public static final int LOG_PERSIST_PRODUCER_LENGTH = LOG_PRODUCER_LENGTH - LOG_STATUS - MESSAGE_OFFSET;
 	// 服务端发送的日志长度
 	public static final int LOG_SERVER_LENGTH = LOG_STATUS + MESSAGE_OFFSET + TIME_LENGTH;
 	public static final int LOG_PERSIST_SERVER_LENGTH = LOG_SERVER_LENGTH - LOG_STATUS - MESSAGE_OFFSET;
 	// 消费者发送的日志长度
 	public static final int LOG_CONSUMER_LENGTH = LOG_STATUS + MESSAGE_OFFSET + TIME_LENGTH + IP_LENGTH;
 	public static final int LOG_PERSIST_CONSUMER_LENGTH = LOG_CONSUMER_LENGTH - LOG_STATUS - MESSAGE_OFFSET;
 	// 客户端发送的单条日志消息的最大长度
 	public static final int MAX_LOG_LENGTH = LOG_PRODUCER_LENGTH;
 	
 	// 客户端发送的消息状态
 	public static final byte LOG_PRODUCER_STATE = 1;
 	public static final byte LOG_SERVER_STATE = 2;
 	public static final byte LOG_CONSUMER_STATE = 3;
 	
	// 单个日志块的长度
 	public static final int LOG_LENGTH = LOG_PERSIST_PRODUCER_LENGTH + LOG_PERSIST_SERVER_LENGTH + LOG_PERSIST_CONSUMER_LENGTH;
 	
	// 标记单个服务端可并发处理的文件数
	public static final int DEFAULT_SERVER_MAX_THREAD_SIZE = 10;
	// 标记识别生产者或消费者的并发线程数
	//public static final int DEFAULT_TRANSFER_SERVER_DATA_THREAD_SIZE = 3;
	
	private static int SERVER_PORT = -1; //获取服务启动时定义的端口号
	private static int HA_SERVER_PORT = -1; //获取服务启动时用于HA通信的端口
	public static int FILE_DEAL_TIME_OUT = 1; //数据放入文件代理中进行处理的最大执行时间，以s为单位
	
	public static final String STREAM_FILE_SUFFIX = ".bytes"; // 顺序写文件的后缀名
	public static final String STREAM_POSITION_FILE_SUFFIX = ".bytespos"; // 记录已经放入索引的顺序文件的位置
	public static final String INDEX_FILE_SUFFIX = ".index"; // 索引文件名
	public static final String MSG_FILE_SUFFIX = ".data"; // 数据文件名
	public static final String HISTORY_DIR = "history"; // 数据历史存放目录
	
	public static final int EVENTLOOP_HA_SERVER_BOSS_SIZE = 5;
	public static final int EVENTLOOP_HA_SERVER_WORKER_SIZE = 10;
	public static final int EVENTLOOP_HA_CLIENT_SIZE = 5;
	public static final int HA_CLIENT_INIT_CHANNEL_SIZE = 3; // HA客户端初始化的链接数
	public static final int HA_CLIENT_SLEEP_TIME = 6 * 60 * 1000; // 当HA客户端到服务端的链接断掉后，需隔对应的时间后再进行线程调度 ；看参考DETECT_MASTER_INTERVAL的大小设置  。
	public static final int HA_UPDATE_DATA_LENGTH =  2 + QUEUE_NAME_MAX_LENGTH + SERVER_NAME_MAX_LENGTH + 
														INDEX_FILE_NAME_LENGTH + INDEX_OFFSET_LENGTH + MESSAGE_STATUS_LENGTH;
	public static final int HA_WIRTE_DATA_LENGHT = HA_UPDATE_DATA_LENGTH + MESSAGE_OFFSET_LENGTH ;
	
	public static final int EVENTLOOP_SERVER_BOSS_SIZE = 30;
	public static final int EVENTLOOP_SERVER_WORKER_SIZE = 100;
	public static final int EVENTLOOP_RRODUCER_CLIENT_SIZE = 5;
	public static final int EVENTLOOP_CONSUMER_CLIENT_SIZE = 5;
	
	public static final int SERVER_ALL_IDLE_TIME = 180;
	public static final int SERVER_READER_IDLE_TIME = SERVER_ALL_IDLE_TIME * 2 + 1;
	public static final int SERVER_WRITER_IDLE_TIME = 0;
			
	public static final int CLIENT_ALL_IDLE_TIME = 10; 
	public static final int CLIENT_READER_IDLE_TIME = 13; // 值不能超过 PRODUCER_SEND_SYNC_TIMEOUT, 且与CLIENT_ALL_IDLE_TIME不能相差过大
	public static final int CLIENT_WRITE_IDLE_TIME = 0;
	
	public static final long NONFILE_FILE_NAME = -1; // 不存在的文件名，用于HA同步数据时
	
	public static final String SERVER_CANNOT_RECEIVE_DATA = "STOP_RECEIVE";// 标记服务端暂时不能接受写入数据
	public static final int SERVER_CANNOT_RECEIVE_DATA_LENGTH = SERVER_CANNOT_RECEIVE_DATA.getBytes().length;
	public static final int PRODUCER_SEND_SYNC_TIMEOUT = 30; // 同步发送消息5s内超时 
	public static final int PRODUCER_WAIT_CONNECT_MASTER_TIMEOUT = 2000;// 生产者在后台Master主机启动后，若链接的不是Master，则等待指定ms后，继续链接
	
	private static SerializerFactory inputFactory = new SerializerFactory(IDataInput.class.getClassLoader());
	
	static {
		// 初始化hessian
		IDataInput input = new DataInput();
		input.getHead().put("prepareHeadKey", "prepareHeadValue");
		input.getData().put("prepareDataKey", "prepareDataValue");
		try {
			byte[] encodeInputBytes = encodeHessian(input);
			decodeHessian(encodeInputBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static enum ASK_FILE_STATE {
		FILE_IN_SLAVE_NOT_IN_ASK(-100), // 被请求的服务中多出的不再请求列表中的文件
		FILE_NOT_IN_SALVE_ACTIVE(-200), // 请求中的文件不在被请求的服务的活跃文件中
		FILE_IS_IN_HISTORY(-300); // 请求中的文件在历史文件目录中
		
		private int state;
		ASK_FILE_STATE(int state) {
			this.state = state;
		}
		
		public int getState() {
			return this.state;
		}
	}
	
	// HA同步数据时标记消息的状态
	public static enum HA_STATE {
		ASK((short)1), ANSWER((short)2), CAN_CACHE_PERSIST((short)3), WRITE_MESSAGE((short)4), UPDATE_STATE((short)5);
		
		private short state; 
		
		HA_STATE(short state) {
			this.state = state;
		}
		
		public short getState() {
			return this.state;
		}
	}
	
	// 标记当前服务类型（用于区分日志服务和消息处理服务）
	private static SERVER_TYPE CUR_SERVER_TYPE = SERVER_TYPE.SERVER;
	
	public static enum SERVER_TYPE {
		LOG(1), SERVER(2);
		
		private int type;
		SERVER_TYPE(int type) {
			this.type = type;
		}
		
		public int getType() {
			return this.type;
		}
	}
	
	/**
	 * 标记客户端的类型
	 */
	public static enum CLIENT_TYPE {
		PRODUCER((short)10), CONSUMER((short)20);
		
		private short type;
		CLIENT_TYPE(short type) {
			this.type = type;
		}
		
		public short getType() {
			return this.type;
		}
	}
	/*
	public static ByteBuffer buildBufferFromRemain(ByteBuffer data) {
		int pos = data.position();
		int msgLength = data.limit() - pos;
		byte[] msgBytes = new byte[msgLength];
		data.get(msgBytes);
		data.position(pos);
		ByteBuffer dupBuffer = ByteBuffer.allocate(msgLength);
		dupBuffer.put(msgBytes);
		dupBuffer.flip();
		ByteBuffer dupBuffer = ByteBuffer.allocate(data.limit() - data.position());
		data.mark();
		data.put(dupBuffer);
		data.reset();
		dupBuffer.flip();
		return dupBuffer;
	}*/
	
	// 消费者发送到服务端的消息内容对应的含义
	public static enum CONSUMER_MESSAGE_TYPE {
		RETRIVE_MESSAGE((short)1), BEGIN_CONSUMER((short)2); // 从服务端请求数据
		private short type;
		
		CONSUMER_MESSAGE_TYPE(short type) {
			this.type = type;
		}
		
		public short getType() {
			return this.type;
		}
	}
	
	// 值要大于0, 小于等于0的用于判断是否为正常数据
	public static enum MESSSAGE_STATE {
		LOG_DATA((short)99),
		//BACKUP_FAILED((short)1), // 备份失败的数据，可异步继续判断是否要备份（根据备机情况决定），但是不影响信息的消费
		SUCCESS_RECEIVED((short)2),
		RETURN_PRODUCER_SUCESS((short)3),  
		RETURN_PRODUCER_FAILED((short)4), // 暂不考虑 返回生产者 成功 或 失败的情况, 重复数据统一由 服务自行控制
		BEGIN_CONSUMER((short)5),  
		CONSUMER_TIMEOUT((short)6), // 暂时无效
		PERSIST_MESSAGE_FAILED((short)7), // 消息持久化失败
		PERSIST_INDEX_FAILED((short)8); // 索引持久化失败
		
		private short state;
		
		MESSSAGE_STATE(short state) {
			this.state = state;
		}
		
		public short getState() {
			return this.state;
		}
		
		/**
		 * 返回生产者失败， 已发送消费者，发送超时都标记为处理结束 
		 * @param state
		 * @return
		 */
		public static boolean isOverState(short state) {
			return state == 4 || state == 5 || state == 6 || state == 7 || state == 8;
		}
		
		public static boolean hasState(short state) {
			return state == 1 || state == 2 || state == 3 || state == 4 || state == 5 || state == 6 || state == 7 || state == 8 || state == 99;
		}
	}
	
	public static int getMaxQueueNameLength() {
		return QUEUE_NAME_MAX_LENGTH;
	}
	
	public static int getMaxServerNameLength() {
		return SERVER_NAME_MAX_LENGTH;
	}
	
	public static int getMaxIndexFileNameLength() {
		return INDEX_FILE_NAME_LENGTH;
	}
	
	public static int getMaxIndexOffsetLength() {
		return INDEX_OFFSET_LENGTH;
	}
	
	public static int getMaxMessageOffsetLength() {
		return MESSAGE_OFFSET_LENGTH;
	}
	
	public static void setServerType(SERVER_TYPE curType) {
		CUR_SERVER_TYPE = curType;
	}
	
	public static SERVER_TYPE getServerType() {
		return CUR_SERVER_TYPE;
	}
	
	public static int getServerPort() {
		if (SERVER_PORT == -1) {
			String port = System.getProperty("wade.server.port");
			if (StringUtils.isBlank(port)) {
				throw new RuntimeException("The property wade.server.port must be defined when start the server!");
			}
			
			SERVER_PORT = Integer.valueOf(port.trim());
		} 
		return SERVER_PORT;
	}
	
	/**
	 * 当服务启动时，通过该参数判断是否为备机，若有值，则为备机，需在备机启动HA服务供主机调用 
	 * @return
	 */
	public static int getHaPort() {
		if (HA_SERVER_PORT == -1) {
			String port = System.getProperty("wade.server.ha.port");
			if (StringUtils.isNotBlank(port)) {
				HA_SERVER_PORT = Integer.valueOf(port.trim());
			}
		}
		return HA_SERVER_PORT;
	}
	
	public static ByteBuffer getKeepAliveBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(NotifyUtility.KEEP_ALIVE_KEY_WORD_LENGTH);
		buffer.put(NotifyUtility.KEEP_ALIVE_KEY_WORD.getBytes());
		buffer.flip();
		return buffer;
	}
	
	public static ByteBuffer getKeepAliveRespBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(NotifyUtility.KEEP_ALIVE_RESP_KEY_WORD_LENGTH);
		buffer.put(NotifyUtility.KEEP_ALIVE_RESP_KEY_WORD.getBytes());
		buffer.flip();
		return buffer;
	}
	
	/**
	 * 将数据转换为指定长度的byte数组
     *
	 * @param data
	 * @param length
	 * @return
	 */
	public static byte[] getBytesWithSpecifyLength(String data, int length) {
		byte[] result = new byte[length];
		byte[] dataBytes = data.getBytes();
		int size = (dataBytes.length > length) ? length : dataBytes.length;
		for (int i = 0; i < size; i++) {
			result[i] = dataBytes[i];
		}
		return result;
	}
	
	public static byte[] encodeHessian(IDataInput input) throws IOException {
		ByteArrayOutputStream baos = null;
		Hessian2Output out = null;

		baos = new ByteArrayOutputStream();
		out = new Hessian2Output(baos);
        out.setSerializerFactory(inputFactory);
        try {
			out.writeObject(input);
			out.flush();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (baos != null) {
				baos.flush();
				baos.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}
	
	public static IDataInput decodeHessian(byte[] data) throws IOException {
		ByteArrayInputStream bais = null;
		Hessian2Input in = null;

		bais = new ByteArrayInputStream(data);
		in = new Hessian2Input(bais);
		in.setSerializerFactory(inputFactory);
		try {
			return (IDataInput)in.readObject(IDataInput.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (bais != null) {
				bais.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}
	
	public static String getLocalIp() {
		String ip = System.getProperty("wade.server.ip");
		if (StringUtils.isNotBlank(ip)) {
			return ip;
		}
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't get Local host!");
		}
	}
	

	public static void checkDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	public static String buildPath(String first, String second) {
		return first + File.separator + second;
	}
	
	public static String buildKey(Object part1, Object part2) {
		return part1 + "_" + part2;
	}
	
	public static String[] splitKey(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value.split("_");
	}
	
	public static String[] splitHost(String host) {
		if (StringUtils.isBlank(host)) {
			return null;
		}
		return host.split(":");
	}
	
	public static Long[] listFileNames(String path) {
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			String[] files = file.list();
			
			List<Long> fileList = new ArrayList<Long>();
			for (String fileName : files) {
				if (StringUtils.isNotBlank(fileName) && fileName.endsWith(NotifyUtility.INDEX_FILE_SUFFIX)) {
					fileList.add(Long.valueOf(fileName.substring(0, fileName.lastIndexOf(NotifyUtility.INDEX_FILE_SUFFIX))));
				}
			}
			
			Long[] fileNames = new Long[fileList.size()];
			fileNames = fileList.toArray(fileNames);
			if (fileNames != null && fileNames.length > 0) {
				Arrays.sort(fileNames, new Comparator<Long>() {
					
					public int compare(Long o1, Long o2) {
						return ((Long)o1 - (Long)o2) > 0 ? 1 : -1;
					}
				});
			}
			if (log.isInfoEnabled()) {
				log.info("List file Names from directory sorted, path :" + path + " ; size :" + fileNames.length);
			}
			
			return fileNames;
		}
		return null;
	}
	
	public static byte[] transferStrToByteArray(String data, int byteLength) {
		byte[] byteArray = new byte[byteLength];
		byte[] strBytes = data.getBytes();
		int length = strBytes.length;
		if (length > byteLength) {
			length = byteLength;
		}
		for (int i = 0; i < length; i++) {
			byteArray[i] = strBytes[i];
		}
		return byteArray;
	}
	
	public static String transferByteArrayToStr(byte[] datas) {
		return new String(datas).trim();
	}
	
	public static ByteBuffer buildMessageOffset(String queueName, String serverName, long indexFileName, int indexOffset) {
		ByteBuffer messageOffset = ByteBuffer.allocate(MESSAGE_OFFSET);
		messageOffset.put(NotifyUtility.getBytesWithSpecifyLength(queueName, getMaxQueueNameLength()));
		messageOffset.put(NotifyUtility.getBytesWithSpecifyLength(serverName, getMaxServerNameLength()));
		
		messageOffset.putLong(indexFileName);
		messageOffset.putInt(indexOffset);
		messageOffset.rewind();
		return messageOffset;
	}
	
	public static String analyMessageOffset(ByteBuffer offset) {
		byte[] topicNameBytes = new byte[QUEUE_NAME_MAX_LENGTH];
		offset.get(topicNameBytes);
		String queueName = new String(topicNameBytes);
		byte[] serverNameBytes = new byte[SERVER_NAME_MAX_LENGTH];
		offset.get(serverNameBytes);
		String serverName = new String(serverNameBytes);
		
		long fileName = offset.getLong();
		int indexOffset = offset.getInt();
		
		String queueAndServer = NotifyUtility.buildKey(queueName, serverName);
		String fileNameAndIndex = NotifyUtility.buildKey(fileName, indexOffset);
		return NotifyUtility.buildKey(queueAndServer, fileNameAndIndex);
	}
	
	/**
	 * 合并oldBuffer中的剩余数据 和 newBuffer中的数据到一个新的buffer中   
	 * @param oldBuffer
	 * @param newBuffer
	 * @param bufferBack
	 * @return
	 */
	public static ByteBuffer dealBuffer(ByteBuffer oldBuffer, ByteBuffer newBuffer, ByteBuffer bufferBack) {
		if (oldBuffer != null && oldBuffer.hasRemaining()) {
			bufferBack.clear();
			try {
				bufferBack.put(oldBuffer);
				bufferBack.put(newBuffer);
				bufferBack.flip();
				releaseByteBuffer(oldBuffer);
				releaseByteBuffer(newBuffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ByteBuffer resultBuffer = ByteBuffer.allocate(bufferBack.limit());
			resultBuffer.put(bufferBack);
			resultBuffer.flip();
			return resultBuffer;
		} else {
			if (oldBuffer != null) {
				oldBuffer.clear();
				releaseByteBuffer(oldBuffer);
			}
			return newBuffer;
		}
	}

	@SuppressWarnings("static-access")
	public static void closeChannel(SocketChannel channel) {
		try {
			Thread.currentThread().sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (channel != null && !channel.socket().isClosed()) {
			try {
				channel.socket().close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 释放ByteBuffer的内存占用 
	 * @param buffer
	 */
    public static void releaseByteBuffer(ByteBuffer buffer) {
		if (buffer == null || !buffer.isDirect() || buffer.capacity() == 0)
            return;
        try {
        	invoke(invoke(viewed(buffer), "cleaner"), "clean");
        } catch (Throwable e) {
        	e.printStackTrace();
        }
	}

	private static ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";

        // JDK7中将DirectByteBuffer类中的viewedBuffer方法换成了attachment方法
        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null)
            return buffer;
        else
            return viewed(viewedBuffer);
    }

	private static Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                }
                catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    private static Method method(Object target, String methodName, Class<?>[] args)
            throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        }
        catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }
    
	public static void main(String[] args) {
		String a = "123456";
		byte[] b = transferStrToByteArray(a, 10);
		System.out.println(transferByteArrayToStr(b));
		
		System.out.println(getLocalIp());
	}
}
