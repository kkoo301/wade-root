import java.util.Date;
import java.util.List;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.wade.gateway.ftp.Constants;
import com.wade.gateway.ftp.client.FTPClient;

public class FTPClientTest implements Constants {
	
	public static void main(String[] args) throws Exception {
		
		String opCode = args[0];
	
		if ("OP_DOWNLOAD_FILE".equals(opCode)) {
			FTPClient.downloadFile(args[1], args[2], args[3]);
		} else if ("OP_UPLOAD_FILE".equals(opCode)) {
			FTPClient.uploadFile(args[1], args[2], args[3]);
		} else if ("OP_REMOVE_FILE".equals(opCode)) {
			FTPClient.deleteFile(args[1], args[2]);
		} else if ("OP_CREATE_DIRECTORY".equals(opCode)) {
			FTPClient.makeDirectory(args[1], args[2]);
		} else if ("OP_REMOVE_DIRECTORY".equals(opCode)) {
			FTPClient.removeDirectory(args[1], args[2]);
		} else if ("OP_LIST_FILES".equals(opCode)) {
			List<String> files = FTPClient.listFiles(args[1], args[2]);
			System.out.println(files);
		} else if ("OP_MOVE_FILE".equals(opCode)) {
			FTPClient.move(args[1], args[2], args[3]);
		} else if ("OP_REMOTE_COPY_FILE".equals(opCode)) {
			FTPClient.remoteCopyFile(args[1], args[2], args[3], args[4]);
		} else {
			throw new IllegalArgumentException("Unsupport ACTION_CODE: " + opCode);
		}
		
		
		//FTPClient.uploadFile("steven", "0/1/2/3/P_CS_CODE_RC.sql", "C:/Users/Administrator/Desktop/P_CS_CODE_RC.sql");
		
		/*
	    System.setProperty("isPrepared", "StartTime:" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		System.out.println(System.getProperty("isPrepared"));
	    
		while (true) {
			IReadOnlyCache cache = CacheFactory.getReadOnlyCache(TestCache.class);
			Object o = cache.get("NOW");
			System.out.println(o);
			Thread.sleep(10000);
		}*/
	}
}
