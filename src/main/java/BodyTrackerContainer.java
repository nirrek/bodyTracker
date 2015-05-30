import gnu.io.CommPortIdentifier;

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
        int screenWidth = effectiveScreenSize.width;
        int screenHeight = effectiveScreenSize.height;
        int controlWidth = getControlsView().getPanel().getPreferredSize().width;
        canvasWidth = (screenWidth - controlWidth - 30);
        canvasHeight = screenHeight - 25 - 20;
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

        mapCanvases.get(RenderCanvasEnum.FrontView2D).init();
        mapCanvases.get(RenderCanvasEnum.SideView2D).init();
        mapCanvases.get(RenderCanvasEnum.Digital3DSketch).init();
        mapCanvases.get(RenderCanvasEnum.Digital2DSketch).init();
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
        return controlsView.getSelectedCanvas();
    }

    public void changeCanvasToUserSelection() {
        String selectedCanvas = controlsView.getSelectedCanvas();
        RenderCanvasEnum canvasEnum = RenderCanvasEnum.getEnumForValue(selectedCanvas);

        clearCanvas();

        canvasPanel.removeAll();
        canvas = mapCanvases.get(canvasEnum);
        canvasPanel.add(canvas);
        canvasPanel.validate();

        controlsView.disableApplyButton();
    }

    public void showAvailablePorts(ArrayList<CommPortIdentifier> availablePorts) {
        controlsView.showAvailablePorts(availablePorts);
    }

    public String getSelectedPort() {
        return controlsView.getSelectedPort();
    }

    public void displayError(String error) {
        controlsView.displayError(error);
    }

    public void toggleControlPaneForArduinoConnected(boolean connected) {
        controlsView.toggleControlPaneForArduinoConnected(connected);
    }

    public void enableLoadFileButton(boolean enable) {
        controlsView.enableLoadFileButton(enable);
    }

    public void enableStreamButton(boolean enable) {
        controlsView.enableStreamButton(enable);
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
