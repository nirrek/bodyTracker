import gnu.io.CommPortIdentifier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

/**
 * Lay out the control panel components, set their initial states,
 * and provide getters functions for further changes by the parent
 * view (BodyTrackerContainer)
 */
public class ControlView extends EventEmitter {

    private JPanel controlPanel;

    private JComboBox<String> renderingOptionComboBox;
    private JButton applyButton;

    private JComboBox<String> availablePortsComboBox;

    private JButton connectButton;
    private JButton closeConnectionButton;

    private JButton loadFromFileButton;
    private JButton streamButton;
    private JButton stopStreamingButton;

    private JTextArea logsTextArea;

    public ControlView() {
        // Create a new Panel
        controlPanel = new JPanel();
        GridBagLayout gridLayout = new GridBagLayout();
        controlPanel.setLayout(gridLayout);

        controlPanel.setBorder(new EmptyBorder(10, 0, 10, 10));

        // Title for the section select rendering style
        JLabel sectionTitleSelectStyle = new JLabel("Select rendering style.");
        addToGrid(sectionTitleSelectStyle, 0, 0, 3, GridBagConstraints.HORIZONTAL);

        // Rendering option comboBox
        renderingOptionComboBox = new JComboBox<String>();
        fillRenderingOptionComboBox(renderingOptionComboBox);
        renderingOptionComboBox.setSelectedItem(RenderCanvasEnum.None.getValue());
        renderingOptionComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    applyButton.setEnabled(true);
                }
            }
        });

        // Change rendering option
        applyButton = new JButton("Apply");
        applyButton.addActionListener(event -> this.emit("applyChanges"));
        // Initially disabled, until user selects an option
        applyButton.setEnabled(false);

        addToGrid(renderingOptionComboBox, 1, 0, 2, GridBagConstraints.HORIZONTAL);
        addToGrid(applyButton, 1, 2, 1, GridBagConstraints.NONE);

        // Title for the section Load from file
        JLabel sectionTitleLoad = new JLabel("Display arm movements saved on file.");
        addToGrid(sectionTitleLoad, 2, 0, 3, GridBagConstraints.HORIZONTAL);

        // Load from file button
        loadFromFileButton = new JButton("Load From File");
        loadFromFileButton.addActionListener(event -> this.emit("loadFile"));
        addToGrid(loadFromFileButton, 3, 0, 3, GridBagConstraints.HORIZONTAL);

        // Title for the section Stream data from Arduino
        JLabel sectionTitleStream = new JLabel("Display arm movements from the ClothMotion.");
        addToGrid(sectionTitleStream, 4, 0, 3, GridBagConstraints.HORIZONTAL);

        // Step 1 -- Components to select serial port
        JLabel stepOne = new JLabel("1. Select serial port connected to your ClothMotion");

        availablePortsComboBox = new JComboBox<String>();

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(event -> this.emit("refresh"));

        addToGrid(stepOne, 5, 0, 3, GridBagConstraints.HORIZONTAL);
        addToGrid(availablePortsComboBox, 6, 0, 2, GridBagConstraints.HORIZONTAL);
        addToGrid(refreshButton, 6, 2, 1, GridBagConstraints.NONE);

        // Step 2 -- Components to start/stop connection with Arduino
        JLabel stepTwo = new JLabel("2. Click 'Connect' to begin connection with your Arduino");

        connectButton = new JButton("Connect");
        connectButton.addActionListener(event -> this.emit("connect"));

        closeConnectionButton = new JButton("Close Connection");
        closeConnectionButton.addActionListener(event -> this.emit("closeConnection"));
        //this button is disabled before a connection is established
        closeConnectionButton.setEnabled(false);

        addToGrid(stepTwo, 7, 0, 3, GridBagConstraints.HORIZONTAL);
        addToGrid(connectButton, 8, 0, 1, GridBagConstraints.NONE);
        addToGrid(closeConnectionButton, 8, 1, 1, GridBagConstraints.NONE);

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

        addToGrid(stepThree, 9, 0, 3, GridBagConstraints.HORIZONTAL);
        addToGrid(streamButton, 10, 0, 2, GridBagConstraints.HORIZONTAL);
        addToGrid(stopStreamingButton, 10, 2, 1, GridBagConstraints.NONE);

        // Create logs text area
        logsTextArea = new JTextArea("");
        logsTextArea.setEditable(false);
        logsTextArea.setLineWrap(true);

        addToGrid(logsTextArea, 11, 0, 3, GridBagConstraints.HORIZONTAL);

        // Save canvas(es) button
        Button buttonSaveCanvases = new Button("Save canvas(es)");
        buttonSaveCanvases.addActionListener(event -> this.emit("saveCanvases"));
        addToGrid(buttonSaveCanvases, 12, 0, 3, GridBagConstraints.HORIZONTAL);
        
        // Clear canvas(es) button
        Button buttonClearCanvases = new Button("Clear canvas(es)");
        buttonClearCanvases.addActionListener(event -> this.emit("clearCanvases"));

        addToGrid(buttonClearCanvases, 13, 0, 3, GridBagConstraints.HORIZONTAL);
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

    private void fillRenderingOptionComboBox(JComboBox cb) {
        for (RenderCanvasEnum canvas : RenderCanvasEnum.values()) {
            cb.addItem(canvas.getValue());
        }
    }


    public JButton getApplyButton() { return applyButton; }
    public JButton getConnectButton() { return connectButton; }
    public JButton getCloseConnectionButton() { return closeConnectionButton; }
    public JButton getLoadFromFileButton() { return loadFromFileButton; }
    public JButton getStreamButton() { return streamButton; }
    public JButton getStopStreamingButton() { return stopStreamingButton; }

    public JComboBox getRenderingOptionComboBox() { return renderingOptionComboBox; }
    public JComboBox getAvailablePortsComboBox() { return availablePortsComboBox; }

    public JTextArea getLogsTextArea() { return logsTextArea; }

    public JPanel getPanel() {
        return controlPanel;
    }
}
