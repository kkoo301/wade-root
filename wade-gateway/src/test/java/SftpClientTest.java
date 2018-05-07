import java.io.FileInputStream;
import java.util.List;

import com.ailk.org.apache.commons.io.FileUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.wade.gateway.ftp.server.FtpSite;
import com.wade.gateway.ftp.util.FTPClientFactory;
import com.wade.gateway.ftp.util.IFTPClient;


public class SftpClientTest {
	public static void main(String[] args) throws Exception {
		FtpSite site = new FtpSite();
		site.setIp("192.168.245.128");
		site.setPort(22);
		site.setUsername("steven");
		site.setPassword("123");
		site.setSiteId("ngboss");
		site.setBasePath("/home/steven");
		site.setUseSftp(true);
		
		IFTPClient client = FTPClientFactory.getInstance(site);
		System.out.println("-- start --");
		//client.downloadFile("ALTIBASE.sql", System.out);
		//byte[] rtn = client.downloadFile("ALTIBASE.sql");
		//System.out.println(new String(rtn));
		
		//client.uploadFile(new FileInputStream("C:/Users/Administrator/Desktop/ALTIBASE.sql"), "2/steven.sql");
		//client.deleteFile("/home/steven/2/steven.sql");
		//client.move("steven.sql", "/home/steven/1/zhoulin.sql");
		//boolean b = client.makeDirectory("2");
		//List<String> files = client.listFiles("/home/steven");
		//System.out.println(files);
		
		//client.removeDirectory("1");
	
		
		System.out.println("--  end  --");
	}
}
