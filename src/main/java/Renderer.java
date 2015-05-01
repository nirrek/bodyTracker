import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashSet;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * The Application's Controller (Or the Renderer Controller...).
 * Connects to the Arduino via serial USB. Fetch/Stream data from the Arduino,
 * and display it in an artistic manner after conversion by the Modeler.
 */
public class Renderer {
	
	private RendererView rendererView;
    private Modeler modeler;
      
    private String portName = "COM3";
    
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
    
    private void connect() throws Exception {
    	CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        
    	if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort ) {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();
                               
                (new Thread(new SerialWriter(out))).start();
                
                serialPort.addEventListener(new SerialReader(in));
                serialPort.notifyOnDataAvailable(true);

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
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
     * Open a new thread. sends message to the Arduino
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
    
    private class ConnectButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			boolean connectionIsSuccessful = true;

        	 try {
                 connect();
             } catch (Exception exception) {
            	 exception.printStackTrace();
            	 rendererView.displayError(exception.getMessage());
             }
        	 
            
        	// TODO: ESTABLISH CONNECTION WITH ARDUINO
        	
        	if (connectionIsSuccessful) {
        		rendererView.toggleControlPaneForArduinoConnected(true);
        	}
		}
    	
    }

    private class CloseConnectionButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			boolean closeConnectionIsSuccessful = true;
        	String error = "";
        	
        	// TODO: CLOSE CONNECTION WITH ARDUINO
        	
        	if (closeConnectionIsSuccessful) {
        		rendererView.toggleControlPaneForArduinoConnected(false);
        	}
        	rendererView.displayError(error);
		}
    	
    }
    
    private class FetchButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			String error = "";
			
			// TODO: FETCH DATA STORED IN ARDUINO
			
			rendererView.displayError(error);
		}
    	
    }
    
    private class StreamButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg0) {
			String error = "";
			
			// TODO: STREAM DATA FROM ARDUINO
			
			rendererView.displayError(error);
		}
    	
    }
    
    private class CloseWindowHandler implements EventHandler<WindowEvent> {

		@Override
		public void handle(WindowEvent e) {
			// TODO: close Arduino connection
		}

		
	}
    
    /**
     * @return    A HashSet containing the CommPortIdentifier for all serial ports that are not currently being used.
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
