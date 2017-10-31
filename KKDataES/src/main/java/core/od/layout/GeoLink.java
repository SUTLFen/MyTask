package core.od.layout;

/**
 * Created by Fairy_LFen on 2017/3/7.
 */
public class GeoLink {
    private  String source = null;
    private String target = null;

    public GeoLink(String o_flag, String d_flag) {
        this.source = o_flag;
        this.target = d_flag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
