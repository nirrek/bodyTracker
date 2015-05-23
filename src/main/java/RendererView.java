import gnu.io.CommPortIdentifier;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;


public class RendererView {


	// TODO : REMOVE IF UNNECESSARRY
	private Modeler model;

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

		container = new HBox(2);
		container.getStyleClass().add("Container");
		container.setAlignment(Pos.TOP_RIGHT);

		displayBox = new VBox();
		//TODO: settings for the display (if any)
		initDisplayBox();

		controlBox = new VBox(4);
		controlBox.getStyleClass().add("Sidebar");
		initControlBox();

		container.getChildren().addAll(displayBox, controlBox);
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

		fetchButton = new Button("Fetch");
		fetchButton.getStyleClass().add("Btn--large");

		streamButton = new Button("Start Streaming From Arduino");
		streamButton.getStyleClass().add("Btn--large");

		getDataButtonWrapper = new HBox(2);
		getDataButtonWrapper.getChildren().addAll(fetchButton, streamButton);
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
				portSelectionWrapper, connectionButtonWrapper, getDataButtonWrapper, logs);
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

		fetchButton.setOnAction(fetchButtonHandler);
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
        return container;
    }
}
