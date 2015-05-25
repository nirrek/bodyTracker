import gnu.io.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
	private RendererView view;
    private Modeler model;
    SerialPort serialPort;
    private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 115200;
	private CommPortIdentifier portIdentifier;
	private InputStream in;

    // The name of the serial port.
    private String portName;

    // Higher level serial wrapper by Kerrin
    private Serial serial;

    // The thread listening to inbound serial messages
    private Thread serialListener;

    // The stage that acts as the Window for the application. Certain operations
    // in JavaFX require this to be passed, which is why the controller needs
    // to maintain a pointer to him.
    private Stage stage;

    /**
     * Constructors a new renderer that is bound to the given model and view.
     * @param model The model in the MVC pattern (ostensibly)
     * @param view The view in the MVC pattern (ostensibly)
     */
    public Renderer(Stage stage, Modeler model, RendererView view) {
        this.stage = stage;
        this.model = model;
        this.view = view;

        model.addListener(Modeler.NEW_SAMPLE, p -> {
            System.out.println("new sample added to model");

            // Just doing one arm for the moment
            Arm leftArm = model.getNextSample().getLeftArm();
            view.renderLeftArm(leftArm);
        });

        view.addListener("loadFile", event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a datalog file");

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null) return;

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
                        Platform.runLater(() -> {
                            model.newSensorReading(samples.get(0));
                        });
                    }
                }
            })).start();
        });

        view.addListener("clearCanvases", event -> view.clearCanvases());

        view.addRefreshButtonHandler(new RefreshButtonHandler());
    	view.addConnectionButtonsHandler(new ConnectButtonHandler(),
    			new CloseConnectionButtonHandler());
    	view.addFetchStreamButtonsHandler(new FetchButtonHandler(),
    			new StreamButtonHandler());


        ArrayList<CommPortIdentifier> availablePorts = getAvailableSerialPorts();
        view.showAvailablePorts(availablePorts);
    }

    /**
     * Establishes a new serial connection using, and stores the new Serial
     * connection object on the instance.
     */
    private void connect() {
        if (this.serial != null) {
            // TODO close existing serial before establishing new connection.
        }

        this.serial = new Serial();
        this.serial.connect(this.portName, DATA_RATE);

        if (!this.serial.isConnected()) {
            // TODO handle connection failure
            System.err.println("Failed to connect");
        }
    }


    /**
     * Lifecycle method to be called before the Renderer is unmounted to
     * clean-up necessary state.
     */
    public void unmount() {
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
                // TODO use a real logging framework
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

    // -------------------------------------------------------------------------
    //      EVENT LISTENERS
    // -------------------------------------------------------------------------
    // 'Refresh' button handler
    private class RefreshButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent arg0) {
            ArrayList<CommPortIdentifier> availablePorts = getAvailableSerialPorts();
            view.showAvailablePorts(availablePorts);
        }
    }

    // 'Start' button handler
    private class ConnectButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent e) {

            portName = view.getSelectedPort();

            if (portName == null) {
                view.displayError("Please select a port to connect to Arduino");
                return;
            }

            connect();
            if (!serial.isConnected()) {
                view.displayError("Can not connect to port " + portName);
                return;
            }

            view.toggleControlPaneForArduinoConnected(true);
        }
    }

    // 'Stop' button handler
    private class CloseConnectionButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
            stopSerialListener();
            closeConnection();
            view.toggleControlPaneForArduinoConnected(false);
		}
    }

    // 'Fetch' button handler
    private class FetchButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			String error = "";

			// TODO: FETCH DATA STORED IN ARDUINO
			view.displayError(error);
		}
    }

    // 'Stream' button handler
    private class StreamButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            // TODO, should disable the button to prevent this event
            // handler from being run multiple times.

            stopSerialListener(); // stop any preexisting listener

            serialListener = new SerialListener((message) -> {
                List<Sample> samples = parseMessage(message);

                if (!samples.isEmpty()) {
                    model.newSensorReading(samples.get(0));
                }
            });
            serialListener.start();
        }
    }
    // -------------------------------------------------------------------------
    //      END OF EVENT HANDLERS
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
                    Platform.runLater(() -> callback.accept(message) );
                    if (Thread.interrupted()) return;
                }
            } catch (IOException e) {
                // Exit thread on IOException.
                // TODO figure out best way to handle this case.
                e.printStackTrace();
            }
        }
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
                    System.out.println("Port, "  + port.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  port.getName());
                    e.printStackTrace();
                }
            }
        }
        return availablePorts;
    }
}