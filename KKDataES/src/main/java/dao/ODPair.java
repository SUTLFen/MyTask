package dao;

import java.util.Map;

public class ODPair {
    private String plateNumber = null;

    private String o_direction = null;
    private String o_kkid = null;
    private Integer o_speed = null;
    private String o_time = null;
    private String o_lnglat = null;

    private String d_direction = null;
    private String d_kkid = null;
    private Integer d_speed = null;
    private String d_time = null;
    private String d_lnglat = null;

    public ODPair(String plateNumberTmp, Map<String, Object> o_stayPoint, Map<String, Object> d_stayPoint) {
        this.plateNumber = plateNumberTmp;
        this.o_direction = (String) o_stayPoint.get("direction");
        this.o_kkid = (String) o_stayPoint.get("kkid");
        this.o_speed = (Integer) o_stayPoint.get("speed");
        this.o_time = (String) o_stayPoint.get("stayPointDate");

        this.d_direction = (String) d_stayPoint.get("direction");
        this.d_kkid = (String) d_stayPoint.get("kkid");
        this.d_speed = (Integer) d_stayPoint.get("speed");
        this.d_time = (String) d_stayPoint.get("stayPointDate");
    }

    public String getO_lnglat() {
        return o_lnglat;
    }

    public void setO_lnglat(String o_lnglat) {
        this.o_lnglat = o_lnglat;
    }

    public String getD_lnglat() {
        return d_lnglat;
    }

    public void setD_lnglat(String d_lnglat) {
        this.d_lnglat = d_lnglat;
    }

    public ODPair() {
    }

    public String getO_direction() {
        return o_direction;
    }

    public void setO_direction(String o_direction) {
        this.o_direction = o_direction;
    }

    public String getO_kkid() {
        return o_kkid;
    }

    public void setO_kkid(String o_kkid) {
        this.o_kkid = o_kkid;
    }

    public Integer getO_speed() {
        return o_speed;
    }

    public void setO_speed(Integer o_speed) {
        this.o_speed = o_speed;
    }

    public String getO_time() {
        return o_time;
    }

    public void setO_time(String o_time) {
        this.o_time = o_time;
    }

    public String getD_direction() {
        return d_direction;
    }

    public void setD_direction(String d_direction) {
        this.d_direction = d_direction;
    }

    public String getD_kkid() {
        return d_kkid;
    }

    public void setD_kkid(String d_kkid) {
        this.d_kkid = d_kkid;
    }

    public Integer getD_speed() {
        return d_speed;
    }

    public void setD_speed(Integer d_speed) {
        this.d_speed = d_speed;
    }

    public String getD_time() {
        return d_time;
    }

    public void setD_time(String d_time) {
        this.d_time = d_time;
    }

    public ODPair(String plateNumber, StayPoint[] stayPoints) {
        this.plateNumber = plateNumber;
        this.o_direction = stayPoints[0].getDirection();
        this.o_kkid = stayPoints[0].getKkid();
        try{
            this.o_speed = stayPoints[0].getSpeed();
        }catch(Exception e){
            this.o_speed = -1;
        }
        this.o_time = stayPoints[0].getTime();

        this.d_direction = stayPoints[1].getDirection();
        this.d_kkid = stayPoints[1].getKkid();
        try{
            this.d_speed = stayPoints[1].getSpeed();
        }catch(Exception e){
            this.d_speed = -1;
        }
        this.d_time = stayPoints[1].getTime();
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }


}