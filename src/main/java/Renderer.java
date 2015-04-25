import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import processing.core.PApplet;
import processing.serial.Serial;

/**
 * The Application's Controller (Or the Renderer Controller...).
 * Connects to the Arduino via serial USB. Fetch/Stream data from the Arduino,
 * and display it in an artistic manner after conversion by the Modeler.
 */
public class Renderer {
	
	private RendererView rendererView;
    private Modeler modeler;
    
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
    }
    
    private class ConnectButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			boolean connectionIsSuccessful = true;
        	String error = "";
        	
        	// TODO: ESTABLISH CONNECTION WITH ARDUINO
        	
        	if (connectionIsSuccessful) {
        		rendererView.toggleControlPaneForArduinoConnected(true);
        	}
        	rendererView.displayError(error);
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
}
