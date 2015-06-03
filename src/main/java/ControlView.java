
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;


/**
 * The control view manages the panel located at the right of the GUI.
 * The panel is divided in two sections:
 *
 * - The Canvas management section:
 *          Contains a combo box to select the rendering style,
 *          and two action buttons to save and clear the canvas
 *
 * - The Data Processing management section:
 *          Contains action buttons to start/stop streaming from Arduino, and load files.
 *
 * The class provides getters functions to allow the parent view (BodyTrackerContainer) to
 * access the components in this view.
 */
public class ControlView extends EventEmitter {

    // The main Panel
    private JPanel controlPanel;

    // The combo box to select a rendering style from
    private JComboBox<String> renderingOptionComboBox;
    private JButton applyButton;

    // Buttons to activate and stop data processing, either by loading file or streaming
    private JButton loadFromFileButton;
    private JButton streamButton;
    private JButton stopStreamingButton;

    // Where the error messages are displayed to the user
    private JTextArea logsTextArea;

    /**
     * The constructor initiates the control view panel and its components,
     * It handles the layout of those components as well as their initial states.
     */
    public ControlView() {
        // Create a new Panel
        controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        GridBagLayout gridLayout = new GridBagLayout();
        controlPanel.setLayout(gridLayout);

        // Initiate components and add them to the grid
        initiateCanvasManagementComponents();
        initiateDataProcessingManagementComponents();

        // Create logs text area
        logsTextArea = new JTextArea("");
        logsTextArea.setPreferredSize(new Dimension(logsTextArea.getPreferredSize().width, 100));
        logsTextArea.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        logsTextArea.setForeground(StyleClass.COLOR_DARK_GREEN);
        logsTextArea.setBorder(new EmptyBorder(20, 10, 10, 10));
        logsTextArea.setEditable(false);
        logsTextArea.setLineWrap(true);

        // Add logs to the grid
        addToGrid(logsTextArea, 17, 0, 3, GridBagConstraints.HORIZONTAL, 20);

    }

    /**
     * - Initiates components of the canvas management section
     * - Set their style
     * - Add listeners to the buttons
     * - Add components to grid
     */
    private void initiateCanvasManagementComponents() {

        // Title for the Canvas management section
        JLabel sectionTitleSelectStyle = new JLabel("Select rendering style.");
        sectionTitleSelectStyle.setFont(StyleClass.FONT_TITLE);
        sectionTitleSelectStyle.setForeground(StyleClass.COLOR_DARK_GREY);

        // Rendering option comboBox
        renderingOptionComboBox = new JComboBox<String>();
        renderingOptionComboBox.setFont(StyleClass.FONT_TEXT);
        renderingOptionComboBox.setForeground(StyleClass.COLOR_DARK_GREY);
        fillRenderingOptionComboBox(renderingOptionComboBox);
        renderingOptionComboBox.setSelectedItem(RenderCanvasEnum.None.getValue());

        // Change rendering option
        applyButton = new JButton("Apply");
        applyButton.setFont(StyleClass.FONT_TEXT);
        applyButton.setForeground(StyleClass.COLOR_DARK_GREY);
        applyButton.setPreferredSize(new Dimension(applyButton.getPreferredSize().width, 14));
        // Initially disabled, until user selects an option
        applyButton.setEnabled(false);

        // Save canvas(es) button
        Button buttonSaveCanvases = new Button("Save canvas");
        buttonSaveCanvases.setFont(StyleClass.FONT_TEXT);
        buttonSaveCanvases.setForeground(StyleClass.COLOR_DARK_GREY);

        // Clear canvas(es) button
        Button buttonClearCanvases = new Button("Clear canvas");
        buttonClearCanvases.setFont(StyleClass.FONT_TEXT);
        buttonClearCanvases.setForeground(StyleClass.COLOR_DARK_GREY);

        // Add listeners
        renderingOptionComboBox.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) { applyButton.setEnabled(true); }
        });
        applyButton.addActionListener(event -> this.emit("applyChanges"));
        buttonSaveCanvases.addActionListener(event -> this.emit("saveCanvases"));
        buttonClearCanvases.addActionListener(event -> this.emit("clearCanvases"));

        // Add components to grid, space them out and add separator
        addToGrid(sectionTitleSelectStyle, 0, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(renderingOptionComboBox, 1, 0, 2, GridBagConstraints.HORIZONTAL, 10);
        addToGrid(applyButton, 1, 2, 1, GridBagConstraints.NONE, 10);
        addToGrid(Box.createVerticalStrut(20), 2, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(buttonSaveCanvases, 3, 0, 3, GridBagConstraints.HORIZONTAL, 20);
        addToGrid(Box.createVerticalStrut(5), 4, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(buttonClearCanvases, 5, 0, 3, GridBagConstraints.HORIZONTAL, 20);
        addToGrid(Box.createVerticalStrut(20), 6, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(new JSeparator(SwingConstants.HORIZONTAL), 7, 0, 3, GridBagConstraints.HORIZONTAL, 0);
    }

    /**
     * - Initiates components of the data processing management section
     * - Set their style
     * - Add listeners to the buttons
     * - Add components to grid
     */
    private void initiateDataProcessingManagementComponents() {

        // Title for the sub-section Load from file
        JLabel sectionTitleLoad = new JLabel("Display arm movements saved on file.");
        sectionTitleLoad.setFont(StyleClass.FONT_TITLE);
        sectionTitleLoad.setForeground(StyleClass.COLOR_DARK_GREY);

        // Load from file button
        loadFromFileButton = new JButton("Load From File");
        loadFromFileButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        loadFromFileButton.setForeground(StyleClass.COLOR_DARK_GREY);

        // Title for the sub-section Stream data from Arduino
        JLabel sectionTitleStream = new JLabel("Display arm movements from the ClothMotion.");
        sectionTitleStream.setFont(StyleClass.FONT_TITLE);
        sectionTitleStream.setForeground(StyleClass.COLOR_DARK_GREY);

        // Start streaming button
        streamButton = new JButton("Start Streaming From ClothMotion");
        streamButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        streamButton.setForeground(StyleClass.COLOR_DARK_GREY);
        //this button is disabled before a connection is established
        streamButton.setEnabled(false);

        // Stop streaming button
        stopStreamingButton = new JButton("Stop Streaming");
        stopStreamingButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        stopStreamingButton.setForeground(StyleClass.COLOR_DARK_GREY);
        //this button is disabled before user start streaming
        stopStreamingButton.setEnabled(false);

        // Add buttons listener
        loadFromFileButton.addActionListener(event -> this.emit("loadFile"));
        streamButton.addActionListener(event -> this.emit("streamFromArduino"));
        stopStreamingButton.addActionListener(event -> this.emit("stopStreaming"));

        // Add components to the Panel and space them out
        addToGrid(sectionTitleLoad, 8, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(Box.createVerticalStrut(20), 9, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(loadFromFileButton, 10, 0, 3, GridBagConstraints.HORIZONTAL, 20);
        addToGrid(Box.createVerticalStrut(20), 11, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(sectionTitleStream, 12, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(Box.createVerticalStrut(20), 13, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(streamButton, 14, 0, 2, GridBagConstraints.HORIZONTAL, 20);
        addToGrid(stopStreamingButton, 14, 2, 1, GridBagConstraints.NONE, 20);
        addToGrid(Box.createVerticalStrut(20), 15, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(new JSeparator(SwingConstants.HORIZONTAL), 16, 0, 3, GridBagConstraints.HORIZONTAL, 0);
    }

    /**
     * Helper method to add components to grid.
     * Receive information to set the constraints.
     *
     * @param comp: The component to add to the grid
     * @param row: The row to add the component
     * @param col: The column to add the component
     * @param colSpan: How many columns this component will span
     * @param fillConstraint: If the component should be resized and how so
     * @param ipady: The vertical padding
     */
    private void addToGrid(Component comp, int row, int col, int colSpan,int fillConstraint, int ipady) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = col;
        constraints.gridy = row;
        constraints.fill = fillConstraint;
        constraints.gridwidth = colSpan;
        constraints.ipady = ipady;

        controlPanel.add(comp, constraints);
    }

    /**
     * Helper method to fill the combo box containing the rendering styles.
     * All rendering styles were added to the enum RenderCanvasEnum, all possible
     * values are retrieved here and added to the combo box
     *
     * @param cb: The Rendering style combo box
     */
    private void fillRenderingOptionComboBox(JComboBox cb) {
        for (RenderCanvasEnum canvas : RenderCanvasEnum.values()) {
            cb.addItem(canvas.getValue());
        }
    }

    /**
     * @return The rendering style combo box
     */
    public JComboBox getRenderingOptionComboBox() { return renderingOptionComboBox; }

    /**
     * @return The 'Apply' button
     */
    public JButton getApplyButton() { return applyButton; }

    /**
     * @return The 'Load File' button
     */
    public JButton getLoadFromFileButton() { return loadFromFileButton; }

    /**
     * @return The 'Start Streaming' button
     */
    public JButton getStreamButton() { return streamButton; }

    /**
     * @return The 'Stop Streaming' button
     */
    public JButton getStopStreamingButton() { return stopStreamingButton; }

    /**
     * @return The logs text area
     */
    public JTextArea getLogsTextArea() { return logsTextArea; }

    /**
     * @return The control panel (contains all components of this view)
     */
    public JPanel getPanel() { return controlPanel; }
}
