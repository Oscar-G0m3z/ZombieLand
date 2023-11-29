import java.util.List;
import processing.core.PImage;
public class Obstacle implements Anime{

    /**
     * An entity that exists in the world. See EntityKind for the
     * different kinds of entities that exist.
     */
        private final String id;
        private Point position;
        private final List<PImage> images;
        private int imageIndex;
        private double animationPeriod;


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


        public Obstacle(String id, Point position, List<PImage> images, double animationPeriod) {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
            this.animationPeriod = animationPeriod;

        }
        public PImage getCurrentImage() {
            return images.get(imageIndex % images.size());
        }

        public void nextImage() {
            this.imageIndex = this.imageIndex + 1;
        }

        public double getAnimationPeriod() {
            return animationPeriod;
        }

        public void scheduleActions(EventScheduler eventScheduler, WorldModel worldModel, ImageStore imageStore) {
            eventScheduler.scheduleEvent(this, Anime.createAnimationAction(this, 0), animationPeriod);
//            eventScheduler.scheduleEvent(this, Anime.createAnimationAction(this, 0), animationPeriod);
        }

    /**
         * Helper method for testing. Preserve this functionality while refactoring.
         */
        public String log(){
            return this.id.isEmpty() ? null :
                    String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
        }

        public int getImageIndex() {
            return imageIndex;
        }

//        public void executeAction(EventScheduler scheduler) {
//
//        }
}
