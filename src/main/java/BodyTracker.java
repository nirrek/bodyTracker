import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import processing.core.PApplet;

import javax.swing.*;


public class BodyTracker extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {


		// The model for the renderer
		Modeler modeler = new Modeler();
		// The view for the renderer
		RendererView rendererView = new RendererView(modeler);
		// TODO : REMOVE OR IMPLEMENT
		HistoryView historyView = new HistoryView();

		VBox layout = new VBox();
		createTabs(layout, rendererView, historyView);

		Group root = new Group();
		Scene scene = new Scene(root, Color.WHITE);
		root.getChildren().addAll(layout);

        // The controller for the renderer (needs to be initialize here as it is initial view)
        Renderer rendererController = new Renderer(modeler, rendererView);

        // Configure scene
        stage.setOnCloseRequest(event -> {
            // Unmount the Renderer before app closure.
            // TODO track the actual current view.
            rendererController.unmount();
        });
        stage.setTitle("ClothMotion");
        stage.setScene(scene);
		stage.show();
	}

	// Can be useful to have a separate tab to review history/previous drawings
	// A good place to archive screenshots etc...
	// TODO : REMOVE OR IMPLEMENT HISTORY VIEW
	private void createTabs(VBox layout, RendererView rendererView, HistoryView historyView) {
		TabPane tabs = new TabPane();
        Tab rendererTab = new Tab("Renderer");
        rendererTab.setContent(rendererView.getNode());
		Tab historyTab = new Tab("History");
		historyTab.setContent(historyView.getNode());
		tabs.getTabs().addAll(rendererTab, historyTab);
        tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        layout.getChildren().addAll(tabs);
	}

}
