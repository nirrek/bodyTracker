import javafx.scene.Node;
import javafx.scene.layout.VBox;


public class HistoryView {

	// The main container of the view
	private VBox container;
		
	public HistoryView() {
		container = new VBox(2);
	}
		
	public Node getNode() {
		return container;
	}
}
