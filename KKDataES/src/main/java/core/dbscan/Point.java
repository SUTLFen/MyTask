package core.dbscan;

import dao.KKInfo;
import util.GeoDistance;
import util.NumberUtil;

/**
 * 坐标点类
 * @author lyq
 */
public class Point {

    int index ;
//    int x; // 坐标点横坐标
//    int y; // 坐标点纵坐标
    boolean isVisited; // 此节点是否已经被访问过
    double x;
    double y;
    private KKInfo kkInfo = null;
    private double[] vectorList = null;
    private GeoDistance geoDistance = GeoDistance.getinstance();

    private NumberUtil numberUtil = NumberUtil.getInstance();

    public Point(KKInfo kkInfo, double[] vectorList){
        this.kkInfo = kkInfo;
        this.vectorList = vectorList;
    }

    public Point(KKInfo kkInfo, double[] vectorList, int i) {
        this.kkInfo = kkInfo;
        this.vectorList = vectorList;
        this.index = i;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public KKInfo getKkInfo() {
        return kkInfo;
    }

    public void setKkInfo(KKInfo kkInfo) {
        this.kkInfo = kkInfo;
    }

    public double[] getVectorList() {
        return vectorList;
    }

    public void setVectorList(double[] vectorList) {
        this.vectorList = vectorList;
    }

    public Point(String x, String y) {
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
        this.isVisited = false;
    }

    /**
     * 计算当前点与制定点之间的欧式距离
     *
     * @param p 待计算聚类的p点
     * @return
     */
    public double cosDistance(Point p) {
        double[] vector_01 = this.vectorList;
        double[] vector_02 = p.getVectorList();

        double norm_01 = calLengthOfVector(vector_01);
        double norm_02 = calLengthOfVector(vector_02);
        double dotProduct = calVectorInnerProduct(vector_01, vector_02);
        double cos = numberUtil.save6Decimal(dotProduct / (norm_01 * norm_02));
        return cos;
    }

    private double calVectorInnerProduct(double[] vectorList, double[] vectorList_02) {
        double result = 0.0d;
        for(int i = 0; i < vectorList.length; i++){
            result += vectorList[i] * vectorList_02[i];
        }
        return result;
    }

    private double calLengthOfVector(double[] vectorList) {
        double sum = 0.0d;
        for(int i = 0; i < vectorList.length; i++){
            sum += (double)Math.pow(vectorList[i], 2);
        }
        sum = Math.sqrt(sum);
        return sum;
    }

    /**
     * 判断2个坐标点是否为用个坐标点
     *
     * @param p 待比较坐标点
     * @return
     */
    public boolean isTheSame(Point p) {
//        boolean isSamed = false;
//        if (this.x == p.x && this.y == p.y) {
//            isSamed = true;
//        }
//        return isSamed;
        KKInfo kkInfo_01 = this.kkInfo;
        KKInfo kkInfo_02 = p.getKkInfo();

        String name_01 = kkInfo_01.getKkName();
        String name_02 = kkInfo_02.getKkName();

        if(name_01.equals(name_02)){
            return true;
        }
        else return false;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
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

    public double ouDistance(Point p2) {
        KKInfo kkInfo_01 = this.kkInfo;
        KKInfo kkInfo_02 = p2.getKkInfo();

        double lat_01 = Double.valueOf(kkInfo_01.getLat());
        double lng_01 = Double.valueOf(kkInfo_01.getLng());

        double lat_02 = Double.valueOf(kkInfo_02.getLat());
        double lng_02 = Double.valueOf(kkInfo_02.getLng());

        double dis = geoDistance.GetDistance(lat_01, lng_01, lat_02, lng_02);
        return numberUtil.save6Decimal(dis);
    }

    public double ouSimilarity(Point p2) {
        double[] vector_01 = this.vectorList;
        double[] vector_02 = p2.getVectorList();

        double sum = 0.0;
        for (int i = 0; i < vector_01.length; i++) {
            sum += Math.pow(vector_01[i] - vector_02[2], 2);
        }
        sum = numberUtil.save6Decimal(Math.sqrt(sum));
        return sum;
    }
}