package core.od.layout;

/**
 * Created by Fairy_LFen on 2017/3/7.
 */
public class GeoNode {
    private int id;
    private double x;
    private double y;

    public GeoNode() {
    }

    public GeoNode(int id, double x, double y){
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
