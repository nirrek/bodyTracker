import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by romainjulien on 1/06/2015.
 */
public class ConnectionView extends EventEmitter {

    private JPanel connectionPanel;

    private JComboBox<String> availablePortsComboBox;

    private JButton connectButton;
    private JButton closeConnectionButton;

    public ConnectionView(ImageIcon refreshImage) {

        // Create a new Panel
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        connectionPanel = new JPanel(layout);
        connectionPanel.setBackground(StyleClass.COLOR_LIGHT_GREEN);
        connectionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Title for the connection section
        JLabel sectionTitle = new JLabel("Connect to your ClothMotion:");
        sectionTitle.setFont(StyleClass.FONT_TITLE);
        sectionTitle.setForeground(StyleClass.COLOR_DARK_GREY);

        availablePortsComboBox = new JComboBox<String>();
        availablePortsComboBox.setFont(StyleClass.FONT_TEXT);
        availablePortsComboBox.setForeground(StyleClass.COLOR_DARK_GREY);
        availablePortsComboBox.setPreferredSize(new Dimension(300, availablePortsComboBox.getPreferredSize().height));

        JButton refreshButton = new JButton(refreshImage);
        refreshButton.setFont(StyleClass.FONT_TEXT);
        refreshButton.setForeground(StyleClass.COLOR_DARK_GREY);
        refreshButton.addActionListener(event -> this.emit("refresh"));

        connectButton = new JButton("Connect");
        connectButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        connectButton.setForeground(StyleClass.COLOR_DARK_GREY);
        connectButton.addActionListener(event -> this.emit("connect"));

        closeConnectionButton = new JButton("Close Connection");
        closeConnectionButton.setFont(StyleClass.FONT_TEXT_HIGHLIGHT);
        closeConnectionButton.setForeground(StyleClass.COLOR_DARK_GREY);
        closeConnectionButton.addActionListener(event -> this.emit("closeConnection"));
        //this button is disabled before a connection is established
        closeConnectionButton.setEnabled(false);

        connectionPanel.add(sectionTitle);
        connectionPanel.add(Box.createHorizontalStrut(20));
        connectionPanel.add(availablePortsComboBox);
        connectionPanel.add(refreshButton);
        connectionPanel.add(Box.createHorizontalStrut(20));
        connectionPanel.add(connectButton);
        connectionPanel.add(closeConnectionButton);
    }

    public JComboBox getAvailablePortsComboBox() { return availablePortsComboBox; }
    public JButton getConnectButton() { return connectButton; }
    public JButton getCloseConnectionButton() { return closeConnectionButton; }
    
    /**
     * Return the JPanel assosiated with the view
     */
    public JPanel getPanel() {
        return connectionPanel;
    }
}
