import gnu.io.CommPortIdentifier;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class RendererView extends EventEmitter {
	// TODO : REMOVE IF UNNECESSARRY
	private Modeler model;

	// The root node of the view tree.
	private GridPane containerView;

	// Front view canvas for rendering arm in space.
	private RenderCanvas canvasFront;

	// Side view canvas for rendering arm in space.
	private RenderCanvas canvasSide;


	// The main container of the view
	private HBox container;
	// The box where the drawing happens
	private VBox displayBox;
	// The control-panel
	private VBox controlBox;

	private ComboBox<String> portsComboBox;
	private Button refreshButton;
	private Label instructionLabel;
	private Button connectButton;
	private Button closeConnectionButton;
	private HBox getDataButtonWrapper;
	private Button fetchButton;
	private Button streamButton;
	private TextArea logs;


	public RendererView(Modeler model) {
		this.model = model;

		containerView = new GridPane();

		// Make the grid resize when the window is resized by the user
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.ALWAYS);
		containerView.getColumnConstraints().addAll(col1);
		RowConstraints row1 = new RowConstraints();
		row1.setVgrow(Priority.ALWAYS);
		containerView.getRowConstraints().addAll(row1);

		// Add the section for the front-view canvas
		Text titleCanvasFront = new Text("Front View");
		canvasFront = new RenderCanvas();
		containerView.add(titleCanvasFront, 1, 1);
		containerView.add(canvasFront.getNode(), 1, 2);

		// Add the section for the side-view canvas
		Text titleCanvasSide = new Text("Side View");
		canvasSide = new RenderCanvas();
		containerView.add(titleCanvasSide, 2, 1);
		containerView.add(canvasSide.getNode(), 2, 2);

		// Add section for load from file / clear canvas controls
		VBox section = new VBox(10);
			section.setStyle("-fx-fill-color: #eaeaea;");
			// Label for the section
			Text sectionTitle = new Text("Load data from a file");

			// Load from file button
			Button buttonLoadFromFile = new Button("Load from file");
			buttonLoadFromFile.getStyleClass().add("Btn--large");
			buttonLoadFromFile.setOnAction(event -> this.emit("loadFile"));

			// Clear canvases button
			Button buttonClearCanvases = new Button("Clear canvases");
			buttonClearCanvases.getStyleClass().add("Btn--large");
			buttonClearCanvases.setOnAction(event -> this.emit("clearCanvases"));

		section.getChildren().addAll(
				sectionTitle,
				buttonLoadFromFile,
				buttonClearCanvases
		);
		containerView.add(section, 3, 3);

		// -------------------------------------------------
		// Start old Romain code.
		container = new HBox();
		container.getStyleClass().add("Container");
		container.setAlignment(Pos.TOP_RIGHT);
		containerView.add(container, 3, 2);

//		displayBox = new VBox();
//		//TODO: settings for the display (if any)
//		initDisplayBox();

		controlBox = new VBox(4);
		controlBox.getStyleClass().add("Sidebar");
		initControlBox();

		container.getChildren().addAll(controlBox);
		// End old Romain code
		// -------------------------------------------------

	}


	private void initDisplayBox() {
		displayBox.setAlignment(Pos.CENTER);
		displayBox.setMinWidth(400);
		displayBox.setSpacing(10);
		displayBox.setPadding(new Insets(10, 10, 10, 10));
	}

	private void initControlBox() {
		controlBox.setAlignment(Pos.TOP_LEFT);
		controlBox.setSpacing(10);
		controlBox.setPadding(new Insets(5, 5, 5, 5));

		Label portLabel = new Label("Select port: ");
		portLabel.getStyleClass().add("Label");

		portsComboBox = new ComboBox<String>();
		portsComboBox.setMinWidth(240);

		refreshButton = new Button("Refresh");
		refreshButton.getStyleClass().add("Btn");

		HBox portSelectionWrapper = new HBox(3);
		portSelectionWrapper.getChildren().addAll(portLabel, portsComboBox, refreshButton);
		portSelectionWrapper.setSpacing(5);
		portSelectionWrapper.setPadding(new Insets(5, 5, 5, 0));
		portSelectionWrapper.setAlignment(Pos.BASELINE_LEFT);

		instructionLabel = new Label("Click Start to connect with your Arduino");
		instructionLabel.getStyleClass().add("Label");

		connectButton = new Button("Start");
		connectButton.getStyleClass().add("Btn");

		closeConnectionButton = new Button("Stop");
		closeConnectionButton.getStyleClass().add("Btn");
		//this button is disabled before a connection is established
		closeConnectionButton.setDisable(true);

		HBox connectionButtonWrapper = new HBox(3);
		connectionButtonWrapper.getChildren().addAll(instructionLabel, connectButton, closeConnectionButton);
		connectionButtonWrapper.setSpacing(5);
		connectionButtonWrapper.setPadding(new Insets(5, 5, 0, 0));
		connectionButtonWrapper.setAlignment(Pos.BASELINE_LEFT);

		streamButton = new Button("Start Streaming From Arduino");
		streamButton.getStyleClass().add("Btn--large");

		getDataButtonWrapper = new HBox(2);
		getDataButtonWrapper.getChildren().addAll(streamButton);
		getDataButtonWrapper.setSpacing(20);
		getDataButtonWrapper.setPadding(new Insets(10, 10, 10, 0));
		getDataButtonWrapper.setAlignment(Pos.BASELINE_LEFT);
		//this box is hidden before a connection is established
		getDataButtonWrapper.setVisible(false);

		logs = new TextArea("");
		logs.getStyleClass().add("Log");
		logs.setEditable(false);
		logs.setWrapText(true);

		controlBox.getChildren().addAll(
			portSelectionWrapper,
			connectionButtonWrapper,
			getDataButtonWrapper,
			logs
		);
	}


	public void addRefreshButtonHandler(EventHandler<ActionEvent> refreshButtonHandler) {

		refreshButton.setOnAction(refreshButtonHandler);
	}

	public void addConnectionButtonsHandler(EventHandler<ActionEvent> connectButtonHandler,
			EventHandler<ActionEvent> closeConnectionButtonHandler) {

		connectButton.setOnAction(connectButtonHandler);
		closeConnectionButton.setOnAction(closeConnectionButtonHandler);
	}

	public void addFetchStreamButtonsHandler(EventHandler<ActionEvent> fetchButtonHandler,
			EventHandler<ActionEvent> streamButtonHandler) {
		streamButton.setOnAction(streamButtonHandler);
	}

	public void toggleControlPaneForArduinoConnected(boolean connected) {
		connectButton.setDisable(connected);
		closeConnectionButton.setDisable(!connected);
		getDataButtonWrapper.setVisible(connected);
		if (connected) {
			instructionLabel.setText("Click Stop to close the connection with Arduino");
		} else {
			instructionLabel.setText("Click Start to connect with your Arduino");
		}
	}

	public String getSelectedPort() {
		return portsComboBox.getValue();
	}


	public void showAvailablePorts(ArrayList<CommPortIdentifier> availablePorts) {
		portsComboBox.getItems().clear();
		for (CommPortIdentifier port : availablePorts)
			portsComboBox.getItems().add(port.getName());
	}

	public void displayError(String errorMessage) {
		logs.setText(errorMessage);
	}

	public Node getNode() {
        return containerView;
    }

	// Temporary method for rendering a single arm from the side.
	public void renderLeftArm(Arm leftArm) {
		System.out.println("renderLeftArm()");
		canvasFront.drawArm(leftArm, "front");
		canvasSide.drawArm(leftArm, "side");
	}

	public void clearCanvases() {
		canvasFront.clearCanvas();
		canvasSide.clearCanvas();
	}
}
