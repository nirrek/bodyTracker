import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * The connection view manages the panel located at the top of the GUI.
 * The main components of the panel are:
 * - The combo box containing the names of the available serial ports
 * - The connect and close connection buttons, used to establish the
 *   connection with the Arduino.
 *
 * The class provides getters functions to allow the parent view (BodyTrackerContainer) to
 * access the components in this view.
 */
public class ConnectionView extends EventEmitter {

    // The main Panel
    private JPanel connectionPanel;

    private JComboBox<String> availablePortsComboBox;

    private JButton connectButton;
    private JButton closeConnectionButton;

    /**
     * The constructor initiates the connection view panel and its components,
     * It handles the layout of those components as well as their initial states.
     *
     * @param refreshImage: The refresh icon
     */
    public ConnectionView(ImageIcon refreshImage) {

        // Create a new Panel
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        connectionPanel = new JPanel(layout);
        connectionPanel.setBackground(StyleClass.COLOR_LIGHT_GREEN);
        connectionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        layoutComponents(refreshImage);
    }

    /**
     * - Initiates components and set their style
     * - Layout
     * - Add listeners to the buttons
     *
     * @param refreshImage: The refresh icon
     */
    private void layoutComponents(ImageIcon refreshImage) {
        // Title for the connection section
        JLabel sectionTitle = new JLabel("Connect to your ClothMotion:");
        sectionTitle.setFont(StyleClass.FONT_TITLE);
        sectionTitle.setForeground(StyleClass.COLOR_DARK_GREY);

        // The combo box for the available ports
        availablePortsComboBox = new JComboBox<String>();
        availablePortsComboBox.setFont(StyleClass.FONT_TEXT);
        availablePortsComboBox.setForeground(StyleClass.COLOR_DARK_GREY);
        availablePortsComboBox.setPreferredSize(new Dimension(
                300, availablePortsComboBox.getPreferredSize().height));

        // The refresh button
        JButton refreshButton = new JButton(refreshImage);
        refreshButton.setFont(StyleClass.FONT_TEXT);
        refreshButton.setForeground(StyleClass.COLOR_DARK_GREY);

        // The connect button
        connectButton = new JButton("Connect");
        connectButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        connectButton.setForeground(StyleClass.COLOR_DARK_GREY);

        closeConnectionButton = new JButton("Close Connection");
        closeConnectionButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        closeConnectionButton.setForeground(StyleClass.COLOR_DARK_GREY);
        //this button is disabled before a connection is established
        closeConnectionButton.setEnabled(false);

        // Add buttons listener
        refreshButton.addActionListener(event -> this.emit("refresh"));
        connectButton.addActionListener(event -> this.emit("connect"));
        closeConnectionButton.addActionListener(event -> this.emit("closeConnection"));

        // Add components to the Panel and space them out
        connectionPanel.add(sectionTitle);
        connectionPanel.add(Box.createHorizontalStrut(20));
        connectionPanel.add(availablePortsComboBox);
        connectionPanel.add(refreshButton);
        connectionPanel.add(Box.createHorizontalStrut(20));
        connectionPanel.add(connectButton);
        connectionPanel.add(closeConnectionButton);
    }

    /**
     * @return The availablePorts combo box
     */
    public JComboBox getAvailablePortsComboBox() { return availablePortsComboBox; }

    /**
     * @return The 'Connect' button
     */
    public JButton getConnectButton() { return connectButton; }

    /**
     * @return The 'Close Connection' button
     */
    public JButton getCloseConnectionButton() { return closeConnectionButton; }
    
    /**
     * @return The connection panel (contains all components of this view)
     */
    public JPanel getPanel() {
        return connectionPanel;
    }
}
