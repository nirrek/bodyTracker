import gnu.io.CommPortIdentifier;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Style;
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

    private JButton loadFromFileButton;
    private JButton streamButton;
    private JButton stopStreamingButton;

    private JTextArea logsTextArea;

    public ControlView() {
        // Create a new Panel
        controlPanel = new JPanel();
        controlPanel.setOpaque(false);
        controlPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        GridBagLayout gridLayout = new GridBagLayout();
        controlPanel.setLayout(gridLayout);

        // Title for the section select rendering style
        JLabel sectionTitleSelectStyle = new JLabel("Select rendering style.");
        sectionTitleSelectStyle.setFont(StyleClass.FONT_TITLE);
        sectionTitleSelectStyle.setForeground(StyleClass.COLOR_DARK_GREY);

        addToGrid(sectionTitleSelectStyle, 0, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        // Rendering option comboBox
        renderingOptionComboBox = new JComboBox<String>();
        renderingOptionComboBox.setFont(StyleClass.FONT_TEXT);
        renderingOptionComboBox.setForeground(StyleClass.COLOR_DARK_GREY);
        fillRenderingOptionComboBox(renderingOptionComboBox);
        renderingOptionComboBox.setSelectedItem(RenderCanvasEnum.None.getValue());
        renderingOptionComboBox.addItemListener( (e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                applyButton.setEnabled(true);
            }
        });

        // Change rendering option
        applyButton = new JButton("Apply");
        applyButton.setFont(StyleClass.FONT_TEXT);
        applyButton.setForeground(StyleClass.COLOR_DARK_GREY);
        applyButton.setPreferredSize(new Dimension(applyButton.getPreferredSize().width, 14));
        applyButton.addActionListener(event -> this.emit("applyChanges"));
        // Initially disabled, until user selects an option
        applyButton.setEnabled(false);

        // Save canvas(es) button
        Button buttonSaveCanvases = new Button("Save canvas");
        buttonSaveCanvases.setFont(StyleClass.FONT_TEXT);
        buttonSaveCanvases.setForeground(StyleClass.COLOR_DARK_GREY);
        buttonSaveCanvases.addActionListener(event -> this.emit("saveCanvases"));

        // Clear canvas(es) button
        Button buttonClearCanvases = new Button("Clear canvas");
        buttonClearCanvases.setFont(StyleClass.FONT_TEXT);
        buttonClearCanvases.setForeground(StyleClass.COLOR_DARK_GREY);
        buttonClearCanvases.addActionListener(event -> this.emit("clearCanvases"));

        addToGrid(renderingOptionComboBox, 1, 0, 2, GridBagConstraints.HORIZONTAL, 10);
        addToGrid(applyButton, 1, 2, 1, GridBagConstraints.NONE, 10);

        addToGrid(Box.createVerticalStrut(20), 2, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        addToGrid(buttonSaveCanvases, 3, 0, 3, GridBagConstraints.HORIZONTAL, 20);
        addToGrid(Box.createVerticalStrut(5), 4, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(buttonClearCanvases, 5, 0, 3, GridBagConstraints.HORIZONTAL, 20);

        addToGrid(Box.createVerticalStrut(20), 6, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(new JSeparator(SwingConstants.HORIZONTAL), 7, 0, 3, GridBagConstraints.HORIZONTAL, 0);


        // Title for the section Load from file
        JLabel sectionTitleLoad = new JLabel("Display arm movements saved on file.");
        sectionTitleLoad.setFont(StyleClass.FONT_TITLE);
        sectionTitleLoad.setForeground(StyleClass.COLOR_DARK_GREY);
        addToGrid(sectionTitleLoad, 8, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        addToGrid(Box.createVerticalStrut(20), 9, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        // Load from file button
        loadFromFileButton = new JButton("Load From File");
        loadFromFileButton.addActionListener(event -> this.emit("loadFile"));
        loadFromFileButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        loadFromFileButton.setForeground(StyleClass.COLOR_DARK_GREY);
        addToGrid(loadFromFileButton, 10, 0, 3, GridBagConstraints.HORIZONTAL, 20);

        addToGrid(Box.createVerticalStrut(20), 11, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        // Title for the section Stream data from Arduino
        JLabel sectionTitleStream = new JLabel("Display arm movements from the ClothMotion.");
        sectionTitleStream.setFont(StyleClass.FONT_TITLE);
        sectionTitleStream.setForeground(StyleClass.COLOR_DARK_GREY);
        addToGrid(sectionTitleStream, 12, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        streamButton = new JButton("Start Streaming From ClothMotion");
        streamButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        streamButton.setForeground(StyleClass.COLOR_DARK_GREY);
        streamButton.addActionListener(event -> this.emit("streamFromArduino"));
        //this button is disabled before a connection is established
        streamButton.setEnabled(false);

        stopStreamingButton = new JButton("Stop Streaming");
        stopStreamingButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        stopStreamingButton.setForeground(StyleClass.COLOR_DARK_GREY);
        stopStreamingButton.addActionListener(event -> this.emit("stopStreaming"));
        //this button is disabled before user start streaming
        stopStreamingButton.setEnabled(false);

        addToGrid(Box.createVerticalStrut(20), 13, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        addToGrid(streamButton, 14, 0, 2, GridBagConstraints.HORIZONTAL, 20);
        addToGrid(stopStreamingButton, 14, 2, 1, GridBagConstraints.NONE, 20);

        addToGrid(Box.createVerticalStrut(20), 15, 0, 3, GridBagConstraints.HORIZONTAL, 0);
        addToGrid(new JSeparator(SwingConstants.HORIZONTAL), 16, 0, 3, GridBagConstraints.HORIZONTAL, 0);

        // Create logs text area
        logsTextArea = new JTextArea("");
        logsTextArea.setPreferredSize(new Dimension(logsTextArea.getPreferredSize().width, 100));
        logsTextArea.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        logsTextArea.setForeground(StyleClass.COLOR_DARK_GREEN);
        logsTextArea.setBorder(new EmptyBorder(20, 10, 10, 10));
        logsTextArea.setEditable(false);
        logsTextArea.setLineWrap(true);

        addToGrid(logsTextArea, 17, 0, 3, GridBagConstraints.HORIZONTAL, 20);

    }

    private void addToGrid(Component comp, int row, int col, int colSpan,int fillConstraint, int ipady) {
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = col;
        constraints.gridy = row;
        constraints.fill = fillConstraint;
        constraints.gridwidth = colSpan;
        constraints.ipady = ipady;

        controlPanel.add(comp, constraints);
    }

    private void fillRenderingOptionComboBox(JComboBox cb) {
        for (RenderCanvasEnum canvas : RenderCanvasEnum.values()) {
            cb.addItem(canvas.getValue());
        }
    }

    public JComboBox getRenderingOptionComboBox() { return renderingOptionComboBox; }
    public JButton getApplyButton() { return applyButton; }

    public JButton getLoadFromFileButton() { return loadFromFileButton; }

    public JButton getStreamButton() { return streamButton; }
    public JButton getStopStreamingButton() { return stopStreamingButton; }

    public JTextArea getLogsTextArea() { return logsTextArea; }

    public JPanel getPanel() {
        return controlPanel;
    }
}
