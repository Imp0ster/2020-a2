/* Name: ClientView
 * Author: Devon McGrath'
 * Description: This class is responsible for creating the view that shows
 * files the user can upload/download.
 * 
 * Version History:
 * 1.0 - 03/23/2017 - Initial version - Devon McGrath
 */

package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import server.FTServer;
import server.ServerManager;

/**
 * The {@code ClientView} class is responsible for creating the main view of
 * the file sharer. It has the buttons to upload and download files to and from
 * the server. It also contains the lists of client and server files.
 */
public class ClientView extends Scene {
	
	/** The main display window. */
	private Stage display;
	
	/** The client using this program. */
	private Client client;
	
	/** The server manager used to manage the server and shut it down. */
	private ServerManager serverManager;
	
	/** The button that sends the DOWNLOAD command to the server. */
	private Button download;
	
	/** The button that sends the UPLOAD command to the server. */
	private Button upload;
	
	/** The button that creates a new {@link SetupView} so the user can enter
	 * the server info and what directory they want to use. */
	private Button updatePath;
	
	/** The list displayed to the user with the files on their machine in the
	 * directory they have chosen. */
	private ListView<String> localFiles;
	
	/** The list displayed to the user with the files on the server path
	 * specified. To get these files, the DIR command is sent to the server
	 * and the server responds with the list of files in the directory. */
	private ListView<String> serverFiles;

	/**
	 * Constructs a new {@code ClientView} with a display, client, and server.
	 * 
	 * @param display - the main window.
	 * @param client - the client using the program.
	 * @param server - the server for the client to connect to.
	 */
	public ClientView(Stage display, Client client, FTServer server) {
		this(display, client, server, 500, 500);
	}
	
	/**
	 * Constructs a new {@code ClientView} with a display, client, and server.
	 * Also sets the dimensions of the display.
	 * 
	 * @param display - the main window.
	 * @param client - the client using the program.
	 * @param server - the server for the client to connect to.
	 * @param width - the new width of the display.
	 * @param height - the new height of the display.
	 */
	public ClientView(Stage display, Client client, FTServer server,
			int width, int height) {
		super(new BorderPane(), width, height);
		setDisplay(display);
		this.client = client;
		setServer(server);
		init();
	}
	
	/** Initializes the layout of the component. */
	private void init() {
		
		// Create the layout
		BorderPane layout = (BorderPane) getRoot();
		layout.setPadding(new Insets(5));
		SplitPane panes = new SplitPane();
		
		// Create the components
		this.download = new Button("Download");
		this.upload = new Button("Upload");
		this.updatePath = new Button("Update Location");
		GridPane buttons = new GridPane();
		buttons.setPadding(new Insets(5,0,5,0));
		buttons.setHgap(5);
		this.localFiles = new ListView<>();
		this.serverFiles = new ListView<>();
		refresh();
		
		// Add actions to the components
		this.download.setOnAction(e -> download());
		this.upload.setOnAction(e -> upload());
		this.updatePath.setOnAction(e -> {
			serverManager.getServer().close();
			display.setScene(new SetupView(display));
		});
		
		// Add components to the main layout
		buttons.add(download, 0, 0);
		buttons.add(upload, 1, 0);
		buttons.add(updatePath, 2, 0);
		panes.getItems().add(localFiles);
		panes.getItems().add(serverFiles);
		layout.setTop(buttons);
		layout.setCenter(panes);
	}
	
	/**
	 * <b><em>refresh</em></b>
	 * 
	 * <p>Refreshes the list of files on the local machine and server. To get
	 * the list of server files, the client sends a DIR request and the server
	 * responds with the list of file names in the directory specified by the
	 * user.</p>
	 */
	public void refresh() {
		this.localFiles.getItems().clear();
		this.localFiles.getItems().addAll(client.listFiles());
		this.serverFiles.getItems().clear();
		String response = client.sendRequest(
				FTServer.LIST_DIRECTORIES, serverManager.getServer().getHost(),
				FTServer.SERVER_PORT);
		if (response.length() > 0) {
			this.serverFiles.getItems().addAll(response.split("\n"));
		}
	}
	
	/**
	 * <b><em>download</em></b>
	 * 
	 * <p>Downloads the selected server file to the client's machine. This is
	 * achieved through the client sending a DOWNLOAD request to the server
	 * with the file name. The server responds with the data contained in the
	 * file. Finally, the client saves that data to the local machine.</p>
	 * 
	 * @see {@link #upload()}
	 */
	public void download() {
		
		// Get the selected file
		String file = serverFiles.getSelectionModel().getSelectedItem();
		if (file == null || file.length() == 0) {
			return;
		}
		
		// Ask the server for the file
		String data = client.sendRequest(FTServer.DOWNLOAD + " " + file,
				serverManager.getServer().getHost(), FTServer.SERVER_PORT);
		
		// Save the file
		try {
			FileWriter fw = new FileWriter(
					client.getSharedPath()+File.separator+file);
			fw.write(data);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		refresh();
	}
	
	/**
	 * <b><em>upload</em></b>
	 * 
	 * <p>Uploads the selected client file to the server. This is achieved by
	 * sending a UPLOAD command to the server with the file name followed by
	 * all the file data. The server then saves that data to the server path
	 * specified.</p>
	 * 
	 * @see {@link #download()}
	 */
	public void upload() {
		
		// Get the selected file
		String filename = localFiles.getSelectionModel().getSelectedItem();
		File file = new File(client.getSharedPath()+File.separator+filename);
		if (filename == null || filename.length() == 0) {
			return;
		}
		if (!file.exists()) {
			return;
		}
		
		// Get the file data
		String fileData = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				fileData += line + "\n";
			}
			if (fileData.length() > 0) {
				fileData = fileData.substring(0, fileData.length()-1);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Send the data to the server
		this.client.sendRequest(FTServer.UPLOAD+" "+filename+"\n"+fileData,
				serverManager.getServer().getHost(), FTServer.SERVER_PORT);
		refresh();
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public FTServer getServer() {
		return serverManager.getServer();
	}

	public void setServer(FTServer server) {
		if (serverManager != null && serverManager.getServer() != null) {
			this.serverManager.getServer().close();
		}
		this.serverManager = new ServerManager(server);
		this.serverManager.start();
	}
	
	public ServerManager getServerManager() {
		return serverManager;
	}

	public Stage getDisplay() {
		return display;
	}

	public void setDisplay(Stage display) {
		this.display = display;
		if (display != null) {
			this.display.setOnCloseRequest(e -> {
				serverManager.getServer().close();
				System.exit(0);
			});
		}
	}
}
