//
//  ========================================================================
//  Copyright (c) 1995-2014 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package com.wade.container.server.bio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import com.wade.container.acl.SourceAddressControl;
import com.wade.container.http.HttpException;
import com.wade.container.io.Buffer;
import com.wade.container.io.ConnectedEndPoint;
import com.wade.container.io.Connection;
import com.wade.container.io.EndPoint;
import com.wade.container.io.EofException;
import com.wade.container.io.bio.SocketEndPoint;
import com.wade.container.server.AbstractConnector;
import com.wade.container.server.AbstractHttpConnection;
import com.wade.container.server.BlockingHttpConnection;
import com.wade.container.server.Request;
import com.wade.container.util.component.AggregateLifeCycle;
import com.wade.container.util.log.Log;
import com.wade.container.util.log.Logger;

/**
 * Socket Connector. This connector implements a traditional blocking IO and
 * threading model. Normal JRE sockets are used and a thread is allocated per
 * connection. Buffers are managed so that large buffers are only allocated to
 * active connections.
 * 
 * This Connector should only be used if NIO is not available.
 * 
 * @org.apache.xbean.XBean element="bioConnector"
 *                         description="Creates a BIO based socket connector"
 * 
 * 
 */
public class SocketConnector extends AbstractConnector {

	private static final Logger LOG = Log.getLogger(SocketConnector.class);

	protected ServerSocket _serverSocket;
	protected final Set<EndPoint> _connections;
	protected volatile int _localPort = -1;

	/**
	 * Constructor
	 */
	public SocketConnector() {
		_connections = new HashSet<EndPoint>();
	}

	public Object getConnection() {
		return _serverSocket;
	}

	public void open() throws IOException {
		// Create a new server socket and set to non blocking mode
		if (_serverSocket == null || _serverSocket.isClosed()) {
			_serverSocket = newServerSocket(getHost(), getPort(), getAcceptQueueSize());
		}
		
		_serverSocket.setReuseAddress(getReuseAddress());
		_localPort = _serverSocket.getLocalPort();
		if (_localPort <= 0) {
			throw new IllegalStateException("port not allocated for " + this);
		}

	}

	protected ServerSocket newServerSocket(String host, int port, int backlog) throws IOException {
		ServerSocket ss = host == null ? new ServerSocket(port, backlog) : new ServerSocket(port, backlog, InetAddress.getByName(host));
		return ss;
	}

	public void close() throws IOException {
		if (_serverSocket != null) {
			_serverSocket.close();
		}
		
		_serverSocket = null;
		_localPort = -2;
	}

	@Override
	public void accept(int acceptorID) throws IOException, InterruptedException {
		Socket socket = _serverSocket.accept();

		/** 源物理地址访问控制 */
		if (SourceAddressControl.isPhyCheckEnable()) {

			// 格式: "/127.0.0.1:7572"
			String address = socket.getRemoteSocketAddress().toString();
			String strIp = address.substring(1, address.lastIndexOf(":"));
			
			if (!SourceAddressControl.isPhyPermit(strIp)) {
				LOG.warn("deny illegal access, physical-address:" + strIp);
				socket.close();
				return;
			}
		}

		configure(socket);

		ConnectorEndPoint connection = new ConnectorEndPoint(socket);
		connection.dispatch();
	}

	/**
	 * Allows subclass to override Conection if required.
	 */
	protected Connection newConnection(EndPoint endpoint) {
		return new BlockingHttpConnection(this, endpoint, getServer());
	}

	/*
	 * --------------------------------------------------------------------------
	 * -----
	 */
	@Override
	public void customize(EndPoint endpoint, Request request) throws IOException {
		ConnectorEndPoint connection = (ConnectorEndPoint) endpoint;
		int lrmit = isLowResources() ? _lowResourceMaxIdleTime : _maxIdleTime;
		connection.setMaxIdleTime(lrmit);

		super.customize(endpoint, request);
	}

	public int getLocalPort() {
		return _localPort;
	}

	@Override
	protected void doStart() throws Exception {
		_connections.clear();
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		Set<EndPoint> set = new HashSet<EndPoint>();
		synchronized (_connections) {
			set.addAll(_connections);
		}
		
		for (EndPoint endPoint : set) {
			ConnectorEndPoint connection = (ConnectorEndPoint) endPoint;
			connection.close();
		}
	}

	@Override
	public void dump(Appendable out, String indent) throws IOException {
		super.dump(out, indent);
		Set<EndPoint> connections = new HashSet<EndPoint>();
		synchronized (_connections) {
			connections.addAll(_connections);
		}
		AggregateLifeCycle.dump(out, indent, connections);
	}

	protected class ConnectorEndPoint extends SocketEndPoint implements	Runnable, ConnectedEndPoint {
		volatile Connection _connection;
		protected final Socket _socket;

		public ConnectorEndPoint(Socket socket) throws IOException {
			super(socket, _maxIdleTime);
			_connection = newConnection(this);
			_socket = socket;
		}

		public Connection getConnection() {
			return _connection;
		}

		public void setConnection(Connection connection) {
			if (_connection != connection && _connection != null)
				connectionUpgraded(_connection, connection);
			_connection = connection;
		}

		public void dispatch() throws IOException {
			if (getThreadPool() == null || !getThreadPool().dispatch(this)) {
				LOG.warn("dispatch failed for {}", _connection);
				close();
			}
		}

		@Override
		public int fill(Buffer buffer) throws IOException {
			int l = super.fill(buffer);
			if (l < 0) {
				if (!isInputShutdown())
					shutdownInput();
				if (isOutputShutdown())
					close();
			}
			return l;
		}

		@Override
		public void close() throws IOException {
			if (_connection instanceof AbstractHttpConnection)
				((AbstractHttpConnection) _connection).getRequest().getAsyncContinuation().cancel();
			super.close();
		}

		public void run() {
			try {
				connectionOpened(_connection);
				synchronized (_connections) {
					_connections.add(this);
				}

				while (isStarted() && !isClosed()) {
					if (_connection.isIdle()) {
						if (isLowResources())
							setMaxIdleTime(getLowResourcesMaxIdleTime());
					}

					_connection = _connection.handle();
				}
			} catch (EofException e) {
				LOG.debug("EOF", e);
				try {
					close();
				} catch (IOException e2) {
					LOG.ignore(e2);
				}
			} catch (SocketException e) {
				LOG.debug("EOF", e);
				try {
					close();
				} catch (IOException e2) {
					LOG.ignore(e2);
				}
			} catch (HttpException e) {
				LOG.debug("BAD", e);
				try {
					close();
				} catch (IOException e2) {
					LOG.ignore(e2);
				}
			} catch (Exception e) {
				LOG.warn("handle failed?", e);
				try {
					close();
				} catch (IOException e2) {
					LOG.ignore(e2);
				}
			} finally {
				connectionClosed(_connection);
				synchronized (_connections) {
					_connections.remove(this);
				}

				// wait for client to close, but if not, close ourselves.
				try {
					if (!_socket.isClosed()) {
						long timestamp = System.currentTimeMillis();
						int max_idle = getMaxIdleTime();

						_socket.setSoTimeout(getMaxIdleTime());
						int c = 0;
						do {
							c = _socket.getInputStream().read();
						} while (c >= 0
								&& (System.currentTimeMillis() - timestamp) < max_idle);
						if (!_socket.isClosed())
							_socket.close();
					}
				} catch (IOException e) {
					LOG.ignore(e);
				}
			}
		}
	}
}
