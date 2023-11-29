public interface Anime extends Entity{
    static Activity createActivityAction(ExecuteActivity entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }
    void nextImage(); // move later

    double getAnimationPeriod();
    static Animation createAnimationAction(Entity entity, int max){
        return new Animation(entity, max);
    }

    void scheduleActions(EventScheduler eventScheduler, WorldModel worldModel, ImageStore imageStore);
}
