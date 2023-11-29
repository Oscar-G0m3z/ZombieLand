public final class Animation implements Action{
    private final Entity entity;
    private final int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
        ((Anime) entity).nextImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, Anime.createAnimationAction(entity, Math.max(repeatCount - 1, 0)), ((Anime)entity).getAnimationPeriod());
        }
    }
}