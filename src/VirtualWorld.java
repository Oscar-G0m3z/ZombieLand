import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final String CLICKED_IMAGE = "pixil-frame-0.png";
    private static final String ENT_IMAGE = "zombie1.png";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;


    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        for (Entity entity : world.getEntities()){
            if (entity instanceof Anime){
            ((Anime)entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    static final Function<Point, Stream<Point>> RANGE =
            point ->
                    Stream.<Point>builder()
                            .add(new Point(point.x, point.y))
                            .add(new Point(point.x + 1, point.y - 1))
                            .add(new Point(point.x -1, point.y + 1))
                            .add(new Point(point.x, point.y - 1))
                            .add(new Point(point.x, point.y + 1))
                            .add(new Point(point.x - 1, point.y - 1))
                            .add(new Point(point.x + 1, point.y + 1))
                            .add(new Point(point.x - 1, point.y))
                            .add(new Point(point.x + 1, point.y))
                            .build();

    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);
        List<PImage> newDudeImage = imageStore.getImageList(Functions.NEW_DUDE_KEY);
        new_Dudes newDudes = Factory.create_New_Dude(Functions.NEW_DUDE_KEY, pressed, 1, 3, newDudeImage);
        world.addEntity(newDudes);
        ((Anime)newDudes).scheduleActions(scheduler, world, imageStore);

        // blood background
        PImage new_image = loadImage("images/pixil-frame-0.png");
        ImageStore imageStore = new ImageStore(new_image);
        Background new_images = new Background(CLICKED_IMAGE, imageStore.getImageList(CLICKED_IMAGE));

        // actual zombie image
        PImage zombie_image = loadImage("images/zombie1.png");
        ImageStore iStore = new ImageStore(zombie_image);

        List<Point> neighbors = new LinkedList<>(RANGE.apply(pressed).toList());

        for (Point neigh: neighbors){
            helper(neigh, new_images, iStore);
        }
    }

    public void helper(Point neigh, Background new_images, ImageStore iStore){
        if (world.withinBounds(neigh)){
            world.setBackgroundCell(neigh, new_images);
        }
        Optional<Entity> entityOptional = world.getOccupant(neigh);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            if (entity.getClass() == Dude_full.class){
                entity.setImages(iStore.getImageList(ENT_IMAGE));

                Dude_full dudeFull = (Dude_full) entity;
                dudeFull.transform_Zombie(world, scheduler, iStore);
            }
            if (entity.getClass() == Dude_not_full.class){
                entity.setImages(iStore.getImageList(ENT_IMAGE));

                Dude_not_full dudeFull = (Dude_not_full) entity;
                dudeFull.transform_Zombie(world, scheduler, iStore);
            }
        }
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            Functions.shiftView(view, dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            Functions.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
