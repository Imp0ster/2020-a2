package server;

import java.io.*;
import java.net.Socket;

/**
 * The {@code ClientConnectionHandler} class is a way for a new connection
 * between the client and server to be handled. The {@link #run()} method will
 * get the command passed from the client and respond if and only if the
 * command is one of {DIR, UPLOAD, DOWNLOAD}. Note: the class extends thread,
 * so it should be started using {@link #start()} so that the server can handle
 * multiple connections.
 */
public class ClientConnectionHandler extends Thread {

	/** The server that the client connected to. */
	private FTServer server;
	
	/** The connection between the client and the server. */
	private Socket socket;

	/**
	 * Constructs a new connection handler.
	 * 
	 * @param server - the server the client connected to.
	 * @param socket - the connection between the client and server.
	 */
	public ClientConnectionHandler(FTServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public void run() {

		try {
			
			// Get the input stream from the socket
			InputStream in = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = br.readLine();
			
			// Client wants a list of directories
			if (line.startsWith(FTServer.LIST_DIRECTORIES)) {
				
				// Write the list of files
				String[] files = server.listFiles();
				PrintWriter pw = new PrintWriter(socket.getOutputStream());
				for (String file : files) {
					pw.println(file);
				}
				pw.flush();
				pw.close();
			}
			
			// Client wants to upload a file
			else if (line.startsWith(FTServer.UPLOAD)) {
				
				// Upload (write) the file to the server
				String filename = line.substring(FTServer.UPLOAD.length()+1);
				PrintWriter out = new PrintWriter(
						server.getSharedPath()+File.separator+filename);
				String fileData = "";
				while (br.ready()) {
					line = br.readLine();
					fileData += line + "\n";
				}
				if (fileData.length() > 0) {
					fileData = fileData.substring(0, fileData.length()-1);
				}
				out.write(fileData);
				out.flush();

				// Close streams
				out.close();
				br.close();
			}
			
			// Client wants to download a file
			else if (line.startsWith(FTServer.DOWNLOAD)) {
				
				// Copy the file from the server
				String filename = line.substring(FTServer.DOWNLOAD.length()+1);
				byte[] buffer = new byte[1024];
				FileInputStream fileIn = new FileInputStream(
						server.getSharedPath()+File.separator+filename);
				OutputStream out = socket.getOutputStream();
				int bytesRead = 0;
		        while ((bytesRead = fileIn.read(buffer)) > 0) {
		            out.write(buffer, 0, bytesRead);
		        }
		        out.flush();
		        
		        // Close streams
		        fileIn.close();
		        out.close();
			}
			
			// Close streams
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FTServer getServer() {
		return server;
	}

	public void setServer(FTServer server) {
		this.server = server;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
}
