import gnu.io.CommPortIdentifier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romainjulien on 28/05/2015.
 */
public class BodyTrackerContainer {

    private Container container;

    private CanvasSelectionView canvasSelectionView;
    private ControlView controlsView;

    private int numberCanvasesDisplayed;
    private int canvasSize;
    private Map<RenderCanvasEnum, RenderCanvas> mapCanvases;

    private JPanel centerPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;

    private RenderCanvas leftCanvas;
    private RenderCanvas rightCanvas;


    public BodyTrackerContainer(Container contentPane) {
        container = contentPane;
        initBodyTrackerGUI();

        numberCanvasesDisplayed = 2;
    }

    private void initMapCanvases() {
        mapCanvases = new HashMap<>();

        mapCanvases.put(RenderCanvasEnum.FrontView2D, new Render2DFront(canvasSize));
        mapCanvases.put(RenderCanvasEnum.SideView2D, new Render2DSide(canvasSize));
        mapCanvases.put(RenderCanvasEnum.Digital3DSketch, new Digital3DSketch(canvasSize));
        mapCanvases.put(RenderCanvasEnum.Digital2DSketch, new Digital3DSketch(canvasSize));

        mapCanvases.get(RenderCanvasEnum.FrontView2D).init();
        mapCanvases.get(RenderCanvasEnum.SideView2D).init();
        mapCanvases.get(RenderCanvasEnum.Digital3DSketch).init();
        mapCanvases.get(RenderCanvasEnum.Digital2DSketch).init();
    }

    private void initBodyTrackerGUI() {

        // Initiate the canvas selection panel and the control Panel
        canvasSelectionView = new CanvasSelectionView();
        controlsView = new ControlView();
        JPanel controlPanelWrapper = new JPanel(new FlowLayout());
        controlPanelWrapper.setAlignmentY(Component.TOP_ALIGNMENT);
        controlPanelWrapper.add(controlsView.getPanel());

        int screenSize = Toolkit.getDefaultToolkit().getScreenSize().width;
        int controlSize = getControlsView().getPanel().getPreferredSize().width;
        canvasSize = (screenSize - controlSize - 80) / 2;

        initMapCanvases();

        leftCanvas = mapCanvases.get(RenderCanvasEnum.FrontView2D);
        rightCanvas = mapCanvases.get(RenderCanvasEnum.SideView2D);

        // Layout the left and right canvases
        centerPanel = new JPanel(new FlowLayout());
        //BoxLayout layout = new BoxLayout(centerPanel, BoxLayout.X_AXIS);
        //centerPanel.setLayout(layout);
        leftPanel = new JPanel(new FlowLayout());
        rightPanel = new JPanel(new FlowLayout());

        leftPanel.add(leftCanvas);
        rightPanel.add(rightCanvas);

        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        // Add the canvases, the canvas selection Panel, and the control Panel to the view container
        container.add(canvasSelectionView.getPanel(), BorderLayout.NORTH);
        container.add(centerPanel, BorderLayout.WEST);
        //container.add(rightPanel, BorderLayout.CENTER);
        container.add(controlPanelWrapper, BorderLayout.EAST);
    }

    private void layoutCanvases() {

        for (int index = 0; index < centerPanel.getComponentCount(); index++) {
            System.out.println("Component of centerPanel " + centerPanel.getComponent(0).getName());
        }
        centerPanel.removeAll();

        switch (numberCanvasesDisplayed) {
            case 2:
                System.out.println("LEFT " + leftCanvas.getClass().toString());
                System.out.println("RIGHT " + rightCanvas.getClass().toString());

                centerPanel.add(leftPanel);
                centerPanel.add(rightPanel);
                System.out.println("Number component centerPanel " + centerPanel.getComponentCount());
                break;
            case 1:
                if (rightCanvas == null) {
                    System.out.println("View: right canvas is null");
                    System.out.println("LEFT " + leftCanvas.getClass().toString());
                    //leftPanel.removeAll();
                    //leftPanel.setPreferredSize(new Dimension(640, 640));
                    //leftPanel.add(mapCanvases.get(RenderCanvasEnum.Digital3DSketch));
                    leftCanvas = new Digital3DSketch(640);
                    leftCanvas.init();
                    JPanel p = new JPanel(new FlowLayout());
                    //p.setBorder(new EmptyBorder(10, 10, 10, 10));
                    p.add(leftCanvas);
                    centerPanel.add(p);
                    //centerPanel.setPreferredSize(new Dimension(640, 640));
                    //centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    System.out.println("Number component centerPanel " + centerPanel.getComponentCount());
                } else {
                    System.out.println("View: left canvas is null");
                    System.out.println("RIGHT " + rightCanvas.getClass().toString());
                    //rightPanel.removeAll();
                    //rightPanel.add(mapCanvases.get(RenderCanvasEnum.Digital3DSketch));
                    mapCanvases.get(RenderCanvasEnum.Digital3DSketch).init();
                    centerPanel.add(mapCanvases.get(RenderCanvasEnum.Digital3DSketch));
                    //centerPanel.setPreferredSize(new Dimension(640, 640));
                    System.out.println("Number component centerPanel " + centerPanel.getComponentCount());
                }
                break;
            default:
                //
        }
    }

    // -------------------------------------------------------------------------
    //      GETTERS
    // -------------------------------------------------------------------------

    public CanvasSelectionView getCanvasSelectionView() {
        return canvasSelectionView;
    }

    public ControlView getControlsView() {
        return controlsView;
    }

    public RenderCanvas getLeftCanvas() {
        return leftCanvas;
    }

    public RenderCanvas getRightCanvas() {
        return rightCanvas;
    }

    // -------------------------------------------------------------------------
    //      OTHER METHODS CANVAS SELECTION VIEW
    // -------------------------------------------------------------------------

    public boolean userHasSelectedLeftCanvas() {
        return canvasSelectionView.userHasSelectedLeftCanvas();
    }

    public boolean userHasSelectedRightCanvas() {
        return canvasSelectionView.userHasSelectedRightCanvas();
    }

    public void displayTwoCanvases() {
        String selectionLeft = canvasSelectionView.getSelectedLeftCanvas();
        String selectionRight = canvasSelectionView.getSelectedRightCanvas();

        RenderCanvasEnum enumLeft = RenderCanvasEnum.getEnumForValue(selectionLeft);
        RenderCanvasEnum enumRight = RenderCanvasEnum.getEnumForValue(selectionRight);

        leftCanvas = mapCanvases.get(enumLeft);
        rightCanvas = mapCanvases.get(enumRight);
        numberCanvasesDisplayed = 2;

        centerPanel.removeAll();

        leftCanvas.destroy();
        leftCanvas = mapCanvases.get(RenderCanvasEnum.SideView2D);
        leftCanvas.init();
        JPanel p = new JPanel(new FlowLayout());
        leftPanel.removeAll();
        leftPanel.add(leftCanvas);
        centerPanel.add(leftPanel);

        rightCanvas.destroy();
        rightCanvas = mapCanvases.get(RenderCanvasEnum.Digital3DSketch);
        rightCanvas.init();
        JPanel p2 = new JPanel(new FlowLayout());
        rightPanel.removeAll();
        rightPanel.add(rightCanvas);
        centerPanel.add(rightPanel);

        //layoutCanvases();
    }

    public void displayOneCanvas() {
        String selectionLeft = canvasSelectionView.getSelectedLeftCanvas();
        String selectionRight = canvasSelectionView.getSelectedRightCanvas();

        RenderCanvasEnum canvasEnum;

        centerPanel.removeAll();

        if (!selectionLeft.equals("None")) {
            canvasEnum = RenderCanvasEnum.getEnumForValue(selectionLeft);
            //leftCanvas = mapCanvases.get(canvasEnum);
            rightCanvas = null;
            leftCanvas = new Digital3DSketch(640);
            leftCanvas.init();
            JPanel p = new JPanel(new FlowLayout());
            p.add(leftCanvas);
            centerPanel.add(p);
        } else {
            canvasEnum = RenderCanvasEnum.getEnumForValue(selectionRight);
            leftCanvas = null;
            //rightCanvas = mapCanvases.get(rightCanvasEnum);
            rightCanvas = new Digital3DSketch(640);
            rightCanvas.init();
            JPanel p = new JPanel(new FlowLayout());
            p.add(rightCanvas);
            centerPanel.add(p);
        }
        numberCanvasesDisplayed = 1;

        //layoutCanvases();
    }

    // -------------------------------------------------------------------------
    //      OTHER METHODS CONTROLS VIEW
    // -------------------------------------------------------------------------

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
    //      OTHER METHODS CANVASES
    // -------------------------------------------------------------------------

    public void saveCanvases() {
        rightCanvas.save("Side");
        leftCanvas.save("Digital");
    }

    public void clearCanvases() {
        leftCanvas.clearCanvas();
        rightCanvas.clearCanvas();
    }

    public void destroyCanvases() {
        leftCanvas.destroy();
        rightCanvas.destroy();
    }

    public void finalRender() {
        if (leftCanvas != null) {
            leftCanvas.finalRender();
        }
        if (rightCanvas != null) {
            rightCanvas.finalRender();
        }
    }

    public Container getContainer() {
        return container;
    }
}
