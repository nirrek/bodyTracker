import javax.swing.*;

/**
 * Where user can select which artistic representation should be displayed
 * in the canvases.
 */
public class CanvasSelectionView extends EventEmitter {

    private JPanel canvasSelectionPanel;

    private JLabel canvasLeftTitle;
    private JComboBox<String> canvasLeftComboBox;

    private JLabel canvasRightTitle;
    private JComboBox<String> canvasRightComboBox;

    private JButton applyButton;

    public CanvasSelectionView() {
        canvasSelectionPanel = new JPanel();

        canvasLeftTitle = new JLabel(RenderCanvasEnum.Render2DFront.getValue());
        canvasLeftComboBox = new JComboBox<String>();
        fillComboBox(canvasLeftComboBox);

        canvasRightTitle = new JLabel(RenderCanvasEnum.Render2DSide.getValue());
        canvasRightComboBox = new JComboBox<String>();
        fillComboBox(canvasRightComboBox);
        canvasRightComboBox.setSelectedItem(RenderCanvasEnum.Render2DSide.getValue());

        applyButton = new JButton("Apply");
        applyButton.addActionListener(event -> this.emit("applyChanges"));

        canvasSelectionPanel.add(canvasLeftTitle);
        canvasSelectionPanel.add(canvasLeftComboBox);
        canvasSelectionPanel.add(canvasRightTitle);
        canvasSelectionPanel.add(canvasRightComboBox);
        canvasSelectionPanel.add(applyButton);
    }

    private void fillComboBox(JComboBox cb) {
        for (RenderCanvasEnum canvas : RenderCanvasEnum.values()) {
            cb.addItem(canvas.getValue());
        }
    }

    public JPanel getPanel() {
        return canvasSelectionPanel;
    }
}
