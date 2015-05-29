/**
 * Created by romainjulien on 28/05/2015.
 */
public enum RenderCanvasEnum {
    None("None"),
    FrontView2D("2D Front View"),
    SideView2D("2D Side View"),
    Digital3DSketch("Digital 3D Sketch"),
    Digital2DSketch("Digital 2D Sketch");

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
                return RenderCanvasEnum.FrontView2D;
            case "2D Side View":
                return RenderCanvasEnum.SideView2D;
            case "Digital 3D Sketch":
                return RenderCanvasEnum.Digital3DSketch;
            case "Digital 2D Sketch":
                return RenderCanvasEnum.Digital2DSketch;
            default:
                return RenderCanvasEnum.None;
        }
    }
}
