import processing.core.PImage;
import java.util.List;

public interface Entity {
    String getId();
    void setImages(List<PImage> images); 
    void setPosition(Point p);
    Point getPosition();
    PImage getCurrentImage();
    String log();

}