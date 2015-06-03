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

    private ConnectionView connectionView;

    private ControlView controlsView;

    private int canvasWidth;
    private int canvasHeight;
    private Map<RenderCanvasEnum, RenderCanvas> mapCanvases;

    private JPanel canvasPanel;
    private RenderCanvas canvas;

    public BodyTrackerContainer(Container contentPane, ImageIcon logoImage, ImageIcon refreshImage) {
        container = contentPane;
        initBodyTrackerGUI(logoImage, refreshImage);
        setCanvasWidthAndHeight();
        initMapCanvases();
    }

    private void initBodyTrackerGUI(ImageIcon logoImage, ImageIcon refreshImage) {

        // Initiate the connection Panel
        connectionView = new ConnectionView(refreshImage);

        // Wrap the connection panel and a horizontal separator together
        JPanel connectionPanelWrapper = new JPanel();
        BoxLayout layout = new BoxLayout(connectionPanelWrapper, BoxLayout.Y_AXIS);
        connectionPanelWrapper.setLayout(layout);
        connectionPanelWrapper.setOpaque(false);
        connectionPanelWrapper.add(connectionView.getPanel());
        connectionPanelWrapper.add(new JSeparator(SwingConstants.HORIZONTAL));


        // Initiate the control Panel
        controlsView = new ControlView();

        // Wrap the connection panel and a vertical separator together
        JPanel controlPanelWrapper = new JPanel();
        BoxLayout layout2 = new BoxLayout(controlPanelWrapper, BoxLayout.X_AXIS);
        controlPanelWrapper.setLayout(layout2);
        controlPanelWrapper.setOpaque(false);

        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentY(Component.TOP_ALIGNMENT);
        wrapper.add(controlsView.getPanel());

        controlPanelWrapper.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanelWrapper.add(wrapper);

        // Layout the canvas panel
        canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setOpaque(false);
        canvasPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel logo = new JLabel(logoImage);
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(logo);
        canvasPanel.add(wrapperPanel);

        // Add the canvas panel, the connection Panel, and the control Panel to the view container
        container.add(connectionPanelWrapper, BorderLayout.NORTH);
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

    public ConnectionView getConnectionView() {
        return connectionView;
    }

    public RenderCanvas getCanvas() {
        return canvas;
    }

    // -------------------------------------------------------------------------
    //      METHODS CONNECTION VIEW
    // -------------------------------------------------------------------------

    public void fillAvailablePortsComboBox(ArrayList<String> availablePorts) {
        connectionView.getAvailablePortsComboBox().removeAllItems();
        for (String portName : availablePorts)
            connectionView.getAvailablePortsComboBox().addItem(portName);
    }

    public String getSelectedPort() {
        return (String) connectionView.getAvailablePortsComboBox().getSelectedItem();
    }

    public void enableConnectButton(boolean enable) {
        connectionView.getConnectButton().setEnabled(enable);
    }

    public void enableCloseConnectionButton(boolean enable) {
        connectionView.getCloseConnectionButton().setEnabled(enable);
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

        canvas.drawModelWithArm();

        controlsView.getApplyButton().setEnabled(false);
    }

    public void enableLoadFileButton(boolean enable) {
        controlsView.getLoadFromFileButton().setEnabled(enable);
    }

    public void enableStreamButton(boolean enable) {
        controlsView.getStreamButton().setEnabled(enable);
    }

    public void enableStopStreamingButtons(boolean enable) {
        controlsView.getStopStreamingButton().setEnabled(enable);
    }

    public void displayError(String error) {
        controlsView.getLogsTextArea().setText(error);
    }

    // -------------------------------------------------------------------------
    //      METHODS CANVASES
    // -------------------------------------------------------------------------

    public void saveCanvas(String path) {
        if (canvas != null) {
            canvas.save(path+"/"+getSelectedCanvas());
        }
    }

    public void clearCanvas() {
        if (canvas != null) {
            canvas.clearCanvas();
        }
    }

    public void destroyCanvases() {
        for (RenderCanvas canvas : mapCanvases.values()) {
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
