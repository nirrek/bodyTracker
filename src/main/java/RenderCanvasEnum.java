/**
 * Enumerate all different rendering style that have been implemented.
 * The enum name correspond to the name of the class, it's corresponding value
 * is the string used when presenting to the user.
 *
 * The Enum 'None -> "None"' is added to facilitate the coding of the Renderer and
 * BodyTrackerContainer classes.
 */
public enum RenderCanvasEnum {
    None("None"),
    FrontView2D("2D Front View"),
    SideView2D("2D Side View"),
    Digital3DSketch("Digital 3D Sketch"),
    Digital2DSketch("Digital 2D Sketch"),
    RenderGenerativeArt("Render Generative Art");

    private String value;

    /**
     * The constructor of the enum.
     * @param value: The String associated to this enum
     */
    private RenderCanvasEnum(String value) {
        this.value = value;
    }

    /**
     * @return The value associated to the enum
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Given the value (String), this method returns the corresponding Enum.
     * @param value: The value of the enum
     * @return The corresponding enum
     */
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
            case "Render Generative Art":
            	return RenderCanvasEnum.RenderGenerativeArt;
            default:
                return RenderCanvasEnum.None;
        }
    }
}
