package client;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import server.FTServer;

public class SetupView extends Scene {

	/** The main display window. */
	private Stage display;
	
	/** The text field that contains the path to the client files. */
	private TextField clientPath;
	
	/** The text field that contains the computer (server) name. */
	private TextField computerName;
	
	/** The text field that contains the path to the server files. */
	private TextField serverPath;
	
	/** The button that submits the data to the {@link ClientView} class. */
	private Button submit;
	
	/**
	 * Constructs a {@code SetupView} with a display.
	 * 
	 * @param display - the main window.
	 */
	public SetupView(Stage display) {
		super(new GridPane(), 500, 500);
		this.display = display;
		init();
	}
	
	/** Initializes the layout of the component. */
	private void init() {
		
		GridPane layout = (GridPane) getRoot();
		layout.setPadding(new Insets(5));
		layout.setVgap(5);
		layout.setHgap(5);
		
		// Create the view
		this.clientPath = new TextField();
		this.clientPath.setPromptText("path to save files to");
		this.computerName = new TextField("127.0.0.1");
		this.computerName.setPromptText("Computer Name");
		this.serverPath = new TextField();
		this.serverPath.setPromptText("Shared Path");
		this.serverPath.setPrefWidth(350);
		Button selectPath = new Button("Select Path");
		selectPath.setOnAction(e -> {
			DirectoryChooser dc = new DirectoryChooser();
			File dir = dc.showDialog(display);
			if (dir != null) {
				clientPath.setText(dir.getAbsolutePath());
			}
		});
		layout.add(selectPath, 1, 1);
		selectPath = new Button("Select Path");
		selectPath.setOnAction(e -> {
			DirectoryChooser dc = new DirectoryChooser();
			File dir = dc.showDialog(display);
			if (dir != null) {
				serverPath.setText(dir.getAbsolutePath());
			}
		});
		layout.add(selectPath, 1, 6);
		this.submit = new Button("Submit");
		this.submit.setOnAction(e -> {
			display.setScene(new ClientView(display,
					new Client(clientPath.getText()),
					new FTServer(computerName.getText(),
							serverPath.getText())));
		});
		
		// Add components
		layout.add(new Label("Download/Upload Path:"), 0, 0);
		layout.add(clientPath, 0, 1);
		layout.add(new Label("Computer Name:"), 0, 2);
		layout.add(computerName, 0, 4);
		layout.add(new Label("Shared Path:"), 0, 5);
		layout.add(serverPath, 0, 6);
		layout.add(submit, 0, 7);
	}

	public Stage getDisplay() {
		return display;
	}

	public void setDisplay(Stage display) {
		this.display = display;
	}
}
