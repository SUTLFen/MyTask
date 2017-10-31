package core.dbscan;

/**
 * Created by Fairy_LFen on 2017/3/30.
 */
public class KKInfoSimple {
    private String lat;
    private String lng;
    private String name;

    public KKInfoSimple(){}

    public KKInfoSimple(String lat, String lng, String kkName) {
        this.lat = lat;
        this.lng = lng;
        this.name = kkName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
