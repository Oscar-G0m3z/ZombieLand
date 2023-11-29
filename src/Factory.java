import processing.core.PImage;

import java.util.List;

public class Factory {
    public static House createHouse(String id, Point position, List<PImage> images) {
        return new House(EntityKind.HOUSE, id, position, images);
    }
    public static Obstacle createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, animationPeriod);
    }

    public static Tree createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, actionPeriod, animationPeriod, health);
    }

    public static Stump createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Sapling createSapling(String id, Point position, List<PImage> images, int health) {
        return new Sapling(id, position, images, health, 1.0000, 1.0000, 5);
    }

    public static Fairy createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public static Dude_not_full createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Dude_not_full(id, position, images, resourceLimit,0, actionPeriod, animationPeriod);
    }
    public static Dude_full createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Dude_full(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }
    public static Zombie createZombie(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Zombie(id, position, images, actionPeriod, animationPeriod);
    }
    public static new_Dudes create_New_Dude(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new new_Dudes(id, position, images, actionPeriod, animationPeriod);
    }
}
