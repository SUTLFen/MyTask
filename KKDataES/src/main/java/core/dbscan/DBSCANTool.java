package core.dbscan;

 import com.alibaba.fastjson.JSON;
 import com.alibaba.fastjson.TypeReference;
 import dao.KKInfo;
 import dao.KKInfoNew;
 import util.FileUtil;

 import java.io.File;
 import java.io.IOException;
 import java.text.MessageFormat;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 /**
 * DBSCAN基于密度聚类算法工具类
 *
 * @author lyq
 *
 */
public class DBSCANTool {

//    全局
//    private String rawPath = "KKDataES\\data\\core-dbscan\\kk_feature_vector_normalized_v_gps.json";     // 测试数据文件地址
//    private String outPath = "KKDataES\\data\\core-dbscan\\kk_dbscan_result_20170627_v2.json";
     private String rawPath = "GeotagGraph\\src\\main\\webapp\\data\\spatial\\kk_feature_vector_normalized_v_gps.json";
    private String outPath = "GeotagGraph\\src\\main\\webapp\\data\\spatial\\kk_dbscan_result_20170906_v2.json";

//     private float minEps = 0.5f;     // 簇扫描半径
//     private float maxEps = 1.0f;
//     private int minPts = 1;      // 最小包含点数阈值
//     private float ouDisVector = 0.2f;  // 特征向量相似度   （0.3）

     private float minEps = 0.5f;     // 簇扫描半径
     private float maxEps = 1.0f;
     private int minPts = 1;      // 最小包含点数阈值
     private float ouDisVector = 1.2f;  // 特征向量相似度   （0.3）


//    多层次视图
//     private String rawPath = "KKDataES\\data\\core-multiview\\bigestRegion_feature.json";
//     private String outPath = "F:\\01_Projects\\MyTask\\GeotagGraph\\src\\main" +
//             "\\webapp\\data\\spatial\\multiview\\bigestRegion_feature_dbscan_result_v4.json";


//    private float minEps = 0.3f;    // 簇扫描半径
//    private float maxEps = 0.7f;
//    private int minPts = 1;         // 最小包含点数阈值
//    private float ouDisVector = 0.8f;  // 特征向量相似度   （0.3）  0.9·

    private ArrayList<Point> totalPoints;      // 所有的数据坐标点
    private ArrayList<ArrayList<Point>> resultClusters;      // 聚簇结果
    private ArrayList<Point> noisePoint;      //噪声数据
    private ArrayList<ArrayList<KKInfoNew>> result;   // 最终返回给SpatialSimplifyDataProvider的结果

    private FileUtil fileUtil = FileUtil.getInstance();

    public DBSCANTool() throws IOException {
        readKKInfoDataFile();
    }

    public DBSCANTool(String dataRawPath) throws IOException {
        this.rawPath = dataRawPath;
        readKKInfoDataFile();
    }

    public DBSCANTool(String rawPath, float minEps, float maxEps, int minPts, float ouDisVector) throws IOException {
        this.rawPath = rawPath;
        this.minEps = minEps;
        this.maxEps = maxEps;
        this.minPts = minPts;
        this.ouDisVector = ouDisVector;
        readKKInfoDataFile();
    }

    /**
     * dbScan算法基于密度的聚类
     */
    public ArrayList<ArrayList<KKInfoNew>> dbScanCluster() {
        ArrayList<Point> cluster = null;


        resultClusters = new ArrayList<ArrayList<Point>>();
        noisePoint = new ArrayList<Point>();

        for (int i = 0 ; i < totalPoints.size(); i++) {
            Point p = totalPoints.get(i);
            if(p.isVisited){
                continue;
            }

            cluster = new ArrayList<Point>();
            recursiveCluster(p, cluster);

            if (cluster.size() > 0) {
                resultClusters.add(cluster);
            }else{
                noisePoint.add(p);
            }
        }
        removeFalseNoise();
//        saveCenterPointOfCluster();
//        saveClusters();

//       将噪声点也加入结果中
        for (Point pointTmp : noisePoint) {
            ArrayList<Point> listTmp = new ArrayList<>();
            listTmp.add(pointTmp);
            resultClusters.add(listTmp);
        }
        result = convertResulClust(resultClusters);

        String jsonStr = JSON.toJSONString(result, true);
        fileUtil.saveToFile(outPath, jsonStr, false);

        printClusters();

        return result;
    }

    /**
     * 递归的寻找聚类
     * @param point 当前的点列表
     * @param parentCluster 父聚簇
     */
    private void recursiveCluster(Point point, ArrayList<Point> parentCluster) {
        double distance = 0;
        ArrayList<Point> cluster;

        // 如果已经访问过了，则跳过
        if (point.isVisited) {
            return;
        }

        point.isVisited = true;
        cluster = new ArrayList<Point>();
        double similarity;
        for (Point p2 : totalPoints) {
            // 过滤掉自身的坐标点
            if (point.isTheSame(p2) || p2.isVisited) {
//            if (point.isTheSame(p2)) {
                continue;
            }

            int index = p2.getIndex();
            boolean isVisitedTmp = p2.isVisited;

            similarity = point.ouSimilarity(p2);
            System.out.println(" ********* similarity : " + similarity);

            distance = point.ouDistance(p2);
            System.out.println("distance : " + distance);

//            if (distance <= minEps || (distance > minEps && distance <= maxEps && similarity <= ouDisVector)) {
            if (distance <= minEps) {
//            if (similarity <= ouDisVector) {
                // 如果聚类小于给定的半径，则加入簇中
                cluster.add(p2);
            }
        }

        if (cluster.size() >= minPts ) {
            // 将自己也加入到聚簇中
            cluster.add(point);
            // 如果附近的节点个数超过最下值，则加入到父聚簇中,同时去除重复的点
            addCluster(parentCluster, cluster);

            for (Point p : cluster) {
                recursiveCluster(p, parentCluster);
            }
        }
    }

    private ArrayList<ArrayList<KKInfoNew>> convertResulClust(ArrayList<ArrayList<Point>> resultClusters) {
        ArrayList<ArrayList<KKInfoNew>> result = new ArrayList<ArrayList<KKInfoNew>>();
        for (ArrayList<Point> clustTmp : resultClusters){
            ArrayList<KKInfoNew> clust = new ArrayList<>();
            for(Point pointTmp : clustTmp){
                KKInfo kkinfo = pointTmp.getKkInfo();
                double[] vector = pointTmp.getVectorList();
                KKInfoNew kkInfoNew = new KKInfoNew(kkinfo, vector);
                clust.add(kkInfoNew);
            }
            result.add(clust);
        }
        return result;
    }

    public void readKKInfoDataFile() throws IOException {
        File file = new File(rawPath);
        System.out.println(file.exists());
        String jsonStr = fileUtil.readJsonFileToStr(file);

        Map<KKInfo, double[]> featureVector_map = JSON.parseObject(jsonStr, new TypeReference<Map<KKInfo, double[]>>(){});

        Set<KKInfo> kkInfo_set = featureVector_map.keySet();
        Point point;
        totalPoints = new ArrayList<Point>();

        System.out.println(totalPoints.size());

        int i= 0;
        for(KKInfo kkInfo : kkInfo_set){
            double[] featureVector_lst = featureVector_map.get(kkInfo);
            point = new Point(kkInfo, featureVector_lst, i++);
            totalPoints.add(point);
        }
    }



    /**
     * 往父聚簇中添加局部簇坐标点
     *
     * @param parentCluster 原始父聚簇坐标点
     * @param cluster 待合并的聚簇
     */
    private void addCluster(ArrayList<Point> parentCluster,
                            ArrayList<Point> cluster) {
        boolean isCotained = false;
        ArrayList<Point> addPoints = new ArrayList<Point>();

        for (Point p : cluster) {
            isCotained = false;
            for (Point p2 : parentCluster) {
                if (p.isTheSame(p2)) {
                    isCotained = true;
                    break;
                }
            }

            if (!isCotained) {
                addPoints.add(p);
            }
        }
        parentCluster.addAll(addPoints);
    }

    /**
     * 移除被错误分类的噪声点数据
     **/
    private void removeFalseNoise(){
        ArrayList<Point> totalCluster = new ArrayList<Point>();
        ArrayList<Point> deletePoints = new ArrayList<Point>();

        //将聚簇合并
        for(ArrayList<Point> list: resultClusters){
            totalCluster.addAll(list);
        }

        for(Point p: noisePoint){
            for(Point p2: totalCluster){
                if(p2.isTheSame(p)){
                    deletePoints.add(p);
                }
            }
        }
        noisePoint.removeAll(deletePoints);
    }

    /**
     * 输出聚类结果
     */
    private void printClusters() {
        int i = 1;
        System.out.println("---------------------------------------------");
        System.out.println("---------------------------------------------");
        System.out.println(MessageFormat.format("聚簇个数：{0}", resultClusters.size()));

        for (ArrayList<Point> pList : resultClusters) {
//            System.out.println(MessageFormat.format("聚簇{0}({1})", i++, pList.size()));
            for (Point p : pList) {
                KKInfo kkInfo_tmp = p.getKkInfo();
//                System.out.print(MessageFormat.format("({0}) ", kkInfo_tmp.getKkName()));
            }
            System.out.println();
        }
    }

    protected  void checkClusterDuplicate(){
        Point curPoint = null;
        ArrayList<Point> pointList = null;
        for (int j = 0; j < totalPoints.size(); j++) {
            curPoint = totalPoints.get(j);
            List<Integer> clusterIndex = new ArrayList<Integer>();
            for (int k = 0; k < resultClusters.size(); k++) {
                pointList = resultClusters.get(k);
//                if(pointList.contains(fcurPoint)){
                if(doExit(curPoint, pointList)){
                    clusterIndex.add(k);
                }
            }

            if (clusterIndex.size() > 1){
                KKInfo kkInfo = curPoint.getKkInfo();
//                String indexStr = j + ": " +  kkInfo.getKkName() + " : ";
                String indexStr = MessageFormat.format("{0} : {1} :" , j, kkInfo.getKkName());
                for (Integer index : clusterIndex){
                    indexStr += index+" ";
                }
                System.out.println(indexStr);
            }

        }
    }

    protected boolean doExit(Point curPoint, ArrayList<Point> pointList) {
        KKInfo curKKInfo = curPoint.getKkInfo();
        for (Point pointElm : pointList) {
            String curKkName = curKKInfo.getKkName();
            KKInfo kkInfoElm = pointElm.getKkInfo();
            String kkNameElm = kkInfoElm.getKkName();
            if (curKkName.equals(kkNameElm)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        new DBSCANTool().dbScanCluster();
    }
}