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

	// The control box.
	private GridPane gridControlBox;

	// The main container of the view
	private HBox container;
	// The control-panel
	private VBox controlBox;

	private ComboBox<String> portsComboBox;
	private Button refreshButton;

	private Label instructionLabel;
	private Button connectButton;
	private Button closeConnectionButton;
	private Button loadFromFileButton;
	private Button streamButton;
	private TextArea logs;


	public RendererView(Modeler model) {
		this.model = model;

		// Create constraint to make the grid resize when the window is resized by the user
		ColumnConstraints colConsraint = new ColumnConstraints();
		colConsraint.setHgrow(Priority.ALWAYS);
		RowConstraints rowConstraint = new RowConstraints();
		rowConstraint.setVgrow(Priority.ALWAYS);

		// Main grid (root)
		containerView = new GridPane();
		//containerView.setGridLinesVisible(true);
		containerView.getColumnConstraints().addAll(colConsraint);
		containerView.getRowConstraints().addAll(rowConstraint);

		// Add the section for the front-view canvas
		Text titleCanvasFront = new Text("Front View");
		canvasFront = new RenderCanvas();
		containerView.add(titleCanvasFront, 0, 0);
		containerView.add(canvasFront.getNode(), 0, 1);

		// Add the section for the side-view canvas
		Text titleCanvasSide = new Text("Side View");
		canvasSide = new RenderCanvas();
		containerView.add(titleCanvasSide, 1, 0);
		containerView.add(canvasSide.getNode(), 1, 1);

		// Add the section for the control box
		gridControlBox = new GridPane();
		gridControlBox.getStyleClass().add("Sidebar");
		gridControlBox.setHgap(10);
		gridControlBox.setVgap(10);
		//gridControlBox.setGridLinesVisible(true);
		initControlBox(gridControlBox);
		containerView.add(gridControlBox, 2, 1);
	}

	private void initControlBox(GridPane controlBox) {

		// Title for the section Load from file
		Text sectionTitleLoad = new Text("Display arm movements saved on file.");

		// Load from file button
		loadFromFileButton = new Button("Load From File");
		loadFromFileButton.getStyleClass().add("Btn--large");
		loadFromFileButton.setOnAction(event -> this.emit("loadFile"));

		// Title for the section Stream data from Arduino
		Text sectionTitleStream = new Text("Display arm movements from the ClothMotion.");

		// Step-by-step instructions
		Text stepOne = new Text("1. Select serial port connected to your ClothMotion");
		Text stepTwo = new Text("2. Click 'Connect' to begin connection with your Arduino");
		Text stepThree = new Text("3. Click 'Stream From ClothMotion' to display arm movements");

		// Style titles and instructions
		sectionTitleStream.getStyleClass().add("SectionTitle");
		sectionTitleLoad.getStyleClass().add("SectionTitle");
		stepOne.getStyleClass().add("Instruction");
		stepTwo.getStyleClass().add("Instruction");
		stepThree.getStyleClass().add("Instruction");

		// Step one
		portsComboBox = new ComboBox<String>();
		portsComboBox.setMinWidth(240);

		refreshButton = new Button("Refresh");
		refreshButton.getStyleClass().add("Btn");
		refreshButton.setOnAction(event -> this.emit("refresh"));

		// Step two
		connectButton = new Button("Connect");
		connectButton.getStyleClass().add("Btn");
		connectButton.setOnAction(event -> this.emit("connect"));

		closeConnectionButton = new Button("Close Connection");
		closeConnectionButton.getStyleClass().add("Btn");
		closeConnectionButton.setOnAction(event -> this.emit("closeConnection"));
		//this button is disabled before a connection is established
		closeConnectionButton.setDisable(true);

		// Step three
		streamButton = new Button("Start Streaming From Arduino");
		streamButton.getStyleClass().add("Btn--large");
		streamButton.setOnAction(event -> this.emit("streamFromArduino"));
		//this button is disabled before a connection is established
		streamButton.setDisable(true);

		// Create logs text area
		logs = new TextArea("");
		logs.getStyleClass().add("Log");
		logs.setEditable(false);
		logs.setWrapText(true);

		// Clear canvases button
		Button buttonClearCanvases = new Button("Clear canvases");
		buttonClearCanvases.getStyleClass().add("Btn--large");
		buttonClearCanvases.setOnAction(event -> this.emit("clearCanvases"));

		// Add elements grid control box
		controlBox.add(sectionTitleLoad, 0, 0, 3, 1);
		controlBox.add(loadFromFileButton, 0, 1, 3, 1);

		controlBox.add(sectionTitleStream, 0, 2, 3, 1);

		controlBox.add(stepOne, 0, 3, 3, 1);
		controlBox.add(portsComboBox, 0, 4, 2, 1);
		controlBox.add(refreshButton, 2, 4);

		controlBox.add(stepTwo, 0, 5, 3, 1);
		controlBox.add(connectButton, 0, 6);
		controlBox.add(closeConnectionButton, 1, 6);

		controlBox.add(stepThree, 0, 7, 3, 1);
		controlBox.add(streamButton, 0, 8, 3, 1);

		controlBox.add(logs, 0, 9, 3, 1);

		controlBox.add(buttonClearCanvases, 0, 10, 3, 1);
	}

	public void toggleControlPaneForArduinoConnected(boolean connected) {
		connectButton.setDisable(connected);
		closeConnectionButton.setDisable(!connected);
		streamButton.setDisable(!connected);

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

	public void enableStreamButton(boolean enable) {
		streamButton.setDisable(!enable);
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
