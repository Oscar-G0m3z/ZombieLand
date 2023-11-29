import java.util.List;
import processing.core.PImage;

public class House implements Entity{
    private final String id;
    private Point position;
    private final List<PImage> images;
    private final int imageIndex;

    public String getId() {
        return id;
    }

    @Override
    public void setImages(List<PImage> images) {

    }

    public void setPosition(Point point) {
        this.position = point;
    }

    public Point getPosition() {
        return position;
    }

    public PImage getCurrentImage() {
        return images.get(imageIndex % images.size());
    }

    public String log() {
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public House(EntityKind kind, String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }
}

