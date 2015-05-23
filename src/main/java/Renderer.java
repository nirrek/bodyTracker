import gnu.io.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

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

    /**
     * Constructors a new renderer that is bound to the given model and view.
     * @param model The model in the MVC pattern (ostensibly)
     * @param view The view in the MVC pattern (ostensibly)
     */
    public Renderer(Modeler model, RendererView view) {
        this.model = model;
        this.view = view;

    	view.addConnectionButtonsHandler(new ConnectButtonHandler(),
    			new CloseConnectionButtonHandler());
    	view.addFetchStreamButtonsHandler(new FetchButtonHandler(),
    			new StreamButtonHandler());

        // Kerrin: Why were we showing inUse ports in the dropdown??????
//        ArrayList<CommPortIdentifier> portsInUse = getUnavailableSerialPorts();
//        view.showPortsInUse(portsInUse);

        ArrayList<CommPortIdentifier> portsInUse = getAvailableSerialPorts();
        view.showAvailablePorts(portsInUse);
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

        // TODO. Don't brute force stop the thread. Have the thread periodically
        // check if it should close itself.
        serialListener.stop();
    }


    // -------------------------------------------------------------------------
    //      EVENT LISTENERS
    // -------------------------------------------------------------------------
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

    private class CloseConnectionButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
            stopSerialListener();
            closeConnection();
            view.toggleControlPaneForArduinoConnected(false);
		}
    }

    private class FetchButtonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent arg0) {
			String error = "";

			// TODO: FETCH DATA STORED IN ARDUINO
			view.displayError(error);
		}
    }


    // -------------------------------------------------------------------------
    //      HELPER METHODS
    // -------------------------------------------------------------------------
    private class StreamButtonHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            // TODO, should disable the button to prevent this event
            // handler from being run multiple times.

            stopSerialListener(); // stop any preexisting listener

            serialListener = createSerialListenerThread(serial);
            serialListener.start();
        }
    }

    // Create a thread that will listen for inbound messages on the
    // provided serial.
    private Thread createSerialListenerThread(Serial serial) {
        return new Thread() {
            public void run() {
                String message = null;

                // TODO decide how we want to be able to cancel this thread
                try {
                    while (true) {
                        message = serial.getNextMessage();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    // TODO decide how we want to handle this.
                    // Printstacktrace, and exit thread.
                    e.printStackTrace();
                }
            }
        };
    }


    // Kerrin: Why are we wishing to fetch unavailable ports?
    /**
     * @return    An ArrayList containing the CommPortIdentifier for all
     * 			  serial ports that are currently being used.
     */
    public static ArrayList<CommPortIdentifier> getUnavailableSerialPorts() {
        ArrayList<CommPortIdentifier> unavailablePorts = new ArrayList<>();
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
            if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    CommPort thePort = port.open("CommUtil", 50);
                    thePort.close();
                } catch (PortInUseException e) {
                    unavailablePorts.add(port);
                    System.out.println("Port, "  + port.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  port.getName());
                    e.printStackTrace();
                }
            }
        }
        return unavailablePorts;
    }

    /**
     * Fetches a list of all available serial ports.
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
