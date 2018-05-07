import java.io.File;

import org.apache.commons.io.FileUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ServerInfo;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class Test {
	
	private static TrackerClient trackerClient = null;
	private static TrackerServer trackerServer = null;
	
	private static StorageServer storageServer = null;
	private static StorageClient storageClient = null;
	
	static {
		try {
			ClientGlobal.init("E:/WADE4.0/trunk/framework/dfs/etc/fdfs_client.conf");
			
			trackerClient =  new TrackerClient();
			trackerServer = trackerClient.getConnection();
			storageClient = new StorageClient(trackerServer, storageServer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		upload("C:/Users/Administrator/Desktop/DFS-DIR/MonitorSessionAttributeListener.java");
		//download("group1", "M00/00/00/wKj1g1TH_sqARQjiAAAHGjkscnM06.java", "C:/Users/Administrator/Desktop/DFS-DIR/xx.java");
		//delete("group1", "M00/00/00/wKj1g1TH_sqARQjiAAAHGjkscnM06.java");
	}

	private static void delete(String groupName, String fileName) throws Exception {
		int i = storageClient.delete_file(groupName, fileName);
		System.out.println("delete result code:" + i);
	}
	
	private static void download(String groupName, String fileName, String localFileName) throws Exception {
		FileInfo fileInfo = storageClient.get_file_info(groupName, fileName);
		System.out.println(fileInfo);
		
		byte[] data = storageClient.download_file(groupName, fileName);
		FileUtils.writeByteArrayToFile(new File(localFileName), data);
	}

	private static void upload(String filePath) throws Exception {

		NameValuePair[] metaData = new NameValuePair[3];
		metaData[0] = new NameValuePair("width", "120");
		metaData[1] = new NameValuePair("heigth", "120");
		metaData[2] = new NameValuePair("author", "steven");

		byte[] data = FileUtils.readFileToByteArray(new File(filePath));
		System.out.println("file length: " + data.length);

		String group_name = null;
		StorageServer[] storageServers = trackerClient.getStoreStorages(trackerServer, group_name);
		if (null == storageServers) {
			System.err.println("get store storage servers fail, error code: " + storageClient.getErrorCode());
		} else {
			System.out.println("store storage servers count: " + storageServers.length);
			for (int k = 0; k < storageServers.length; k++) {
				System.out.println(k + 1 + ". " + storageServers[k].getInetSocketAddress().getAddress().getHostAddress() + ":" + storageServers[k].getInetSocketAddress().getPort());
			}
			System.out.println("");
		}

		long startTime = System.currentTimeMillis();
		String[] results = storageClient.upload_file(data, "java", null);
		System.out.println("upload_file time used: " + (System.currentTimeMillis() - startTime) + " ms");

		if (null == results) {
			System.err.println("upload file fail, error code: " + storageClient.getErrorCode());
			return;
		}

		group_name = results[0];
		String remote_filename = results[1];
		System.out.println("group_name: " + group_name + ", remote_filename: " + remote_filename);
		System.out.println("-------------------------- file info -----------------------------");
		System.out.println(storageClient.get_file_info(group_name, remote_filename));

		ServerInfo[] servers = trackerClient.getFetchStorages(trackerServer, group_name, remote_filename);
		if (null == servers) {
			System.err.println("get storage servers fail, error code: " + trackerClient.getErrorCode());
		} else {
			System.out.println("storage servers count: " + servers.length);
			for (int i = 0; i < servers.length; i++) {
				System.out.println(i + ". " + servers[i].getIpAddr() + ":" + servers[i].getPort());
			}
			System.out.println("");
		}
	}
}
