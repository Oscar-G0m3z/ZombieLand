import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import processing.core.PImage;
public class Dude_full implements Transform, Moblity, ExecuteActivity{

    /**
     * An entity that exists in the world. See EntityKind for the
     * different kinds of entities that exist.
     */
        private final String id;
        private Point position;
        private List<PImage> images;
        private int imageIndex;
        private final int resourceLimit;
    private final double actionPeriod;
        private final double animationPeriod;

        public String getId(){
            return id;
        }
        public void setPosition(Point point){
            this.position = point;
        }

        public Point getPosition(){
            return this.position;
        }

        public Dude_full(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
            this.id = id;
            this.position = position;
            this.images = images;
            this.imageIndex = 0;
            this.resourceLimit = resourceLimit;
            this.actionPeriod = actionPeriod;
            this.animationPeriod = animationPeriod;
        }
        public PImage getCurrentImage() {
            return images.get(imageIndex % images.size());
        }

        public void nextImage() {
            this.imageIndex = this.imageIndex + 1;
        }

        public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
            scheduler.scheduleEvent(this, Anime.createActivityAction(this, world, imageStore), this.actionPeriod);
            scheduler.scheduleEvent(this, Anime.createAnimationAction(this,0), this.animationPeriod);
        }

        public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
            if (Functions.adjacent(this.position, target.getPosition())) {
                return true;
            } else {
                Point nextPos = this.nextPositionDude(world, target.getPosition());
                if (!this.position.equals(nextPos)) {
                    world.moveEntity(scheduler, this, nextPos);
                }
                return false;
            }
        }

        public Point nextPositionDude(WorldModel world, Point destPos) {
            PathingStrategy strategy = new AStarPathingStrategy();

            List<Point> points = strategy.computePath(this.position, destPos,
                    p -> (world.withinBounds(p) && (!world.isOccupied(p))),
                    Functions::adjacent,// potential neighbors
                    PathingStrategy.CARDINAL_NEIGHBORS);

            if (points.isEmpty()){
                return this.position;
            }

            // exclude the start/end, and be in ascending order

            // return the first point form path
            // handle the case where the path is empty - stay where we are

//        int horiz = Integer.signum(destPos.x - this.position.x);
//        Point newPos = new Point(this.position.x + horiz, this.position.y);
//
//        if (horiz == 0 || world.isOccupied(newPos)  && Functions.getOccupancyCell(world, newPos).getClass() != House.class) {
//            int vert = Integer.signum(destPos.y - this.position.y);
//            newPos = new Point(this.position.x, this.position.y + vert);
//
//            if (vert == 0 || world.isOccupied(newPos)  && Functions.getOccupancyCell(world, newPos).getClass() != House.class) {
//                newPos = this.position;
//            }
//        }
            return points.get(0);
        }

        public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
            Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));

            if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
                this.transform(world, scheduler, imageStore);
            } else {
                scheduler.scheduleEvent(this, Anime.createActivityAction(this, world, imageStore), this.actionPeriod);
            }
        }

        public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
            Anime dude = Factory.createDudeNotFull(id, position, actionPeriod, animationPeriod, resourceLimit, images);

            WorldModel.removeEntity(world, scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        public void transform_Zombie(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
            Anime dude = Factory.createZombie(id, position, actionPeriod, 1, images);

            WorldModel.removeEntity(world, scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);
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

        public void setImages(List<PImage> images) {
            this.images = images;
        }

        public int getImageIndex() {
            return imageIndex;
        }


        public double getAnimationPeriod() {
            return animationPeriod;
        }

    }
