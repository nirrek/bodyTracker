import gnu.io.*;

import javax.swing.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;


/**
 * The Application's Controller (Or the Renderer Controller...).
 * Connects to the Arduino via serial USB. Fetch/Stream data from the Arduino,
 * and display it in an artistic manner after conversion by the Modeler.
 */
public class Renderer {

    private static final int DATA_RATE = 115200;

    private Modeler model;

    private BodyTrackerContainer view;

    // The name of the serial port.
    private String portName;

    // Higher level serial wrapper by Kerrin
    private Serial serial;

    // The thread listening to inbound serial messages
    private Thread serialListener;
    
    // Use to slow down rendering for 3D digital sketch
    private int count = 0;

    // Use to decide which button should be enabled, depending on the state of the application
    private boolean serialConnected = false;
    private boolean modelIsProcessingReadings = false;
    private boolean isStreaming = false;

    // The destination path to the folder where JPG will be saved
    private String destinationPathSavedFile = null;

    /**
     * Constructors a new renderer that is bound to the given model and view.
     * @param modeler Produce a 3-Dimensional model of the user's limb in space
     * @param container The container view of the application
     */
    public Renderer(Modeler modeler, BodyTrackerContainer container) {
        this.model = modeler;
        this.view = container;

        model.addListener(Modeler.NEW_SAMPLE, p -> modelAddedNewSample());

        view.getConnectionView().addListener("refresh", event -> refreshButtonClicked());
        view.getConnectionView().addListener("connect", event -> connectButtonClicked());
        view.getConnectionView().addListener("closeConnection", event -> closeConnectionButtonClicked());

        view.getControlsView().addListener("applyChanges", event -> changeCanvases());
        view.getControlsView().addListener("loadFile", event -> loadFileButtonClicked());
        view.getControlsView().addListener("streamFromArduino", event -> streamFromArduinoButtonClicked());
        view.getControlsView().addListener("stopStreaming", event -> stopStreamingButtonClicked());
        view.getControlsView().addListener("clearCanvases", event -> clearCanvases());
        view.getControlsView().addListener("saveCanvases", event -> saveCanvases());

        updateUIDisplaySerialPortsAvailable();
    }

    /**
     * Establishes a new serial connection using, and stores the new Serial
     * connection object on the instance.
     *
     * @return true if connection is successful, and false otherwise
     */
    private boolean connect() {

        // Close existing serial before establishing new connection.
        closeConnection();

        this.serial = new Serial();
        this.serial.connect(this.portName, DATA_RATE);

        if (!this.serial.isConnected()) { return false;  }
        else { return true; }
    }


    /**
     * Lifecycle method to be called before the Renderer is unmounted to
     * clean-up necessary state.
     */
    public void unmount() {
        // Kill the Applets
        view.destroyCanvases();
        // Close existing serial and stop the Serial Listener
        stopSerialListener();
        closeConnection();
    }

    /**
     * Closes the serial connection.
     */
    private void closeConnection() {
        if (serial == null) return;
        serial.close();
    }

    /**
     * Stops the thread that is listening for inbound serial communication.
     */
    private void stopSerialListener() {
        if (serialListener == null) return;
        serialListener.interrupt();
    }

    /**
     * Fetches a list of all available serial ports.
     * Once the Arduino is connected, the port it is connected to will appear in
     * this list.
     *
     * This function is called when application is launched, and when user press
     * the 'Refresh' button.
     *
     * @author Kerrin
     * @return A list of available serial ports.
     */
    private static ArrayList<CommPortIdentifier> getAvailableSerialPorts() {
        ArrayList<CommPortIdentifier> availablePorts = new ArrayList<>();
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    CommPort thePort = port.open("CommUtil", 50);
                    thePort.close();
                    availablePorts.add(port);
                } catch (PortInUseException e) {
                    // Do nothing, the port is used
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  port.getName());
                    e.printStackTrace();
                }
            }
        }
        return availablePorts;
    }

    /**
     * Clear the error logs and reset the count value to 0
     */
    private void resetAfterButtonClicked() {
        count = 0;
        view.displayError("");
    }

    // -------------------------------------------------------------------------
    //      UI CONTROL METHODS
    // -------------------------------------------------------------------------

    /**
     * Display the serial ports available in the GUI combo box
     */
    private void updateUIDisplaySerialPortsAvailable() {
        ArrayList<String> portNames = new ArrayList<>();

        for (CommPortIdentifier port : getAvailableSerialPorts()) {
            portNames.add(port.getName());
        }

        view.fillAvailablePortsComboBox(portNames);
    }

    /**
     * Enable or disable the buttons depending on the state of the application
     */
    private void updateUIButtons() {
        view.enableConnectButton(!serialConnected);
        view.enableCloseConnectionButton(serialConnected && !modelIsProcessingReadings);
        view.enableLoadFileButton(!modelIsProcessingReadings);
        view.enableStreamButton(serialConnected && !modelIsProcessingReadings);
        view.enableStopStreamingButtons(serialConnected && modelIsProcessingReadings && isStreaming);
    }

    // -------------------------------------------------------------------------
    //      MODEL EVENT LISTENERS
    // -------------------------------------------------------------------------

    /**
     * 
     */
    private void modelAddedNewSample() {
    	Arm rightArm = model.getNextSample().getRightArm();

    	if (view.getCanvas() != null) {
    		/* front 2d view canvas */
    		if (view.getCanvas() instanceof Render2DSide) {
    			view.getCanvas().drawArm(rightArm, "side");
    			/* digital 3d canvas - want to slow down sampling, so only
    			 * using every 5 samples */
    		} else if (view.getCanvas() instanceof Digital3DSketch) {
    			if (count % 5 == 0) {
    				view.getCanvas().drawArm(rightArm, "front");
    			}
    			count++;
    		}	else { /* regular canvas */
    			view.getCanvas().drawArm(rightArm, "front");
    		}
    	}

    }

    // -------------------------------------------------------------------------
    //      VIEW EVENT LISTENERS
    // -------------------------------------------------------------------------

    // 'Refresh' button handler
    private void refreshButtonClicked() {
        resetAfterButtonClicked();
        updateUIDisplaySerialPortsAvailable();
    }


    // 'Start' button handler
    private void connectButtonClicked() {

        resetAfterButtonClicked();

        if (modelIsProcessingReadings) {
            view.displayError("Wait until the file finished loading");
            return;
        }

        portName = view.getSelectedPort();

        if (portName == null) {
            view.displayError("Please select a port to connect to Arduino");
            return;
        }

        if (!connect() ) {
            view.displayError("Can not connect to port " + portName);
            return;

        }

        serialConnected = true;
        updateUIButtons();
    }

    // 'Stop' button handler
    private void closeConnectionButtonClicked() {
        resetAfterButtonClicked();

        stopSerialListener();
        closeConnection();

        serialConnected = false;
        updateUIButtons();
    }

    // 'Load File' button handler
    private void loadFileButtonClicked() {

        resetAfterButtonClicked();

        if (view.getSelectedCanvas().equals("None")) {
            view.displayError("You must select a rendering style");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        File selectedFile;

        int returnVal = fileChooser.showOpenDialog(view.getContainer());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        } else {
            return;
        }

        modelIsProcessingReadings = true;
        updateUIButtons();

        // TODO: make the thread a private class that extends thread
        // Spawn a new thread for reading from the file
        (new Thread(() -> {
            Scanner s = null;
            try {
                s = new Scanner(selectedFile);
            } catch (FileNotFoundException e) { e.printStackTrace(); }
            if (s == null) return;

            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.equals("$")) continue; // message boundary

                List<Sample> samples = Sample.parseMessage(line);
                if (!samples.isEmpty()) {
                    try {
                        Thread.sleep(100); // simulate events coming in
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //only process the samples from the bNo
                    if (samples.get(0).getID() == 2) {
                        SwingUtilities.invokeLater(() -> {
                            model.newSensorReading(samples.get(0));
                        });
                    }
                }
            }

            //Finished reading from file
            //Need to pause & wait for the process to render the last reading
            try {
                Thread.sleep(500);               
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            view.finalRender();

            modelIsProcessingReadings = false;
            updateUIButtons();

        })).start();
        
    }

    // 'Stream from Arduino' button handler
    private void streamFromArduinoButtonClicked() {

        resetAfterButtonClicked();

        if (view.getSelectedCanvas().equals("None")) {
            view.displayError("You must select a rendering style");
            return;
        }

        // stop any preexisting listener
        stopSerialListener();

        modelIsProcessingReadings = true;
        isStreaming = true;
        updateUIButtons();


        serialListener = new SerialListener((message) -> {
            List<Sample> samples = Sample.parseMessage(message);

            if (!samples.isEmpty()) {
                model.newSensorReading(samples.get(0));
            }
        });
        serialListener.start();
    }

    // 'Stop Streaming' button handler
    private void stopStreamingButtonClicked() {
        resetAfterButtonClicked();

        stopSerialListener();

        modelIsProcessingReadings = false;
        isStreaming = false;
        updateUIButtons();

        view.finalRender();
    }

    // Clear canvases button handler
    private void clearCanvases() {
        resetAfterButtonClicked();

        // Check if the user has selected a rendering style
        if (!view.getSelectedCanvas().equals("None")) {
            view.clearCanvas();
        } else{
            view.displayError("You must select a rendering style");
        }
    }
    
    // Save Canvases button handler
    private void saveCanvases() {
        resetAfterButtonClicked();

        // Check if the user has selected a rendering style
        if (!view.getSelectedCanvas().equals("None")) {
            if (destinationPathSavedFile == null) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Choose a folder");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setAcceptAllFileFilterUsed(false);

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    destinationPathSavedFile = chooser.getSelectedFile().toString();
                }
            }

            view.saveCanvas(destinationPathSavedFile);

        } else{
            view.displayError("You must select a rendering style");
        }

    }

    // Apply button handler
    private void changeCanvases() {
        resetAfterButtonClicked();

        // Check if the user has selected a rendering style
        if (!view.getSelectedCanvas().equals("None")) {
            view.changeCanvasToUserSelection();
        } else{
            view.displayError("You must select a rendering style");
        }

    }

    // -------------------------------------------------------------------------
    //      PRIVATE THREADS
    // -------------------------------------------------------------------------

    /**
     * SerialListener thread reads new messages from the Serial and dispatches
     * them to the provided callback.
     */
    private class SerialListener extends Thread {
        // Callback to be executed when a new message arrives.
        private Consumer<String> callback;

        /**
         * Instantiates a new SerialListener thread. The callback provided
         * will be invoked every time a new message is received.
         * @param callback The callback to be invoked upon message reception. The
         *                 callback is passed a single message string argument.
         */
        SerialListener(Consumer<String> callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = serial.getNextMessage();

                    // Add the callback invokation to the an event queue on
                    // the application thread. This is required due to the fact
                    // that the JavaFX Scene graph is NOT THREADSAFE.
                    SwingUtilities.invokeLater(() -> callback.accept(message));
                    if (Thread.interrupted()) return;
                }
            } catch (IOException e) {
                serialConnected = false;
                updateUIButtons();

                view.displayError("Connection with Arduino was interrupted");
            }
        }
    }
}
