import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by romainjulien on 27/05/2015.
 */
public class ControlView extends EventEmitter {
    private JPanel controlPanel;

    public ControlView() {
        controlPanel = new JPanel();
        GridBagLayout gridLayout = new GridBagLayout();
        controlPanel.setLayout(gridLayout);

        GridBagConstraints constraints = new GridBagConstraints();

        JButton test = new JButton("Stop Loop");
        test.addActionListener(event -> this.emit("stopLoop"));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipady = 20;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 2;

        controlPanel.add(test, constraints);

        JButton test2 = new JButton("Start Loop");
        test2.addActionListener(event -> this.emit("startLoop"));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;

        controlPanel.add(test2, constraints);

        JButton test3 = new JButton("test 3");
        constraints.gridx = 1;
        constraints.gridy = 1;

        controlPanel.add(test3, constraints);

    }

    public JPanel getPanel() {
        return controlPanel;
    }
}
