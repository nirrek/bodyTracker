import gnu.io.*;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // A boolean value indicating if data are currently
    // streamed from Arduino, or loaded from file
    private boolean modelIsProcessingNewReadings = false;
    /**
     * Constructors a new renderer that is bound to the given model and view.
     * @param modeler Produce a 3-Dimensional model of the user's limb in space
     * @param container The container view of the application
     */
    public Renderer(Modeler modeler, BodyTrackerContainer container) {
        this.model = modeler;
        this.view = container;

        model.addListener(Modeler.NEW_SAMPLE, p -> modelAddedNewSample());

        view.getControlsView().addListener("refresh", event -> refreshButtonClicked());
        view.getControlsView().addListener("connect", event -> startButtonClicked());
        view.getControlsView().addListener("closeConnection", event -> stopButtonClicked());
        view.getControlsView().addListener("loadFile", event -> loadFileButtonClicked());
        view.getControlsView().addListener("streamFromArduino", event -> streamFromArduinoButtonClicked());
        view.getControlsView().addListener("stopStreaming", event -> stopStreamingButtonClicked());
        view.getControlsView().addListener("clearCanvases", event -> clearCanvases());
        view.getControlsView().addListener("saveCanvases", event -> saveCanvases());

        view.getCanvasSelectionView().addListener("applyChanges", event -> changeCanvases());

        displaySerialPortsAvailable();
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
        view.destroyCanvases();
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
     * Parses a message from the Arduino, and returns a list of Samples
     * from the message.
     * @param msg The message to parse
     * @return A list of Samples, or null if no valid samples in the message.
     */
    public static List<Sample> parseMessage(String msg) {
        Pattern sampleRegex = Pattern.compile(
                "^id ([0-9]+) " + // group 1
                "time ([0-9]+) " + // group 2
                "x ([-]?[0-9]+\\.?[0-9]+) " + // group 3
                "y ([-]?[0-9]+\\.?[0-9]+) " + // group 4
                "z ([-]?[0-9]+\\.?[0-9]+)[ ]?$"  // group 5
        );

        List<Sample> samples = new ArrayList<>();

        for (String line : msg.split("\n")) {
            Matcher m = sampleRegex.matcher(line);
            if (!m.matches()) {
                System.out.println("Invalid sample line: ");
                System.out.println(line);
                continue; // skip invalid lines
            }

            Sample sample = new Sample();
            sample.sensorId = Integer.parseInt(m.group(1));
            sample.timestamp = Long.parseLong(m.group(2));
            sample.yaw = Double.parseDouble(m.group(3));   // X => yaw
            sample.pitch = Double.parseDouble(m.group(4)); // Y => pitch
            sample.roll = Double.parseDouble(m.group(5));  // Z => roll

            samples.add(sample);
        }

        return samples;
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

    private void displaySerialPortsAvailable() {
        ArrayList<CommPortIdentifier> availablePorts = getAvailableSerialPorts();
        view.showAvailablePorts(availablePorts);
    }

    // -------------------------------------------------------------------------
    //      MODEL EVENT LISTENERS
    // -------------------------------------------------------------------------

    private void modelAddedNewSample() {
        System.out.println("new sample added to model");
        Arm leftArm = model.getNextSample().getLeftArm();

        view.getLeftCanvas().drawArm(leftArm, "front");
        view.getRightCanvas().drawArm(leftArm, "side");

    }

    // -------------------------------------------------------------------------
    //      VIEW EVENT LISTENERS
    // -------------------------------------------------------------------------

    // 'Refresh' button handler
    private void refreshButtonClicked() {
        displaySerialPortsAvailable();
    }


    // 'Start' button handler
    private void startButtonClicked() {
        portName = view.getSelectedPort();

        if (portName == null) {
            view.displayError("Please select a port to connect to Arduino");
            return;
        }

        if (!connect()) {
            view.displayError("Can not connect to port " + portName);
            return;
        }

        view.toggleControlPaneForArduinoConnected(true);
    }

    // 'Stop' button handler
    private void stopButtonClicked() {
        stopSerialListener();
        closeConnection();
        view.toggleControlPaneForArduinoConnected(false);
    }

    // 'Load File' button handler
    private void loadFileButtonClicked() {
        JFileChooser fileChooser = new JFileChooser();
        File selectedFile;

        int returnVal = fileChooser.showOpenDialog(view.getContainer());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        } else {
            return;
        }

        modelIsProcessingNewReadings = true;
        view.enableLoadFileButton(false);

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

                List<Sample> samples = parseMessage(line);
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

            modelIsProcessingNewReadings = false;
            view.enableLoadFileButton(true);
            //Finished reading from file
            //Need to pause & wait for the process to render the last reading
            try {
                Thread.sleep(500);               
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            view.finalRender();
        })).start();
        
    }

    // 'Stream from Arduino' button handler
    private void streamFromArduinoButtonClicked() {
        // Disable the button to prevent this event
        // handler from being run multiple times.
        view.enableStreamButton(false);
        modelIsProcessingNewReadings = true;

        // stop any preexisting listener
        stopSerialListener();

        serialListener = new SerialListener((message) -> {
            List<Sample> samples = parseMessage(message);

            if (!samples.isEmpty()) {
                model.newSensorReading(samples.get(0));
            }
        });
        serialListener.start();
    }

    private void stopStreamingButtonClicked() {
        stopSerialListener();
        view.enableStreamButton(true);
        modelIsProcessingNewReadings = false;
    }

    // Clear canvases button handler
    private void clearCanvases() {
        view.clearCanvases();
    }
    
    // Save Canvases button handler
    private void saveCanvases() {
    	view.saveCanvases();
    } 

    // Apply button handler
    private void changeCanvases() {
        if (modelIsProcessingNewReadings) {
            view.displayError("Can't change rendering options while drawing happens.");
        }

        // Check if the user has selected different rendering options for the left canvas
        // and/or the right canvas
        if (view.userHasSelectedLeftCanvas() && view.userHasSelectedRightCanvas()) {
            view.displayTwoCanvases();
        } else if (view.userHasSelectedLeftCanvas() || view.userHasSelectedRightCanvas()){
            view.displayOneCanvas();
        } else {
            view.displayError("Select at least one rendering option");
        }

    }

    // -------------------------------------------------------------------------
    //      END OF EVENT LISTENERS
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
                view.enableStreamButton(true);
                view.displayError("Connection with Arduino was interrupted");
            }
        }
    }
}