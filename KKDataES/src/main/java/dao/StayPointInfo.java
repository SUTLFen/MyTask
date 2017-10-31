package dao;

/**
 * Created by Fairy_LFen on 2017/1/7.
 *
 * 每一辆车经过卡口时的数据。
 */
public class StayPointInfo {
    private String kkid;  //卡口ID
    private String direction;  //方向
    private String plateColor;  //车牌颜色
    private String vehicleType;   // 车辆类型
    private Integer speed;  //行驶速度
    private String plateNumber;   //车牌号
    private String stayPointDate;   //路过时刻
    private String stayPointDuration;  //路过时间（可忽视， 没用这个数据）
    private String vehicleColor;    //车辆颜色

    public StayPointInfo() {
    }

    public StayPointInfo(String[] info_arry) {
        this.kkid = info_arry[0];
        this.direction = info_arry[1];
        this.plateColor = info_arry[2];
        this.vehicleType = info_arry[3];
        try{
            this.speed = Integer.valueOf(info_arry[4]);
        }catch(Exception e){
            this.speed = -1;
        }
        this.plateNumber = info_arry[5];
        this.stayPointDate = info_arry[6];
        this.stayPointDuration = info_arry[7];
        this.vehicleColor = info_arry[8];
    }

    public String getKkid() {
        return kkid;
    }

    public void setKkid(String kkid) {
        this.kkid = kkid;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(String plateColor) {
        this.plateColor = plateColor;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getStayPointDate() {
        return stayPointDate;
    }

    public void setStayPointDate(String stayPointDate) {
        this.stayPointDate = stayPointDate;
    }

    public String getStayPointDuration() {
        return stayPointDuration;
    }

    public void setStayPointDuration(String stayPointDuration) {
        this.stayPointDuration = stayPointDuration;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }
    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }
}
