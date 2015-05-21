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
	private static final int DATA_RATE = 9600;
	private CommPortIdentifier portIdentifier;
	private InputStream in;

    // TODO: Remove if everything works correctly
    //private String portName = "/dev/cu.usbmodem1451";  //Lisa's iMac port.
    private String portName;  //Lisa's iMac port.

    public Renderer(Modeler model, RendererView view) {
        this.model = model;
        this.view = view;


    	view.addConnectionButtonsHandler(new ConnectButtonHandler(),
    			new CloseConnectionButtonHandler());
    	view.addFetchStreamButtonsHandler(new FetchButtonHandler(),
    			new StreamButtonHandler());

        ArrayList<CommPortIdentifier> portsInUse = getAvailableSerialPorts();
        view.showPortsInUse(portsInUse);
    }

    /**
     * This function is called when user press the "Start" button.
     * @throws Exception
     */
    private void connect() throws Exception, NoSuchPortException {
        portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),
                    TIME_OUT);
            if ( commPort instanceof SerialPort ) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(DATA_RATE,SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialWriter(out))).start();
            }
            else {
                System.out.println("Error: Only serial ports are handled by "
                        + "this example.");
            }
        }
    }

    /**
     * Lifecycle method to be called before the Renderer is unmounted to
     * clean-up necessary state.
     */
    public void unmount() {
        closeConnection();
    }

    private void closeConnection() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * This function is called when user press the "Stream" button
     * @throws Exception
     */
    private void listenForInput() throws Exception{
        in = serialPort.getInputStream();
        serialPort.addEventListener(new SerialReader(in));
        serialPort.notifyOnDataAvailable(true);
    }


    /////////////////////////////////////////////////////////////////////////
    ////////////// COMMUNICATION TO/FROM ARDUINO ////////////////////////////
    /////////////////////////////////////////////////////////////////////////

    /**
     * Handles the input coming from the serial port. A new line character
     * is treated as the end of a block in this example.
     */
    public static class SerialReader implements SerialPortEventListener {
        private InputStream in;
        private byte[] buffer = new byte[1024];


        public SerialReader ( InputStream in ) {
            this.in = in;
        }

        public void serialEvent(SerialPortEvent arg0) {
            int data;

            try {
                int len = 0;
                while ( ( data = in.read()) > -1 ) {
                    if ( data == '\n' ) {
                        break;
                    }
                    buffer[len++] = (byte) data;
                }
                System.out.print(new String(buffer,0,len));

            } catch ( IOException e ) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * Open a new thread. Sends message(s) to the Arduino
     */
    public static class SerialWriter implements Runnable {
        OutputStream out;

        public SerialWriter ( OutputStream out ) {
            this.out = out;
        }

        public void run () {
            try {
                int c = 0;
                while ( ( c = System.in.read()) > -1 ) {
                    this.out.write(c);
                }

            } catch ( IOException e ) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////
    /////////////////////////// BUTTONS LISTENER /////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    private class ConnectButtonHandler implements EventHandler<ActionEvent> {

		//@Override
		public void handle(ActionEvent e) {
			boolean connectionIsSuccessful = true;

        	 try {
                 portName = view.getSelectedPort();
                 if ((portName = view.getSelectedPort()) != null) {
                     connect();
                 } else {
                     view.displayError("Please select a port to connect to Arduino");
                     connectionIsSuccessful = false;
                 }
             } catch (Exception exception) {
                 view.displayError("Can not connect to port " + portName);
                 connectionIsSuccessful = false;
             }

        	if (connectionIsSuccessful) {
        		view.toggleControlPaneForArduinoConnected(true);
        	}
		}

    }

    private class CloseConnectionButtonHandler implements EventHandler<ActionEvent> {

		//@Override
		public void handle(ActionEvent arg0) {
        	closeConnection();
            view.toggleControlPaneForArduinoConnected(false);
		}

    }

    private class FetchButtonHandler implements EventHandler<ActionEvent> {

		//@Override
		public void handle(ActionEvent arg0) {
			String error = "";

			// TODO: FETCH DATA STORED IN ARDUINO

			view.displayError(error);
		}

    }


    private class StreamButtonHandler implements EventHandler<ActionEvent> {

        //@Override
		public void handle(ActionEvent arg0) {
		    try {
				listenForInput();
			} catch (Exception e) {
				e.printStackTrace();
			}

			String error = "";

			Thread t=new Thread() {
			public void run() {
				//listens to input for 1000 seconds & prints to console.
				try {
					Thread.sleep(1000000);
				} catch (InterruptedException ie)
				{
				}
			}
		};

			t.start();

			view.displayError(error);
		}
    }


    /**
     * @return    An ArrayList containing the CommPortIdentifier for all
     * 			  serial ports that are currently being used.
     */
    public static ArrayList<CommPortIdentifier> getAvailableSerialPorts() {
        ArrayList<CommPortIdentifier> h = new ArrayList<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                } catch (PortInUseException e) {
                    h.add(com);
                    System.out.println("Port, "  + com.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }
}
