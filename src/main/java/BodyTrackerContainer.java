import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The container view for the application.
 * This class abstract the view hierarchy to the Renderer.
 */
public class BodyTrackerContainer {

    // The container of the JFrame
    private Container container;

    private ConnectionView connectionView;

    private ControlView controlsView;

    // A wrapper for the canvas, and at launch-time for the logo
    private JPanel canvasPanel;

    // The canvas used to render the movements. The canvas is actually a
    // Processing applet embed in the application.
    private RenderCanvas canvas;
    private int canvasWidth;
    private int canvasHeight;

    // A mapping of the classes name to their corresponding instance
    private Map<RenderCanvasEnum, RenderCanvas> mapCanvases;


    /**
     * The constructor initiates all of the view components, and takes care
     * of the layout of those components. It also instantiates the 5
     * Processing applets that have been implemented to render the movements.
     *
     * @param contentPane: The content panel of the JFrame
     * @param logoImage: The logo image
     * @param refreshImage: The refresh icon
     */
    public BodyTrackerContainer(Container contentPane, ImageIcon logoImage, ImageIcon refreshImage) {
        container = contentPane;

        // Initiates all the components and do the layout
        initBodyTrackerGUI(logoImage, refreshImage);

        // Set canvasWidth and canvasHeight private variables
        setCanvasWidthAndHeight();

        // Initiates instances of the 5 Processing applets (instances of RenderCanvas)
        // and map them to their class name
        initMapCanvases();
    }

    /**
     * Initiates all of the view components, and takes care of the layout of those components.
     *
     * @param logoImage: The logo as an IconImage
     * @param refreshImage: The refresh icon as an ImageIcon
     */
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
        // A wrapper for the connection panel (for layout purposes: it moves the panel to the top)
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

        // Add the logo to the canvas panel, it will be removed as soon as the user select a canvas
        JLabel logo = new JLabel(logoImage);
        JPanel logoWrapper = new JPanel(new GridBagLayout());
        logoWrapper.setOpaque(false);
        logoWrapper.add(logo);
        canvasPanel.add(logoWrapper);

        // Add the canvas panel, the connection Panel, and the control Panel to the view container
        container.add(connectionPanelWrapper, BorderLayout.NORTH);
        container.add(canvasPanel, BorderLayout.CENTER);
        container.add(controlPanelWrapper, BorderLayout.EAST);
    }

    /**
     * Set the canvas width and height, by retrieving the size of the screen, and
     * the preferred size of the control and the connection panels (we can do that here
     * because the panels have been layed out and their preferred size have been set).
     */
    private void setCanvasWidthAndHeight() {
        Dimension effectiveScreenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getMaximumWindowBounds().getSize();

        int windowWidth = Math.min(1920, effectiveScreenSize.width);
        int windowHeight = Math.min(1080 ,effectiveScreenSize.height);
        int controlWidth = getControlsView().getPanel().getPreferredSize().width;
        int connectionHeight = getConnectionView().getPanel().getPreferredSize().height;

        canvasWidth = windowWidth - controlWidth - 30;
        canvasHeight = windowHeight - connectionHeight - 25 - 20;
    }

    /**
     * Instantiates the 5 Processing applets that have been implemented to render the movements.
     * And stores them in a map. The Processing applets need to be initiated before the main
     * windows appears visible on screen (if not, unexplained layout and rendering behaviors happen)
     */
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

    /**
     * @return the control view
     */
    public ControlView getControlsView() {
        return controlsView;
    }

    /**
     * @return the connection view
     */
    public ConnectionView getConnectionView() {
        return connectionView;
    }

    /**
     * @return the current canvas
     */
    public RenderCanvas getCanvas() {
        return canvas;
    }

    // -------------------------------------------------------------------------
    //      METHODS CONNECTION VIEW
    // -------------------------------------------------------------------------

    /**
     * Fill the combo box in connection view with the content of the
     * given array.
     *
     * @param availablePorts An array of port names available for serial connection
     */
    public void fillAvailablePortsComboBox(ArrayList<String> availablePorts) {
        connectionView.getAvailablePortsComboBox().removeAllItems();
        for (String portName : availablePorts)
            connectionView.getAvailablePortsComboBox().addItem(portName);
    }

    /**
     * Get the item (String) currently selected on the combo box
     * in connection view.
     *
     * @return The name of the port selected
     */
    public String getSelectedPort() {
        return (String) connectionView.getAvailablePortsComboBox().getSelectedItem();
    }

    /**
     * Enable/disable the 'Connect' button
     *
     * @param enable: true if the button should be enabled, and false otherwise
     */
    public void enableConnectButton(boolean enable) {
        connectionView.getConnectButton().setEnabled(enable);
    }

    /**
     * Enable/disable the 'Close Connection' button
     *
     * @param enable: true if the button should be enabled, and false otherwise
     */
    public void enableCloseConnectionButton(boolean enable) {
        connectionView.getCloseConnectionButton().setEnabled(enable);
    }

    // -------------------------------------------------------------------------
    //      METHODS CONTROLS VIEW
    // -------------------------------------------------------------------------

    /**
     * Return the name of the canvas currently selected in the combo box
     * in the control view.
     *
     * @return The name of the canvas selected
     */
    public String getSelectedCanvas() {
        return (String) controlsView.getRenderingOptionComboBox().getSelectedItem();
    }

    /**
     * This function is called after the user has selected a different canvas and pressed
     * the 'Apply' button in the control view.
     * For layout reasons, we need to clear the canvas panel first, then assign the private
     * variable 'canvas' to the running instance of the selected canvas.
     */
    public void changeCanvasToUserSelection() {
        // Retrieve user selection
        String selectedCanvas = getSelectedCanvas();
        RenderCanvasEnum canvasEnum = RenderCanvasEnum.getEnumForValue(selectedCanvas);

        // Clear the old canvas before making the switch
        clearCanvas();

        // Add new canvas to the canvas panel
        canvasPanel.removeAll();
        canvas = mapCanvases.get(canvasEnum);
        canvasPanel.add(canvas);
        canvasPanel.validate();

        // Draw the human (only applicable for the 2D Side & Front views)
        canvas.drawModelWithArm();

        // Disable 'Apply' button until user make a new selection
        controlsView.getApplyButton().setEnabled(false);
    }

    /**
     * Enable/disable the 'Load File' button
     *
     * @param enable: true if the button should be enabled, and false otherwise
     */
    public void enableLoadFileButton(boolean enable) {
        controlsView.getLoadFromFileButton().setEnabled(enable);
    }

    /**
     * Enable/disable the 'Start Streaming' button
     *
     * @param enable: true if the button should be enabled, and false otherwise
     */
    public void enableStreamButton(boolean enable) {
        controlsView.getStreamButton().setEnabled(enable);
    }

    /**
     * Enable/disable the 'Stop Streaming' button
     *
     * @param enable: true if the button should be enabled, and false otherwise
     */
    public void enableStopStreamingButtons(boolean enable) {
        controlsView.getStopStreamingButton().setEnabled(enable);
    }

    /**
     * Display an error message to the user in the text area at the bottom
     * of the control view.
     *
     * @param error: The message to be displayed.
     */
    public void displayError(String error) {
        controlsView.getLogsTextArea().setText(error);
    }

    // -------------------------------------------------------------------------
    //      METHODS CANVASES
    // -------------------------------------------------------------------------

    /**
     * Save the canvas currently on screen as a JPG. Save to the folder with
     * absolute path 'path'.
     *
     * @param path The path to the folder where image is saved.
     */
    public void saveCanvas(String path) {
        if (canvas != null) {
            canvas.save(path + "/" + getSelectedCanvas());
        }
    }

    /**
     * Clear the canvas, ie remove the drawings of the arm movements and
     * display a black canvas.
     */
    public void clearCanvas() {
        if (canvas != null) {
            canvas.clearCanvas();
        }
    }

    /**
     * Kill instances of the canvases in mapCanvases.
     */
    public void destroyCanvases() {
        for (RenderCanvas canvas : mapCanvases.values()) {
            canvas.destroy();
        }
    }

    /**
     * Creates the final render in high quality (only applies to the 3D skecth)
     */
    public void finalRender() {
        if (canvas != null) {
            canvas.finalRender();
        }
    }

}
