import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Where user can select which artistic representation should be displayed
 * in the canvases.
 */
public class CanvasSelectionView extends EventEmitter {

    private JPanel canvasSelectionPanel;

    private JComboBox<String> leftCanvasComboBox;

    private JComboBox<String> rightCanvasComboBox;

    private JButton applyButton;

    public CanvasSelectionView() {
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.LEFT);
        canvasSelectionPanel = new JPanel(layout);

        canvasSelectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        leftCanvasComboBox = new JComboBox<String>();
        fillComboBox(leftCanvasComboBox);
        leftCanvasComboBox.setSelectedItem(RenderCanvasEnum.FrontView2D.getValue());

        rightCanvasComboBox = new JComboBox<String>();
        fillComboBox(rightCanvasComboBox);
        rightCanvasComboBox.setSelectedItem(RenderCanvasEnum.SideView2D.getValue());

        applyButton = new JButton("Apply");
        applyButton.addActionListener(event -> this.emit("applyChanges"));

        canvasSelectionPanel.add(leftCanvasComboBox);
        canvasSelectionPanel.add(rightCanvasComboBox);
        canvasSelectionPanel.add(applyButton);
    }

    private void fillComboBox(JComboBox cb) {
        for (RenderCanvasEnum canvas : RenderCanvasEnum.values()) {
            cb.addItem(canvas.getValue());
        }
    }

    public boolean userHasSelectedLeftCanvas() {
        if (getSelectedLeftCanvas().equals("None")) {
            return false;
        }
        return true;
    }

    public boolean userHasSelectedRightCanvas() {
        if (getSelectedRightCanvas().equals("None")) {
            return false;
        }
        return true;
    }

    public String getSelectedLeftCanvas() {
        return (String) leftCanvasComboBox.getSelectedItem();
    }

    public String getSelectedRightCanvas() {
        return (String) rightCanvasComboBox.getSelectedItem();
    }

    public JPanel getPanel() {
        return canvasSelectionPanel;
    }
}
