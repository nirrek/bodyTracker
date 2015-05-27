import java.awt.*;

/**
 * Created by romainjulien on 27/05/2015.
 */
public class Controller {

    private RendeR canvas1;
    private RendeR canvas2;

    public Controller(Modeler model, Container container) {

        canvas1 = new RendeR();
        container.add(canvas1, BorderLayout.WEST);
        canvas1.init();

        canvas2 = new RendeR();
        container.add(canvas2, BorderLayout.CENTER);
        canvas2.init();

        ControlView controls = new ControlView();
        container.add(controls.getPanel(), BorderLayout.EAST);

        controls.addListener("stopLoop", event -> stopLoop());
        controls.addListener("startLoop", event -> startLoop());
    }

    public void stopLoop() {
        System.out.println("You clicked STOP");
        /*
        canvas1.stopLoop();
        canvas2.stopLoop();
        */
    }

    public void startLoop() {
        System.out.println("You clicked START");
        /*
        canvas1.startLoop();
        canvas2.startLoop();
        */
    }
}
