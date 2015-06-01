import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romainjulien on 28/05/2015.
 */
public class BodyTrackerContainer {

    private Container container;

    private ControlView controlsView;

    private int canvasWidth;
    private int canvasHeight;
    private Map<RenderCanvasEnum, RenderCanvas> mapCanvases;

    private JPanel canvasPanel;
    private RenderCanvas canvas;

    public BodyTrackerContainer(Container contentPane) {
        container = contentPane;
        initBodyTrackerGUI();
        setCanvasWidthAndHeight();
        initMapCanvases();
    }

    private void initBodyTrackerGUI() {

        // Initiate the canvas selection panel and the control Panel
        controlsView = new ControlView();
        JPanel controlPanelWrapper = new JPanel(new FlowLayout());
        controlPanelWrapper.setAlignmentY(Component.TOP_ALIGNMENT);
        controlPanelWrapper.add(controlsView.getPanel());

        // Layout the canvas panel
        canvasPanel = new JPanel();
        canvasPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        canvasPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add the canvas panel and the control Panel to the view container
        container.add(canvasPanel, BorderLayout.CENTER);
        container.add(controlPanelWrapper, BorderLayout.EAST);
    }

    private void setCanvasWidthAndHeight() {
        Dimension effectiveScreenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getMaximumWindowBounds().getSize();
        int windowWidth = Math.min(1920, effectiveScreenSize.width);
        int windowHeight = Math.min(1080 ,effectiveScreenSize.height);
        int controlWidth = getControlsView().getPanel().getPreferredSize().width;
        canvasWidth = (windowWidth - controlWidth - 30);
        canvasHeight = windowHeight - 25 - 20;
    }

    private void initMapCanvases() {
        mapCanvases = new HashMap<>();

        mapCanvases.put(RenderCanvasEnum.FrontView2D,
                new Render2DFront(canvasWidth, canvasHeight));
        mapCanvases.put(RenderCanvasEnum.SideView2D,
                new Render2DSide(canvasWidth, canvasHeight));
        mapCanvases.put(RenderCanvasEnum.Digital3DSketch,
                new Digital3DSketch(canvasWidth, canvasHeight));
        mapCanvases.put(RenderCanvasEnum.Digital2DSketch,
                new Digital2DSketch(canvasWidth, canvasHeight));
        mapCanvases.put(RenderCanvasEnum.RenderGenerativeArt,
        		new RenderGenerativeArt(canvasWidth, canvasHeight));

        mapCanvases.get(RenderCanvasEnum.FrontView2D).init();
        mapCanvases.get(RenderCanvasEnum.SideView2D).init();
        mapCanvases.get(RenderCanvasEnum.Digital3DSketch).init();
        mapCanvases.get(RenderCanvasEnum.Digital2DSketch).init();
        mapCanvases.get(RenderCanvasEnum.RenderGenerativeArt).init();
    }

    // -------------------------------------------------------------------------
    //      GETTERS
    // -------------------------------------------------------------------------

    public ControlView getControlsView() {
        return controlsView;
    }

    public RenderCanvas getCanvas() {
        return canvas;
    }


    // -------------------------------------------------------------------------
    //      METHODS CONTROLS VIEW
    // -------------------------------------------------------------------------

    public String getSelectedCanvas() {
        return (String) controlsView.getRenderingOptionComboBox().getSelectedItem();
    }

    public void changeCanvasToUserSelection() {
        String selectedCanvas = getSelectedCanvas();
        RenderCanvasEnum canvasEnum = RenderCanvasEnum.getEnumForValue(selectedCanvas);

        clearCanvas();

        canvasPanel.removeAll();
        canvas = mapCanvases.get(canvasEnum);
        canvasPanel.add(canvas);
        canvasPanel.validate();

        controlsView.getApplyButton().setEnabled(false);
    }

    public void fillAvailablePortsComboBox(ArrayList<String> availablePorts) {
        controlsView.getAvailablePortsComboBox().removeAllItems();
        for (String portName : availablePorts)
            controlsView.getAvailablePortsComboBox().addItem(portName);
    }

    public String getSelectedPort() {
        return (String) controlsView.getAvailablePortsComboBox().getSelectedItem();
    }

    public void enableConnectButton(boolean enable) {
        controlsView.getConnectButton().setEnabled(enable);
        controlsView.getCloseConnectionButton().setEnabled(!enable);
    }

    public void enableLoadFileButton(boolean enable) {
        System.out.println(enable);
        controlsView.getLoadFromFileButton().setEnabled(enable);
    }

    public void enableStreamButton(boolean enable) {
        controlsView.getStreamButton().setEnabled(enable);
        controlsView.getStopStreamingButton().setEnabled(!enable);
    }

    public void displayError(String error) {
        controlsView.getLogsTextArea().setText(error);
    }

    // -------------------------------------------------------------------------
    //      METHODS CANVASES
    // -------------------------------------------------------------------------

    public void saveCanvas() {
        if (canvas != null) {
            canvas.save("Side");
        }
    }

    public void clearCanvas() {
        if (canvas != null) {
            canvas.clearCanvas();
        }
    }

    public void destroyCanvas() {
        if (canvas != null) {
            canvas.destroy();
        }
    }

    public void finalRender() {
        if (canvas != null) {
            canvas.finalRender();
        }
    }

    public Container getContainer() {
        return container;
    }
}
