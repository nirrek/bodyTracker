import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BodyTracker{

	public static void main(String[] args) {
		new BodyTracker();
	}


	/**
	 * This is the entry point of the application.
	 * The element that encapsulates the entire app is the container.
	 */
	public BodyTracker() {
		JFrame main = new JFrame();
		// Set title
		main.setTitle("Cloth Motion");
		// Set size
		setWindowSize(main);

		// Pass container view and model to the Renderer
		BodyTrackerContainer container = new BodyTrackerContainer(main.getContentPane());
		Modeler model = new Modeler();
		Renderer rendererController = new Renderer(model, container);

		// Clean up when window closes
		main.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				rendererController.unmount();
				System.exit(0);
			}
		});

		// TODO add css style sheet
		//String cssUrl = this.getClass().getResource("style.css").toExternalForm();
		//main.getStylesheets().add(cssUrl);

		main.setVisible(true);
	}

	/**
	 * Set the size of the specified frame. The size will be set to 1920x1080
	 * unless the usable screen size is smaller than this. If the screen size
	 * is smaller than 1920x1080, the frame will be set equal to the effective
	 * screen size.
	 * @param frame The frame to set the size of
	 */
	private void setWindowSize(JFrame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = Math.min(1920, screenSize.width);
		int height = Math.min(1080, screenSize.width);
		frame.setSize(width, height);
	}

}
