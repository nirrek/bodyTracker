/**
 * Created by romainjulien on 28/05/2015.
 */
public enum RenderCanvasEnum {
    Render2DFront("2D Front View"),
    Render2DSide("2D Side View");

    private String value;

    private RenderCanvasEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
