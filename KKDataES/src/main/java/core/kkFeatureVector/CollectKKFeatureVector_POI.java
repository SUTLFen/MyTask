package core.kkFeatureVector;

import com.alibaba.fastjson.JSON;
import core.es.poi.POI;
import dao.KKInfo;
import util.FileUtil;
import util.GeoDistance;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fairy_LFen on 2017/4/23.
 * 整理卡口POI特征向量
 */
public class CollectKKFeatureVector_POI {
//  private String rawPath_kk = "KKDataES\\data\\core_kkFeatureVector\\allKKInfo.json";
//  private String outPath = "KKDataES\\data\\core_kkFeatureVector\\kk_feature_vector.json";

    private String rawPath_kk = "KKDataES\\data\\core-kkFeatureVector\\gps\\allKKInfo_gps.json";
    private String outPath = "KKDataES\\data\\core-kkFeatureVector\\gps\\kk_feature_vector_gps.json";

    private String rawPath_poi = "KKDataES\\data\\poi\\poi_hangzhou.json";
    private final double DIS_Threshold = 3.0;
    private final int DIMENSION_V = 13;   // 原值 ： 20

    private FileUtil fileUtil = FileUtil.getInstance();
    private GeoDistance geoDisUtil = GeoDistance.getinstance();

    public void collect() throws IOException {
        List<KKInfo> kkInfo_list = toKKInfoList(rawPath_kk);
        List<POI> poi_list = toPOIList(rawPath_poi);
        Map<KKInfo, int[]> featureVector_map = new HashMap<KKInfo, int[]>();

        for (KKInfo kkInfo_tmp : kkInfo_list) {
            double lat1, lng1;
            String lat1Str = kkInfo_tmp.getLat();
            String lng1Str = kkInfo_tmp.getLng();
            if (!lat1Str.isEmpty() && !lng1Str.isEmpty()) {
                lat1 = Double.valueOf(kkInfo_tmp.getLat());
                lng1 = Double.valueOf(kkInfo_tmp.getLng());
            } else
                continue;

            String typecode , typeTmp;
            for (POI poi_tmp : poi_list) {
                System.out.println("typecode: " + poi_tmp.getTypecode());

                typecode = poi_tmp.getTypecode();
                try{
                    typeTmp = typecode.substring(0, 2);
                    System.out.println(typeTmp);
                }catch(Exception e){
                    typeTmp = "-1";
                }

                int typeTmpInt = Integer.valueOf(typeTmp);
                if(isNeeded(typeTmp)){
                    double lng2 = poi_tmp.getLocationx();
                    double lat2 = poi_tmp.getLocationy();
                    double dis = geoDisUtil.GetDistance(lat1, lng1, lat2, lng2);

                    if (dis < DIS_Threshold) {
                        if (!featureVector_map.containsKey(kkInfo_tmp)) {
                            featureVector_map.put(kkInfo_tmp, new int[DIMENSION_V]);
                        }

                        int[] poi_featureVector = featureVector_map.get(kkInfo_tmp);
                        int index_poi = toIndex(poi_tmp);
                        if (index_poi > 0) {
                            System.out.println(index_poi);
                            poi_featureVector[index_poi - 5]++;
                        }
                    }
                }
            }
        }


        String jsonStr = JSON.toJSONString(featureVector_map);
        fileUtil.saveToFile(outPath, jsonStr, false);

    }

    private boolean isNeeded(String typeTmp) {
        int typeInt = Integer.valueOf(typeTmp);
        if(typeInt == 5 ||typeInt == 6 ||typeInt == 7 ||typeInt == 8 ||typeInt == 9 ||typeInt == 10 ||typeInt == 11
                ||typeInt == 12 ||typeInt == 13 ||typeInt == 14 ||typeInt == 15 ||typeInt == 16 ||typeInt == 17){
            return true;
        }else {
            return false;
        }
    }

    private int toIndex(POI poi_tmp) {
        try {
            String typeCode = poi_tmp.getTypecode();
            String subCode = typeCode.substring(0, 2);
            int index = Integer.valueOf(subCode);
            if (index <= 20 && index >= 1) {
                return index;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    private List<POI> toPOIList(String rawPath_poi) throws IOException {
        File file = new File(rawPath_poi);
        String str = fileUtil.readJsonFileToStr(file);
        List<POI> kkInfo_list = JSON.parseArray(str, POI.class);
        return kkInfo_list;
    }

    private List<KKInfo> toKKInfoList(String rawPath_kk) throws IOException {
        File file = new File(rawPath_kk);
        String str = fileUtil.readJsonFileToStr(file);
        List<KKInfo> kkInfo_list = JSON.parseArray(str, KKInfo.class);
        return kkInfo_list;
    }

    public static void main(String[] args) throws IOException {
        new CollectKKFeatureVector_POI().collect();
    }
}
