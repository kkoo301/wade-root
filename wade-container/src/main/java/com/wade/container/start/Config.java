package com.wade.container.start;

public class Config {
	
	private static final int DEFAULT_PORT = 7002;
	private static final String DEFAULT_CONTEXT_PATH = "/";
	private static final int DEFAULT_MIN_THREADS = 10;
	private static final int DEFAULT_MAX_THREADS = 100;
	private static final int DEFAULT_ACCEPTORS = 1;
	private static final int DEFAULT_ACCEPT_QUEUESIZE = 50;
	private static final int DEFAULT_MAX_IDLETIME = 300000;
	private static final int DEFAULT_LOW_RESOURCES_MAX_IDLETIME = 5000;

	private static final boolean DEFAULT_SSL = false;
	private static final boolean DEFAULT_NIO = false;
	private static final boolean DEFAULT_AJP = false;
	private static final boolean DEFAULT_FORWARDED = false;
	
	public static final int PORT;

	public static final String RESOURCE_BASE;
	public static final String CONTEXT_PATH;

	public static final String DESCRIPTOR;
	public static final String DEFAULTS_DESCRIPTOR;

	public static final int MIN_THREADS;
	public static final int MAX_THREADS;
	public static final int ACCEPTORS;
	public static final int ACCEPT_QUEUESIZE;
	public static final int MAX_IDLETIME;
	public static final int LOW_RESOURCES_MAX_IDLETIME;

	public static final boolean SSL;
	public static final boolean NIO;
	public static final boolean AJP;
	public static final boolean FORWARDED;
	
	public static final String SSL_KEYSTORE_PATH;
	public static final String SSL_KEYSTORE_PASSWD;
	public static final String SSL_KEYMANAGER_PASSWD;
	
	public static final String SSL_TRUSTSTORE_PATH;
	public static final String SSL_TRUSTSTORE_PASSWD;
	
	public static final String FORWARDED_CIPHER_SUITE_HEADER; 
	public static final String FORWARDED_FOR_HEADER;
	public static final String FORWARDED_HOST_HEADER;
	public static final String FORWARDED_PROTO_HEADER;
	public static final String FORWARDED_SERVER_HEADER;
	public static final String FORWARDED_SSL_SESSION_ID;
	
	static {
		
		String _port = System.getProperty("wade.container.port");
		String _resourceBase = System.getProperty("wade.container.resourceBase");
		String _contextPath = System.getProperty("wade.container.contextPath");
		String _minThreads = System.getProperty("wade.container.minThreads");
		String _maxThreads = System.getProperty("wade.container.maxThreads");
		String _acceptors = System.getProperty("wade.container.acceptors");
		String _acceptQueueSize = System.getProperty("wade.container.acceptQueueSize");
		String _maxIdleTime = System.getProperty("wade.container.maxIdleTime");
		String _lowResourcesMaxIdleTime = System.getProperty("wade.container.lowResourcesMaxIdleTime");
		
		String _ssl = System.getProperty("wade.container.ssl");
		String _nio = System.getProperty("wade.container.nio");
		String _ajp = System.getProperty("wade.container.ajp");
		String _forwarded = System.getProperty("wade.container.forwarded");
		
		String _ssl_KeyStore_Path = System.getProperty("wade.container.sslKeyStorePath");
		String _ssl_KeyStore_Password = System.getProperty("wade.container.sslKeyStorePassword");
		String _ssl_KeyManager_Password = System.getProperty("wade.container.sslKeyManagerPassword");
		
		String _ssl_trustStore_Path = System.getProperty("wade.container.sslTrustStorePath");
		String _ssl_trustStore_Password = System.getProperty("wade.container.sslTrustStorePassword");

		String _descriptor = System.getProperty("wade.container.descriptor");
		String _defaultsDescriptor = System.getProperty("wade.container.defaultsDescriptor");
		
		String _forwardedCipherSuiteHeader = System.getProperty("wade.container.forwardedCipherSuiteHeader");
		String _forwardedForHeader         = System.getProperty("wade.container.forwardedForHeader");
		String _forwardedHostHeader        = System.getProperty("wade.container.forwardedHostHeader");
		String _forwardedProtoHeader       = System.getProperty("wade.container.forwardedProtoHeader");
		String _forwardedServerHeader      = System.getProperty("wade.container.forwardedServerHeader");
		String _forwardedSslSessionId      = System.getProperty("wade.container.forwardedSslSessionId");
		
		PORT = _port != null && !"".equals(_port) ? Integer.parseInt(_port)	: DEFAULT_PORT;

		RESOURCE_BASE = _resourceBase;
		CONTEXT_PATH = _contextPath != null && !"".equals(_contextPath) ? _contextPath : DEFAULT_CONTEXT_PATH;

		DESCRIPTOR = _descriptor;
		DEFAULTS_DESCRIPTOR = _defaultsDescriptor;

		MIN_THREADS = _minThreads != null && !"".equals(_minThreads) ? Integer.parseInt(_minThreads) : DEFAULT_MIN_THREADS;
		MAX_THREADS = _maxThreads != null && !"".equals(_maxThreads) ? Integer.parseInt(_maxThreads) : DEFAULT_MAX_THREADS;
		ACCEPTORS = _acceptors != null && !"".equals(_acceptors) ? Integer.parseInt(_acceptors) : DEFAULT_ACCEPTORS;
		ACCEPT_QUEUESIZE = _acceptQueueSize != null	&& !"".equals(_acceptQueueSize) ? Integer.parseInt(_acceptQueueSize) : DEFAULT_ACCEPT_QUEUESIZE;
		MAX_IDLETIME = _maxIdleTime != null && !"".equals(_maxIdleTime) ? Integer.parseInt(_maxIdleTime) : DEFAULT_MAX_IDLETIME;
		LOW_RESOURCES_MAX_IDLETIME = _lowResourcesMaxIdleTime != null && !"".equals(_lowResourcesMaxIdleTime) ? Integer.parseInt(_lowResourcesMaxIdleTime) : DEFAULT_LOW_RESOURCES_MAX_IDLETIME;

		SSL = _ssl != null ? ("true".equals(_ssl) ? true : false) : DEFAULT_SSL;
		NIO = _nio != null ? ("true".equals(_nio) ? true : false) : DEFAULT_NIO;
		AJP = _ajp != null ? ("true".equals(_ajp)) ? true : false : DEFAULT_AJP;
		FORWARDED = _forwarded != null ? ("true".equals(_forwarded)) ? true : false : DEFAULT_FORWARDED ;
		
		SSL_KEYSTORE_PATH = _ssl_KeyStore_Path;
		SSL_KEYSTORE_PASSWD = _ssl_KeyStore_Password;
		SSL_KEYMANAGER_PASSWD = _ssl_KeyManager_Password;
		
		SSL_TRUSTSTORE_PATH = _ssl_trustStore_Path;
		SSL_TRUSTSTORE_PASSWD = _ssl_trustStore_Password;
		
		FORWARDED_CIPHER_SUITE_HEADER = _forwardedCipherSuiteHeader;
		FORWARDED_FOR_HEADER = _forwardedForHeader;
		FORWARDED_HOST_HEADER = _forwardedHostHeader;
		FORWARDED_PROTO_HEADER = _forwardedProtoHeader;
		FORWARDED_SERVER_HEADER = _forwardedServerHeader;
		FORWARDED_SSL_SESSION_ID = _forwardedSslSessionId;
	}
}