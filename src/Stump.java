import java.util.List;
import processing.core.PImage;
public class Stump implements Entity{
    /**
     * An entity that exists in the world. See EntityKind for the
     * different kinds of entities that exist.
     */
        private final String id;
        private Point position;
        private final List<PImage> images;
        private final int imageIndex;

        public  PImage getCurrentImage(){
            return images.get(imageIndex % images.size());
        }
        public String getId(){
            return id;
        }

    @Override
    public void setImages(List<PImage> images) {

    }

    public void setPosition(Point point){
            this.position = point;
        }



        public Point getPosition(){
            return this.position;
        }

        public Stump(String id, Point position, List<PImage> images) {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
        }
            /**
         * Helper method for testing. Preserve this functionality while refactoring.
         */
        public String log(){
            return this.id.isEmpty() ? null :
                    String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
        }

    }
