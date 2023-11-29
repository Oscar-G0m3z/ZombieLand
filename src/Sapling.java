import java.util.List;
import processing.core.PImage;
public class Sapling implements Transform, ExecuteActivity, Anime, Plant{

    /**
     * An entity that exists in the world. See EntityKind for the
     * different kinds of entities that exist.
     */
        private final String id;
        private Point position;
        private final List<PImage> images;
        private int imageIndex;
        private int health;
        private final double actionPeriod;
        private final double animationPeriod;
        private final int healthLimit;

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

        public Sapling(String id, Point position, List<PImage> images, int health, double actionPeriod, double animationPeriod, int healthLimit) {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
            this.health = health;
            this.actionPeriod = actionPeriod;
            this.animationPeriod = animationPeriod;
            this.healthLimit = healthLimit;
        }
        public PImage getCurrentImage() {
                return images.get(imageIndex % images.size());
        }

        public Animation createAnimationAction(int repeatCount) {
            return new Animation(this,  repeatCount);
        }

        public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
            if (this.health <= 0) {
                Entity stump = Factory.createStump(Functions.STUMP_KEY + "_" + this.id, this.position, imageStore.getImageList(Functions.STUMP_KEY));

                WorldModel.removeEntity(world, scheduler, this);

                world.addEntity(stump);

                return true;
            } else if (this.health >= this.healthLimit) {
                Entity tree = Factory.createTree(Functions.TREE_KEY + "_" + this.id, this.position, Functions.getNumFromRange(Functions.TREE_ACTION_MAX, Functions.TREE_ACTION_MIN), Functions.getNumFromRange(Functions.TREE_ANIMATION_MAX, Functions.TREE_ANIMATION_MIN), Functions.getIntFromRange(Functions.TREE_HEALTH_MAX, Functions.TREE_HEALTH_MIN), imageStore.getImageList(Functions.TREE_KEY));

                WorldModel.removeEntity(world, scheduler, this);

                world.addEntity(tree);
                ((Anime)tree).scheduleActions(scheduler, world, imageStore);

                return true;
            }
            return false;
        }

        public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
            this.health++;
            if (!transform(world, scheduler, imageStore)) {
                scheduler.scheduleEvent(this, Anime.createActivityAction(this, world, imageStore), this.actionPeriod);
//                scheduler.scheduleEvent(this, Functions.createActivityAction(this), this.actionPeriod);
            }
        }

        public void nextImage() {
            this.imageIndex = this.imageIndex + 1;
        }

        public double getAnimationPeriod() {
            return animationPeriod;
        }

        public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
            scheduler.scheduleEvent(this, Anime.createActivityAction(this, world, imageStore), this.actionPeriod);
            scheduler.scheduleEvent(this, Anime.createAnimationAction(this,0), this.animationPeriod);
    }


    /**
         * Helper method for testing. Preserve this functionality while refactoring.
         */
        public String log(){
            return this.id.isEmpty() ? null :
                    String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
        }


        public List<PImage> getImages() {
            return images;
        }


        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }
}
