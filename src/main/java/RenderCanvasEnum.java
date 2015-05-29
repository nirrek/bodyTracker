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

    public static RenderCanvasEnum getEnumForValue(String value) {
        switch (value) {
            case "2D Front View":
                return RenderCanvasEnum.Render2DFront;
            case "2D Side View":
                return RenderCanvasEnum.Render2DSide;
            default:
                return null;
        }
    }
}
