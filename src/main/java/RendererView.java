import gnu.io.CommPortIdentifier;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;

public class RendererView extends EventEmitter {

	// The root node of the view tree.
	private GridPane containerView;

	// Front view canvas for rendering arm in space.
	private RenderCanvas canvasFront;

	// Side view canvas for rendering arm in space.
	private RenderCanvas canvasSide;

	// The control box.
	private GridPane controlBox;


	private ComboBox<String> portsComboBox;
	private Button refreshButton;

	private Button connectButton;
	private Button closeConnectionButton;

	private Button loadFromFileButton;
	private Button streamButton;
	private Button stopStreamingButton;

	private TextArea logs;


	public RendererView() {
		// Main grid (root)
		containerView = new GridPane();
		containerView.setGridLinesVisible(true);


		// Create constraint to make the grid resize when the window is resized by the user
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.ALWAYS);
		ColumnConstraints col2 = new ColumnConstraints(600);
		col2.setHgrow(Priority.ALWAYS);
		ColumnConstraints col3 = new ColumnConstraints(400);

		RowConstraints row1 = new RowConstraints();
		RowConstraints row2 = new RowConstraints();
		row2.setVgrow(Priority.ALWAYS);

		containerView.getColumnConstraints().addAll(col1, col2, col3);
		containerView.getRowConstraints().addAll(row1, row2);

		// Add the section for the front-view canvas
		Text titleCanvasFront = new Text("Front View");
		titleCanvasFront.getStyleClass().add("SectionTitle");
		canvasFront = new RenderCanvas();
		containerView.add(titleCanvasFront, 0, 0);
		containerView.add(canvasFront.getNode(), 0, 1);

		// Add the section for the side-view canvas
		Text titleCanvasSide = new Text("Side View");
		titleCanvasSide.getStyleClass().add("SectionTitle");
		canvasSide = new RenderCanvas();
		containerView.add(titleCanvasSide, 1, 0);
		containerView.add(canvasSide.getNode(), 1, 1);

		// Add the section for the control box
		controlBox = new GridPane();
		controlBox.getStyleClass().add("Sidebar");
		controlBox.setHgap(10);
		controlBox.setVgap(10);
		//gridControlBox.setGridLinesVisible(true);
		initControlBox(controlBox);
		containerView.add(controlBox, 2, 1);

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

		stopStreamingButton = new Button("Stop Streaming");
		stopStreamingButton.getStyleClass().add("Btn--large");
		stopStreamingButton.setOnAction(event -> this.emit("stopStreaming"));
		//this button is disabled before user start streaming
		stopStreamingButton.setDisable(true);

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
		controlBox.add(streamButton, 0, 8, 2, 1);
		controlBox.add(stopStreamingButton, 2, 8, 1, 1);

		controlBox.add(logs, 0, 9, 3, 1);

		controlBox.add(buttonClearCanvases, 0, 10, 3, 1);
	}

	public void toggleControlPaneForArduinoConnected(boolean connected) {
		connectButton.setDisable(connected);
		closeConnectionButton.setDisable(!connected);
		streamButton.setDisable(!connected);
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
		stopStreamingButton.setDisable(enable);
	}

	public void enableLoadFileButton(boolean enable) {
		loadFromFileButton.setDisable(!enable);
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
