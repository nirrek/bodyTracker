import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;


public class Test1 {

	public Test1 () {
		JFrame main = new JFrame();
		// Set title
		main.setTitle("Cloth Motion");
		// Set size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = Math.min(1920, screenSize.width);
		int height = Math.min(1080, screenSize.width);
		main.setSize(width, height);


		// Pass container view and model to the Renderer
		Container container = main.getContentPane();
		Modeler model = new Modeler();
		Controller controller = new Controller(model, container);

		// Clean up when window closes
		main.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
				//applet.destroy();
			}
		});

		// TODO add css style sheet
		//String cssUrl = this.getClass().getResource("style.css").toExternalForm();
		//main.getStylesheets().add(cssUrl);

		main.setVisible(true);
}


	public static void main(String args[]) {
		Test1 test = new Test1();
	}

}