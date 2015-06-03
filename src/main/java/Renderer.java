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

        // Add model listener
        model.addListener(Modeler.NEW_SAMPLE, p -> modelAddedNewSample());

        // Add button listeners
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

    // -------------------------------------------------------------------------
    //      HELPER METHODS
    // -------------------------------------------------------------------------

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
     * Opens a modal dialog window allowing user to select a file or a folder
     * on the computer.
     *
     * @param type: 0 if user needs to select a folder, 1 if user needs to
     *            select a file
     *
     * @return The selected file/folder, or null if the user haven't selected anything
     */
    private File selectFile(int type) {
        File selectedFile = null;

        JFileChooser chooser = new JFileChooser();

        if (type == 0) { // select a folder
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
        }

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
        }

        return selectedFile;
    }

    /**
     * Fetches a list of all available serial ports.
     * Once the Arduino is connected, the port it is connected to will appear in
     * this list.
     *
     * This function is called when application is launched, and when user press
     * the 'Refresh' button.
     *
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
     * Method called every time a button is clicked.
     *
     * Clear the error logs and reset the count value to 0.
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
     * TODO: Lisa
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
    		}	else { // regular canvas
    			view.getCanvas().drawArm(rightArm, "front");
    		}
    	}

    }

    // -------------------------------------------------------------------------
    //      VIEW EVENT LISTENERS
    // -------------------------------------------------------------------------

    /**
     * Method to handle when the refresh (available ports) button is clicked.
     * The method that retrieves the available ports is called in a new Thread.
     *
     * Display the ports currently available.
     */
    private void refreshButtonClicked() {
        resetAfterButtonClicked();

        (new Thread(() -> {
            updateUIDisplaySerialPortsAvailable();
        })).start();
    }


    /**
     * Method to handle when the 'Connect' button is clicked
     *
     * Establish a connection with the Arduino. Display appropriate error message
     * if a connection can't or shouldn't be established just now.
     */
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

        // Update the application and the buttons state
        serialConnected = true;
        updateUIButtons();
    }

    /**
     * Method to handle when the 'Close Connection' button is clicked
     *
     * Stop the serial listener and close the serial connection.
     */
    private void closeConnectionButtonClicked() {
        resetAfterButtonClicked();

        stopSerialListener();
        closeConnection();

        // Update the application and the buttons state
        serialConnected = false;
        updateUIButtons();
    }

    /**
     * Method to handle when the 'Load File' button is clicked
     *
     * If a rendering style has been selected, it opens a modal dialog window
     * for the user to select a file to read.
     * Opens a new thread to process the values in the files and render the
     * positions of the arm using the model listener.
     */
    private void loadFileButtonClicked() {

        resetAfterButtonClicked();

        if (view.getSelectedCanvas().equals("None")) {
            view.displayError("You must select a rendering style");
            return;
        }

        File selectedFile = selectFile(1);
        if (selectedFile == null) return;

        // Update the application and the buttons state
        modelIsProcessingReadings = true;
        updateUIButtons();

        // Spawn a new thread for reading from the file
        (new FileLoader((line) -> {
            List<Sample> samples = Sample.parseMessage(line);

            if (!samples.isEmpty()) {
                //only process the samples from the bNo
                if (samples.get(0).getID() == 2) {
                    model.newSensorReading(samples.get(0));
                }
            }
        }, selectedFile)).start();
    }

    /**
     * Method to handle when the 'Stream From ClothMotion' button is clicked
     *
     * If a rendering style has been selected, it starts a new Serial Listener (new thread)
     * to process the values sent from the Arduino using the model listener.
     */
    private void streamFromArduinoButtonClicked() {

        resetAfterButtonClicked();

        if (view.getSelectedCanvas().equals("None")) {
            view.displayError("You must select a rendering style");
            return;
        }

        // stop any preexisting listener
        stopSerialListener();

        // Update the application and the buttons state
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

    /**
     * Method to handle when the 'Stop Streaming' button is clicked.
     *
     * Close the serial listener used to process values received from the Arduino.
     */
    private void stopStreamingButtonClicked() {
        resetAfterButtonClicked();

        // Creates the final render in high quality for the digital 3D canvas
        view.finalRender();

        stopSerialListener();

        // Update the application and the buttons state
        modelIsProcessingReadings = false;
        isStreaming = false;
        updateUIButtons();
    }

    /**
     * Method to handle when the 'Clear Canvas' button is clicked.
     *
     * Clear the canvas currently shown on screen.
     */
    private void clearCanvases() {
        resetAfterButtonClicked();

        // Check if the user has selected a rendering style
        if (!view.getSelectedCanvas().equals("None")) {
            view.clearCanvas();
        } else{
            view.displayError("You must select a rendering style");
        }
    }

    /**
     * Method to handle when the 'Save Canvas' button is clicked.
     *
     * Save a JPG of the canvas currently shown on screen. The first time
     * the button is pressed, a modal dialog window appears for the user to choose
     * the destination folder. The path is then saved and will be reused each time
     * the user wants to save a file afterward.
     */
    private void saveCanvases() {
        resetAfterButtonClicked();

        // Check if the user has selected a rendering style
        if (view.getSelectedCanvas().equals("None")) {
            view.displayError("You must select a rendering style");
            return;
        }

        // Get the destination folder if it is the first time user is saving a canvas
        if (destinationPathSavedFile == null) {
            destinationPathSavedFile = selectFile(0).toString();
        }

        view.saveCanvas(destinationPathSavedFile);
    }

    /**
     * Method to handle when the 'Apply' (change rendering style) button is clicked.
     *
     * Change the canvas displayed on screen to the one selected by the user.
     */
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
                    // that the Swing GUI is NOT THREADSAFE.
                    SwingUtilities.invokeLater(() -> callback.accept(message));
                    if (Thread.interrupted()) return;
                }
            } catch (IOException e) {
                // Update application and button state
                serialConnected = false;
                modelIsProcessingReadings = false;
                isStreaming = false;
                updateUIButtons();

                view.displayError("Connection with Arduino was interrupted");
            }
        }
    }

    /**
     * FileLoader thread reads new messages from the Serial and dispatches
     * them to the provided callback.
     */
    private class FileLoader extends Thread {
        // Callback to be executed when a new message arrives.
        private Consumer<String> callback;
        private Scanner scanner = null;
        /**
         * Instantiates a new FileLoader thread. The callback provided
         * will be invoked every time a new line is read.
         *
         * @param callback The callback to be invoked when a line has been processed.
         *                 The callback is passed a single message string argument.
         */
        FileLoader(Consumer<String> callback, File selectedFile) {
            this.callback = callback;
            try {
                this.scanner = new Scanner(selectedFile);
            } catch (FileNotFoundException e) { e.printStackTrace(); }
        }

        @Override
        public void run() {
            if (scanner == null) return;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("$")) continue; // message boundary

                // Add the callback invokation to the an event queue on
                // the application thread. This is required due to the fact
                // that the Swing GUI is NOT THREADSAFE.
                SwingUtilities.invokeLater(() -> callback.accept(line));
                if (Thread.interrupted()) break;

                try {
                    Thread.sleep(100); // simulate events coming in
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Finished reading from file
            //Need to pause & wait for the process to render the last reading
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // Creates the final render in high quality for the digital 3D canvas
            view.finalRender();

            // Update the application and the buttons state
            modelIsProcessingReadings = false;
            updateUIButtons();
        }
    }
}
