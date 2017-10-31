package core.search.kkHourlyData;

/**
 * Created by Fairy_LFen on 2017/3/8.
 */
public class KKInfoTemp {
    private String kkName = null;
    private String kkId = null;
    private String fxbh = null;

    public KKInfoTemp(String kkName, String kkId, String fxbh) {
        this.kkName = kkName;
        this.kkId = kkId;
        this.fxbh = fxbh;
    }

    public String getKkName() {
        return kkName;
    }

    public void setKkName(String kkName) {
        this.kkName = kkName;
    }

    public String getKkId() {
        return kkId;
    }

    public void setKkId(String kkId) {
        this.kkId = kkId;
    }

    public String getFxbh() {
        return fxbh;
    }

    public void setFxbh(String fxbh) {
        this.fxbh = fxbh;
    }
}
