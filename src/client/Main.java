/* Project: CSCI 2020U - Assignment 2
 * Author(s): Devon McGrath
 * ****************************************************************************
 * Name: Main
 * Author: Devon McGrath
 * Description: This is the main class that creates the display.
 * 
 * Version History:
 * 1.1 - 03/27/2017 - Added command line arguments so the user can pass
 * 	the server name and server path. - Devon McGrath
 * 1.0 - 03/23/2017 - Initial version - Devon McGrath
 */

package client;

import java.io.File;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;
import server.FTServer;

public class Main extends Application {

	public static void main(String[] args) {

		// Start the application
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		List<String> args = getParameters().getRaw();
		String computerName = null, sharedFolderPath = null;
		
		// Get the arguments
		if (args.size() >= 2) {
			computerName = args.get(0);
			sharedFolderPath = args.get(1);
		}

		// Create the display
		if (computerName == null || sharedFolderPath == null) {
			primaryStage.setScene(new SetupView(primaryStage));
		} else {
			primaryStage.setScene(new ClientView(
				primaryStage, new Client((new File("")).getAbsolutePath()),
				new FTServer(computerName, sharedFolderPath)));
		}
		primaryStage.setTitle("File Sharer v1.0");
		primaryStage.show();
	}
}
