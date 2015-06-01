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
    
    //Use to slow down rendering for 3D digital sketch
    private int count = 0;

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
        view.destroyCanvas();
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
                    //System.out.println("Port, "  + port.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  port.getName());
                    e.printStackTrace();
                }
            }
        }
        return availablePorts;
    }

    // -------------------------------------------------------------------------
    //      UI CONTROL METHODS
    // -------------------------------------------------------------------------

    private void updateUIForArduinoConnected(boolean isConnected) {
        view.enableConnectButton(!isConnected);
        view.enableStreamButton(isConnected);
    }

    private void updateUIForModelProcessingReadings(boolean isProcessingReadings) {
        view.enableLoadFileButton(!isProcessingReadings);

        if (serial != null) {
            if (!isProcessingReadings && serial.isConnected()) {
                view.enableStreamButton(!isProcessingReadings);
            }
        }
    }

    private void updateUIDisplaySerialPortsAvailable() {
        ArrayList<String> portNames = new ArrayList<>();

        for (CommPortIdentifier port : getAvailableSerialPorts()) {
            portNames.add(port.getName());
        }

        view.fillAvailablePortsComboBox(portNames);
    }

    // -------------------------------------------------------------------------
    //      MODEL EVENT LISTENERS
    // -------------------------------------------------------------------------

    private void modelAddedNewSample() {
    	Arm leftArm = model.getNextSample().getLeftArm();

    	if (view.getCanvas() != null) {
    		/* front 2d view canvas */
    		if (view.getCanvas() instanceof Render2DFront) {
    			view.getCanvas().drawArm(leftArm, "front");
    			/* digital 3d canvas - want to slow down sampling */
    		} else if (view.getCanvas() instanceof Digital3DSketch) {
    			System.out.println("Count: " + count);
   
    			if (count % 5 == 0) {
    				System.out.println("Should be rendering: " + count);
    				view.getCanvas().drawArm(leftArm, "side");
    			}
    			count++;
    		}	else { /* regular canvas */
    			view.getCanvas().drawArm(leftArm, "side");
    		}
    	}

    }

    // -------------------------------------------------------------------------
    //      VIEW EVENT LISTENERS
    // -------------------------------------------------------------------------

    // 'Refresh' button handler
    private void refreshButtonClicked() {
        updateUIDisplaySerialPortsAvailable();
    }


    // 'Start' button handler
    private void connectButtonClicked() {
    	
    	count = 0;
        portName = view.getSelectedPort();

        if (portName == null) {
            view.displayError("Please select a port to connect to Arduino");
            return;
        }

        if (!connect()) {
            view.displayError("Can not connect to port " + portName);
            return;
        }

        view.displayError("");
        updateUIForArduinoConnected(true);
    }

    // 'Stop' button handler
    private void closeConnectionButtonClicked() {
        stopSerialListener();
        closeConnection();
        updateUIForArduinoConnected(false);
    }

    // 'Load File' button handler
    private void loadFileButtonClicked() {
    	count = 0;

        if (view.getSelectedCanvas().equals("None")) {
            view.displayError("You must select a rendering style");
            return;
        }

        view.displayError("");

        JFileChooser fileChooser = new JFileChooser();
        File selectedFile;

        int returnVal = fileChooser.showOpenDialog(view.getContainer());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        } else {
            return;
        }

        updateUIForModelProcessingReadings(true);

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
                    SwingUtilities.invokeLater(() -> {
                        model.newSensorReading(samples.get(0));
                    });
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
            updateUIForModelProcessingReadings(false);
        })).start();
        
    }

    // 'Stream from Arduino' button handler
    private void streamFromArduinoButtonClicked() {

    	count = 0;

        // stop any preexisting listener
        stopSerialListener();

        view.displayError("");
        updateUIForModelProcessingReadings(true);

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
    	count = 0;
        view.displayError("");
        stopSerialListener();
        updateUIForModelProcessingReadings(false);
        view.finalRender();
    }

    // Clear canvases button handler
    private void clearCanvases() {
    	count = 0;
        view.displayError("");
        view.clearCanvas();
    }
    
    // Save Canvases button handler
    private void saveCanvases() {
    	count = 0;
        view.displayError("");
    	view.saveCanvas();
    } 

    // Apply button handler
    private void changeCanvases() {
        count = 0;

        // Check if the user has selected different rendering options for the left canvas
        if (!view.getSelectedCanvas().equals("None")) {
            view.changeCanvasToUserSelection();
            view.displayError("");
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
                updateUIForArduinoConnected(false);
                view.displayError("Connection with Arduino was interrupted");
            }
        }
    }
}
