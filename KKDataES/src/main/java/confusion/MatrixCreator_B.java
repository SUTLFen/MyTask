package confusion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sun.deploy.util.StringUtils;
import core.dbscan.KKInfoSimple;
import core.es.poi.POI;
import ctx.KKContext;
import dao.KKInfo;
import util.FileUtil;
import util.GeoDistance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Fairy_LFen on 2017/4/2.
 * 主要用于构建矩阵B region * POi (KK 3Km内的POI)
 */
public class MatrixCreator_B {
    private String rawPath_region = "KKDataES\\data\\confusion\\kk_dbscan_result_index.json";
    private String rawPath_POI = "KKDataES\\data\\poi\\poi_hangzhou.json";
    private String outPath = "KKDataES\\data\\confusion\\B_POI.txt";

    private final double DISTANCE = 3.0;   // 3公里以内的POI
    private FileUtil fileUtil = FileUtil.getInstance();
    public void createMatrixB() throws IOException {
        List<POI> pois = collectAllHangzhouPOIS(rawPath_POI);
        Map<Integer, ArrayList<KKInfo>> regions_map = collectAllHangzhouKKInfo(rawPath_region);
        Set<Integer> key_set = regions_map.keySet();

        ArrayList<KKInfo> region = null;
        File outFile = new File(outPath);
        BufferedWriter bw = fileUtil.getBufferedWriter(outFile);
        int[] numsInPois;
        String rowStr;
        for (int i = 0; i < regions_map.size(); i++) {
            region = regions_map.get(i);
//            矩阵B的某一行
            numsInPois = poisIncludedInRegion(pois, region);
            rowStr = toStrList(numsInPois);

            bw.append(rowStr);
            bw.append("\n");
            bw.flush();
        }
        bw.close();
    }

    private String toStrList(int[] numsOfPois) {
        List<String> strs = new ArrayList<String>();
        for (int i = 0; i < numsOfPois.length; i++) {
            strs.add(numsOfPois[i] + "");
        }
        String str = StringUtils.join(strs, " ");
        return str;
    }

    private int[] poisIncludedInRegion(List<POI> pois, ArrayList<KKInfo> region) {
        int[] nums = new int[KKContext.NUM_POI_TYPE ];
        for (int i = 0; i < pois.size(); i++) {
            if(doPOIExist(pois.get(i), region)){
                int typeIndex = poiIndex(pois.get(i));
                if(typeIndex >= 1 && typeIndex <= 20){
                    nums[typeIndex-1]++;
                }
            }
        }
        return nums;
    }

    private int poiIndex(POI poi) {
        String typeStr = poi.getTypecode();
        try{
            String subTypeStr = typeStr.substring(0, 2);
            int typeIndex = Integer.valueOf(subTypeStr);
            return typeIndex;
        }catch(Exception e){
            return -1;
        }
    }

    private GeoDistance geoDistance = GeoDistance.getinstance();

    // poi是否存在regions中， 3km
    private boolean doPOIExist(POI poi, ArrayList<KKInfo> regionsList) {
        double lng_01 = Double.valueOf(poi.getLocationx());
        double lat_01 = Double.valueOf(poi.getLocationy());

        double lat_02, lng_02;
        for(KKInfo kkInfo : regionsList){
            lat_02 = Double.valueOf(kkInfo.getLat());
            lng_02 = Double.valueOf(kkInfo.getLng());

            System.out.println(geoDistance.GetDistance(lat_01, lng_01, lat_02, lng_02));
            if(geoDistance.GetDistance(lat_01, lng_01, lat_02, lng_02) <= DISTANCE){
                return true;
            }
        }
        return false;
    }

    private Map<Integer, ArrayList<KKInfo>> collectAllHangzhouKKInfo(String rawPath_region) throws IOException {
        File file = new File(rawPath_region);
        String jsonStr = fileUtil.readJsonFileToStr(file);
        Map<Integer, ArrayList<KKInfo>> regions_map = JSON.parseObject(jsonStr,
                new TypeReference<Map<Integer, ArrayList<KKInfo>>>(){});
        return regions_map;
    }

    private ArrayList<ArrayList<KKInfoSimple>> collectAllKKInfoSimple(String rawPath_region) throws IOException {
        File poiFile = new File(rawPath_region);
        String jsonStr = fileUtil.readJsonFileToStr(poiFile);
        ArrayList<ArrayList<KKInfoSimple>> kkInfoSmpl_list = JSON.parseObject(jsonStr, new TypeReference<ArrayList<ArrayList<KKInfoSimple>>>(){});
        return kkInfoSmpl_list;
    }

    private List<POI> collectAllHangzhouPOIS(String rawPath_poi) throws IOException {
        File poiFile = new File(rawPath_poi);
        String jsonStr = fileUtil.readJsonFileToStr(poiFile);
        List<POI> pois = JSON.parseArray(jsonStr, POI.class);
        return pois;
    }

    public static void main(String[] args) throws IOException {
        new MatrixCreator_B().createMatrixB();
    }
}
