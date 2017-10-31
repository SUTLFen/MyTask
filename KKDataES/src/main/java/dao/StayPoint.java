package dao;

/**
 * Created by Fairy_LFen on 2017/1/8.
 *
 * 每辆车经过的卡口点（时间、地点（卡口id））
 *
 */
public class
StayPoint {
    private String kkid = null;
    private String time = null;
    private String direction = null;
    private Integer speed = null;

    public StayPoint() {
    }

    public StayPoint(StayPointInfo stayPointInfo) {
        this.kkid = stayPointInfo.getKkid();
        this.time = stayPointInfo.getStayPointDate();
        this.direction = stayPointInfo.getDirection();
        try{
            this.speed = stayPointInfo.getSpeed();
        }catch(Exception e){
            this.speed = -1;
        }
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getKkid() {
        return kkid;
    }

    public void setKkid(String kkid) {
        this.kkid = kkid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
