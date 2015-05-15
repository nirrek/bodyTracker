import java.io.*;
import java.util.*;

import gnu.io.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



/**
 * The Application's Controller (Or the Renderer Controller...).
 * Connects to the Arduino via serial USB. Fetch/Stream data from the Arduino,
 * and display it in an artistic manner after conversion by the Modeler.
 */
public class Renderer {
	
	private RendererView rendererView;
    private Modeler modeler;
    SerialPort serialPort;
    private static final int TIME_OUT = 2000;
	private static final int DATA_RATE = 9600;
	private CommPortIdentifier portIdentifier;
	private InputStream in;
      
    private String portName = "/dev/cu.usbmodem1451";  //Lisa's iMac port.
    
    public Renderer(Stage primaryStage, Modeler model, RendererView view) {
    	rendererView = view;
    	modeler = model;
    	
    	rendererView.addConnectionButtonsHandler(new ConnectButtonHandler(), 
    			new CloseConnectionButtonHandler());
    	rendererView.addFetchStreamButtonsHandler(new FetchButtonHandler(), 
    			new StreamButtonHandler());
    	
    	// We might want to make sure the connection with arduino is closed
    	// before user closes the application window
    	primaryStage.setOnCloseRequest(new CloseWindowHandler());
    	
    	getAvailableSerialPorts();
    }


    /**
     * This function is called when user press the "Connect" button.
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
                 connect();
             } catch (Exception exception) {
            	 rendererView.displayError(exception.toString());
                 connectionIsSuccessful = false;
             }
        	
        	if (connectionIsSuccessful) {
        		rendererView.toggleControlPaneForArduinoConnected(true);
        	}
		}
    	
    }

    private class CloseConnectionButtonHandler implements EventHandler<ActionEvent> {

		//@Override
		public void handle(ActionEvent arg0) {
        	closeConnection();
            rendererView.toggleControlPaneForArduinoConnected(false);
		}
    	
    }
    
    private class FetchButtonHandler implements EventHandler<ActionEvent> {

		//@Override
		public void handle(ActionEvent arg0) {
			String error = "";
			
			// TODO: FETCH DATA STORED IN ARDUINO
			
			rendererView.displayError(error);
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
	
			rendererView.displayError(error);
		}	
    }

    /**
     * The handle() method is called when user closes the application window.
     * Closes the connection with the Arduino
     */
    private class CloseWindowHandler implements EventHandler<WindowEvent> {

		/* Close connection with arduino */
		public void handle(WindowEvent e) {
	    	closeConnection();
		}

		
	}


    /**
     * @return    A HashSet containing the CommPortIdentifier for all 
     * 			  serial ports that are not currently being used.
     */
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<CommPortIdentifier>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
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
