/* Name: FTServer
 * Author: Devon McGrath
 * Description: This class acts as the server for the program.
 * 
 * Version History:
 * 1.0 - 03/25/2017 - Initial version - Devon McGrath
 */

package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class FTServer {
	
	/** The server port this server is created on. */
	public static final int SERVER_PORT = 15421;
	
	/** The string used to upload a file to the server. */
	public static final String UPLOAD = "UPLOAD";
	
	/** The string used to download a file from the server. */
	public static final String DOWNLOAD = "DOWNLOAD";
	
	/** The string used to list the directories (files) on the server from a
	 * specified directory. */
	public static final String LIST_DIRECTORIES = "DIR";
	
	/** The server socket used to accept incoming connections. */
	private ServerSocket serverSocket;
	
	/** The host. */
	private String host;
	
	/** The shared path on the server that the list of files will come from. */
	private String sharedPath;
	
	/**
	 * Constructs a new server with the host and path on the server.
	 * 
	 * @param host - the host.
	 * @param sharedPath - the directory on the server to interact with.
	 */
	public FTServer(String host, String sharedPath) {
		this.host = host;
		this.sharedPath = sharedPath;
	}
	
	/**
	 * <b><em>listen</em></b>
	 * 
	 * <p>The listen method causes the server to listen for new connections
	 * until it is stopped. When a new connection is made, a new thread using a
	 * {@link ClientConnectionHandler} is constructed to handle the connection.
	 * </p>
	 */
	public void listen() {
		
		// Listen for incoming attempts to connect to the server
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		while (!serverSocket.isClosed()) {
			try {
				ClientConnectionHandler conn = new ClientConnectionHandler(
						this, serverSocket.accept());
				conn.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * <b><em>close</em></b>
	 * 
	 * <p>Shuts down the server.</p>
	 * 
	 * @return true if and only if the server listening on the port is stopped.
	 */
	public boolean close() {
		
		// Special case
		if (serverSocket == null) {
			return true;
		}
		
		// Try to close the connection
		boolean err = false;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			err = true;
		}
		
		return !err;
	}
	
	/**
	 * <b><em>listFiles</em></b>
	 * 
	 * <p>Gets a list of files (and only files) in a given directory, which
	 * can be set using {@link #setSharedPath(String)} or in the constructor
	 * {@link #FTServer(String, String)}.</p>
	 * 
	 * @return the list of files in the directory.
	 */
	public String[] listFiles() {

		// Special case
		File path = new File(sharedPath);
		if (!path.exists() || !path.isDirectory()) {
			return new String[0];
		}

		// Get only the files
		File[] dirContents = path.listFiles();
		List<String> fileList = new ArrayList<>();
		for (File file : dirContents) {
			if (file.isFile()) {
				fileList.add(file.getName());
			}
		}
		String[] files = new String[fileList.size()];
		for (int i = 0; i < files.length; i ++) {
			files[i] = fileList.get(i);
		}

		return files;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getSharedPath() {
		return sharedPath;
	}

	public void setSharedPath(String sharedPath) {
		this.sharedPath = sharedPath;
	}
}
