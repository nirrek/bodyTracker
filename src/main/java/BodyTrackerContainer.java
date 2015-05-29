import gnu.io.CommPortIdentifier;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 * Created by romainjulien on 28/05/2015.
 */
public class BodyTrackerContainer {

    private Container container;

    private CanvasSelectionView canvasSelectionView;
    private ControlView controlsView;
    private RenderCanvas leftCanvas;
    private RenderCanvas rightCanvas;

    public BodyTrackerContainer(Container contentPane) {
        container = contentPane;
        initBodyTrackerGUI();
    }

    private void initBodyTrackerGUI() {

        // Initiate the canvas selection panel and the control Panel
        canvasSelectionView = new CanvasSelectionView();
        controlsView = new ControlView();

        // TODO decide on the default canvas
        // Make the left canvas render a (Front view) 2D representation of the arm
        leftCanvas = new Render2DFront();
        leftCanvas.init();
        // Make the right canvas render a (Side view) 2D representation of the arm
        rightCanvas = new Render2DSide();
        rightCanvas.init();

        // Add the canvases, the canvas selection Panel, and the control Panel to the view container
        container.add(canvasSelectionView.getPanel(), BorderLayout.NORTH);
        container.add(leftCanvas, BorderLayout.WEST);
        container.add(rightCanvas, BorderLayout.CENTER);
        container.add(controlsView.getPanel(), BorderLayout.EAST);
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

    public boolean userHasSelectedDifferentLeftCanvas() {
        return canvasSelectionView.userHasSelectedDifferentLeftCanvas();
    }

    public boolean userHasSelectedDifferentRightCanvas() {
        return canvasSelectionView.userHasSelectedDifferentRightCanvas();
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


    public void clearCanvases() {
        leftCanvas.clearCanvas();
        rightCanvas.clearCanvas();
    }

    public void destroyCanvases() {
        leftCanvas.destroy();
        rightCanvas.destroy();
    }

    public void changeLeftCanvasToUserSelection() {
        leftCanvas.destroy();

        String newCanvas = canvasSelectionView.getSelectedLeftCanvas();
        RenderCanvasEnum newCanvasEnum = RenderCanvasEnum.getEnumForValue(newCanvas);

        try {
            Class<?> RenderCanvasClass = Class.forName(newCanvasEnum.name());
            Constructor<?> constructor = RenderCanvasClass.getConstructor();
            rightCanvas = (RenderCanvas) constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            controlsView.displayError("An error happened when changing rendering option");
            return;
        }

        leftCanvas.init();
        canvasSelectionView.setLeftCanvasTitle(newCanvas);
    }

    public void changeRightCanvasToUserSelection() {
        rightCanvas.destroy();

        String newCanvas = canvasSelectionView.getSelectedRightCanvas();
        RenderCanvasEnum newCanvasEnum = RenderCanvasEnum.getEnumForValue(newCanvas);

        try {
            Class<?> RenderCanvasClass = Class.forName(newCanvasEnum.name());
            Constructor<?> constructor = RenderCanvasClass.getConstructor();
            rightCanvas = (RenderCanvas) constructor.newInstance();
        } catch (Exception e) {
            controlsView.displayError("An error happened when changing rendering option");
            return;
        }

        rightCanvas.init();
        canvasSelectionView.setRightCanvasTitle(newCanvas);
    }

    public Container getContainer() {
        return container;
    }
}
