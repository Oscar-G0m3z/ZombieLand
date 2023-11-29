import static java.lang.Math.abs;

public class Node {
    private Point loc;
    private int f; // g + h (go to node next)
    private int g; //  g + 1
    private int h; //Distance of current node from start (known NOT heuristic)
    private Node prior; // current node
    public Node(Point loc, int g, int h, int f, Node prior){
        this.loc = loc;
        this.g = g;
        this.h = h;
        this.f = f;
        this.prior = prior;
    }
    public boolean equals(Node n){
        return this == n;
    }
    public int heuristic(Point cur, Point end){
        return abs((end.x - cur.x) + (end.y - cur.y));
    }
    public Point getLoc(){
     return this.loc;
    }
    public int getF() {
        return f;
    }

    public void setF(int g, int h) {
        this.f = g + h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(Point p, Point p2) {
        this.h = heuristic(p, p2);
    }

    public Node getPrior() {
        return prior;
    }

    public void setPrior(Node prior) {
        this.prior = prior;
    }
}