import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import processing.core.PImage;
public class new_Dudes implements Transform, Moblity, ExecuteActivity{

    /**
     * An entity that exists in the world. See EntityKind for the
     * different kinds of entities that exist.
     */
    private final String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceCount;
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

    public new_Dudes(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }
    public PImage getCurrentImage() {
        return images.get(imageIndex % images.size());
    }

    public Animation createAnimationAction(int repeatCount) {
        return new Animation(this, repeatCount);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        House t = (House) target;
        if (Functions.adjacent(this.position, t.getPosition())) {
            return true;
        } else {
            Point nextPos = nextPositionDude(world, t.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fullTarget = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));
// need to make disappear now
        if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
            world.removeEntityAt(this.position);
        }
        else {
        scheduler.scheduleEvent(this, Anime.createActivityAction(this, world, imageStore), this.actionPeriod);
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


    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
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

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImages(List<PImage> images) {
        this.images = images;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public void scheduleActions(EventScheduler eventScheduler, WorldModel worldModel, ImageStore imageStore) {
        eventScheduler.scheduleEvent(this, Anime.createActivityAction(this, worldModel, imageStore), this.actionPeriod);
        eventScheduler.scheduleEvent(this, this.createAnimationAction(0), this.animationPeriod);
//            eventScheduler.scheduleEvent(this, this.createAnimationAction(0), this.animationPeriod);
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return false;
    }
}
