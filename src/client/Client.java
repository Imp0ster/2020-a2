/* Name: Client
 * Author: Devon McGrath
 * Description: This class represents the client for the program and
 * stores all necessary info about the client.
 * 
 * Version History:
 * 1.0 - 03/25/2017 - Initial version - Devon McGrath
 */

package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

	/** The path to the files on the client machine. */
	private String sharedPath;
	
	/** Constructs a client with the path being the working directory. */
	public Client() {
		this((new File("")).getAbsolutePath());
	}
	
	/**
	 * Constructs a client with a specified path on the local machine.
	 * 
	 * @param sharedPath - the path (directory).
	 */
	public Client(String sharedPath) {
		this.sharedPath = sharedPath;
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
	
	/**
	 * <b><em>sendRequest</em></b>
	 * 
	 * <p>Sends a request to the server and returns the server's response.</p>
	 * 
	 * @param command - the command (data) to send to the server.
	 * @param host - the host to connect to.
	 * @param port - the port to connect to.
	 * 
	 * @return the server's response.
	 */
	public String sendRequest(String command, String host, int port) {
		
		// Special case
		if (command == null || command.length() == 0) {
			return "";
		}
		
		String response = "";
		try {
			
			// Create a connection with the server
			Socket socket = new Socket(host, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			// Send the command to the server
			out.println(command);
			out.flush();
			
			// Receive the response
			String line = null;
			BufferedReader br = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while ((line = br.readLine()) != null) {
				response += line + "\n";
			}
			if (response.length() > 0) {
				response = response.substring(0, response.length()-1);
			}
			
			// Close connections
			br.close();
			out.close();
			socket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}

	public String getSharedPath() {
		return sharedPath;
	}

	public void setSharedPath(String sharedPath) {
		this.sharedPath = sharedPath;
	}
}
