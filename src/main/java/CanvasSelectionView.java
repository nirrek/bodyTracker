import javax.swing.*;

/**
 * Where user can select which artistic representation should be displayed
 * in the canvases.
 */
public class CanvasSelectionView extends EventEmitter {

    private JPanel canvasSelectionPanel;

    private JLabel leftCanvasTitle;
    private JComboBox<String> leftCanvasComboBox;

    private JLabel rightCanvasTitle;
    private JComboBox<String> rightCanvasComboBox;

    private JButton applyButton;

    public CanvasSelectionView() {
        canvasSelectionPanel = new JPanel();

        leftCanvasTitle = new JLabel(RenderCanvasEnum.Render2DFront.getValue());
        leftCanvasComboBox = new JComboBox<String>();
        fillComboBox(leftCanvasComboBox);

        rightCanvasTitle = new JLabel(RenderCanvasEnum.Render2DSide.getValue());
        rightCanvasComboBox = new JComboBox<String>();
        fillComboBox(rightCanvasComboBox);
        rightCanvasComboBox.setSelectedItem(RenderCanvasEnum.Render2DSide.getValue());

        applyButton = new JButton("Apply");
        applyButton.addActionListener(event -> this.emit("applyChanges"));

        canvasSelectionPanel.add(leftCanvasTitle);
        canvasSelectionPanel.add(leftCanvasComboBox);
        canvasSelectionPanel.add(rightCanvasTitle);
        canvasSelectionPanel.add(rightCanvasComboBox);
        canvasSelectionPanel.add(applyButton);
    }

    private void fillComboBox(JComboBox cb) {
        for (RenderCanvasEnum canvas : RenderCanvasEnum.values()) {
            cb.addItem(canvas.getValue());
        }
    }

    public boolean userHasSelectedDifferentLeftCanvas() {
        return !leftCanvasTitle.getText().equals(getSelectedLeftCanvas());
    }

    public boolean userHasSelectedDifferentRightCanvas() {
        return !rightCanvasTitle.getText().equals(getSelectedRightCanvas());
    }

    public String getSelectedLeftCanvas() {
        return (String) leftCanvasComboBox.getSelectedItem();
    }

    public String getSelectedRightCanvas() {
        return (String) rightCanvasComboBox.getSelectedItem();
    }

    public void setLeftCanvasTitle(String title) {
        leftCanvasTitle.setText(title);
    }

    public void setRightCanvasTitle(String title) {
        rightCanvasTitle.setText(title);
    }

    public JPanel getPanel() {
        return canvasSelectionPanel;
    }
}
