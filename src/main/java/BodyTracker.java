import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class BodyTracker extends Application {
    	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Body Tracker");
		
		// The model for the renderer
		Modeler modeler = new Modeler();
		// The view for the renderer
		RendererView rendererView = new RendererView(modeler);
		// TODO : REMOVE OR IMPLEMENT
		HistoryView historyView = new HistoryView();
		
		VBox layout = new VBox();
		createTabs(layout, rendererView, historyView);
		
        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        
        // The controller for the renderer (needs to be initialize here as it is initial view)
        new Renderer(stage, modeler, rendererView);
        
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
