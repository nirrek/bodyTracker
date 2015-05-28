import gnu.io.CommPortIdentifier;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Contains the main control buttons, as well as a text area to display errors.
 */
public class ControlView extends EventEmitter {

    private JPanel controlPanel;

    private JComboBox<String> portsComboBox;
    private JButton refreshButton;

    private JButton connectButton;
    private JButton closeConnectionButton;

    private JButton loadFromFileButton;
    private JButton streamButton;
    private JButton stopStreamingButton;

    private JTextArea logs;

    public ControlView() {
        // Create a new Panel
        controlPanel = new JPanel();
        GridBagLayout gridLayout = new GridBagLayout();
        controlPanel.setLayout(gridLayout);

        // Title for the section Load from file
        JLabel sectionTitleLoad = new JLabel("Display arm movements saved on file.");
        addToGrid(sectionTitleLoad, 0, 0, 3, GridBagConstraints.HORIZONTAL);

        // Load from file button
        loadFromFileButton = new JButton("Load From File");
        loadFromFileButton.addActionListener(event -> this.emit("loadFile"));
        addToGrid(loadFromFileButton, 1, 0, 3, GridBagConstraints.HORIZONTAL);

        // Title for the section Stream data from Arduino
        JLabel sectionTitleStream = new JLabel("Display arm movements from the ClothMotion.");
        addToGrid(sectionTitleStream, 2, 0, 3, GridBagConstraints.HORIZONTAL);

        // Step 1 -- Components to select serial port
        JLabel stepOne = new JLabel("1. Select serial port connected to your ClothMotion");

        portsComboBox = new JComboBox<String>();

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> this.emit("refresh"));

        addToGrid(stepOne, 3, 0, 3, GridBagConstraints.HORIZONTAL);
        addToGrid(portsComboBox, 4, 0, 2, GridBagConstraints.HORIZONTAL);
        addToGrid(refreshButton, 4, 2, 1, GridBagConstraints.NONE);

        // Step 2 -- Components to start/stop connection with Arduino
        JLabel stepTwo = new JLabel("2. Click 'Connect' to begin connection with your Arduino");

        connectButton = new JButton("Connect");
        connectButton.addActionListener(event -> this.emit("connect"));

        closeConnectionButton = new JButton("Close Connection");
        closeConnectionButton.addActionListener(event -> this.emit("closeConnection"));
        //this button is disabled before a connection is established
        closeConnectionButton.setEnabled(false);

        addToGrid(stepTwo, 5, 0, 3, GridBagConstraints.HORIZONTAL);
        addToGrid(connectButton, 6, 0, 1, GridBagConstraints.NONE);
        addToGrid(closeConnectionButton, 6, 1, 1, GridBagConstraints.NONE);

        // Step 3 -- Components to start/stop streaming with Arduino
        JLabel stepThree = new JLabel("3. Click 'Stream From ClothMotion' to display arm movements");

        streamButton = new JButton("Start Streaming From Arduino");
        streamButton.addActionListener(event -> this.emit("streamFromArduino"));
        //this button is disabled before a connection is established
        streamButton.setEnabled(false);

        stopStreamingButton = new JButton("Stop Streaming");
        stopStreamingButton.addActionListener(event -> this.emit("stopStreaming"));
        //this button is disabled before user start streaming
        stopStreamingButton.setEnabled(false);

        addToGrid(stepThree, 7, 0, 3, GridBagConstraints.HORIZONTAL);
        addToGrid(streamButton, 8, 0, 2, GridBagConstraints.HORIZONTAL);
        addToGrid(stopStreamingButton, 8, 2, 1, GridBagConstraints.NONE);

        // Create logs text area
        logs = new JTextArea("");
        logs.setEditable(false);
        logs.setLineWrap(true);

        addToGrid(logs, 9, 0, 3, GridBagConstraints.HORIZONTAL);

        // Clear canvases button
        Button buttonClearCanvases = new Button("Clear canvases");
        buttonClearCanvases.addActionListener(event -> this.emit("clearCanvases"));

        addToGrid(buttonClearCanvases, 10, 0, 3, GridBagConstraints.HORIZONTAL);
    }

    private void addToGrid(Component comp, int row, int col, int colSpan,int fillConstraint) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = col;
        constraints.gridy = row;
        constraints.fill = fillConstraint;
        constraints.gridwidth = colSpan;
        constraints.ipady = 20;

        controlPanel.add(comp, constraints);
    }

    public void toggleControlPaneForArduinoConnected(boolean connected) {
        connectButton.setEnabled(!connected);
        closeConnectionButton.setEnabled(connected);
        streamButton.setEnabled(connected);
    }

    public String getSelectedPort() {
        return (String) portsComboBox.getSelectedItem();
    }

    public void showAvailablePorts(ArrayList<CommPortIdentifier> availablePorts) {
        portsComboBox.removeAllItems();
        for (CommPortIdentifier port : availablePorts)
            portsComboBox.addItem(port.getName());
    }

    public void displayError(String errorMessage) {
        logs.setText(errorMessage);
    }

    public void enableLoadFileButton(boolean enable) {
        loadFromFileButton.setEnabled(enable);
    }

    public void enableStreamButton(boolean enable) {
        streamButton.setEnabled(enable);
        stopStreamingButton.setEnabled(!enable);
    }

    public JPanel getPanel() {
        return controlPanel;
    }
}
