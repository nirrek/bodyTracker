import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class BodyTracker extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * This is the entry point for a JavaFX application. The element that
	 * encapsulates the entire app is the stage.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		// The model for the renderer
		Modeler modeler = new Modeler();
		// The view for the renderer
		RendererView rendererView = new RendererView(modeler);
		// TODO : REMOVE OR IMPLEMENT
		HistoryView historyView = new HistoryView();

		ScrollPane rootNode = new ScrollPane();
		rootNode.getStyleClass().add("Root");

		TabPane tabPane = createTabs(rendererView, historyView);
		rootNode.setContent(tabPane);

		Scene scene = new Scene(rootNode);
		String cssUrl = this.getClass().getResource("style.css").toExternalForm();
		scene.getStylesheets().add(cssUrl);

		// The controller for the renderer (needs to be initialize here as it is initial view)
        Renderer rendererController = new Renderer(stage, modeler, rendererView);

        // Configure stage
		setStageSize(stage);
        stage.setOnCloseRequest(event -> {
            // Unmount the Renderer before app closure.
            // TODO track the actual current view.
            rendererController.unmount();
        });
        stage.setTitle("ClothMotion");
        stage.setScene(scene);
		stage.show();
	}

	/**
	 * Set the size of the specified stage. The size will be set to 1920x1080
	 * unless the usable screen size is smaller than this. If the screen size
	 * is smaller than 1920x1080, the stage will be set equal to screen size.
	 * @param stage The stage to set the size of
	 */
	private void setStageSize(Stage stage) {
		Rectangle2D screen = Screen.getPrimary().getVisualBounds();
		double width = Math.min(1920, screen.getWidth());
		double height = Math.min(1080, screen.getHeight());
		stage.setWidth(width);
		stage.setHeight(height);
	}

	// Can be useful to have a separate tab to review history/previous drawings
	// A good place to archive screenshots etc...
	// TODO : REMOVE OR IMPLEMENT HISTORY VIEW
	private TabPane createTabs(RendererView rendererView, HistoryView historyView) {
		TabPane tabPane = new TabPane();
		tabPane.getStyleClass().add("TabPane");
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

		Tab rendererTab = new Tab("Renderer");
		rendererTab.setContent(rendererView.getNode());
		Tab historyTab = new Tab("History");
		historyTab.setContent(historyView.getNode());

		tabPane.getTabs().addAll(rendererTab, historyTab);
		return tabPane;
	}

}
