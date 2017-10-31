package core.kkFeatureVector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.KKInfo;
import util.FileUtil;
import util.NumberUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Fairy_LFen on 2017/4/23
 * 卡口特征向量归一化
 */
public class KKFeatureNormalize {
// POI

//    private String rawPath = "KKDataES\\data\\core_kkFeatureVector\\kk_feature_vector.json";
//    private String outPath = "KKDataES\\data\\core_kkFeatureVector\\kk_feature_vector_normalized.json";

    private String rawPath = "KKDataES\\data\\core-kkFeatureVector\\gps\\kk_feature_vector_gps.json";
//    private String outPath = "KKDataES\\data\\core_kkFeatureVector\\gps\\kk_feature_vector_normalized_h_gps.json";
    private String outPath = "KKDataES\\data\\core-kkFeatureVector\\gps\\kk_feature_vector_normalized_v_gps.json";

    private final int DIMENSION_V = 13;   // 原值 ： 20

////       flow
//    private String rawPath = "KKDataES\\data\\core_kkFeatureVector\\flow\\kk_feature_vector_flow.json";
//    private String outPath = "KKDataES\\data\\core_kkFeatureVector\\flow\\kk_feature_vector_normalized_flow.json";
//    private final int DIMENSION_V = 24;

    private FileUtil fileUtil = FileUtil.getInstance();
    private NumberUtil numberUtil = NumberUtil.getInstance();


//    横向归一化
//    public void normalized() throws IOException {
//        Map<KKInfo, int[]> kkVector_map = toKKVectorMap(rawPath);
//        Map<KKInfo, double[]> resultVector_map = new HashMap<KKInfo, double[]>();
//
//        Set<KKInfo> kkInfo_set = kkVector_map.keySet();
//        for (KKInfo kkInfo_tmp : kkInfo_set) {
//            if (!resultVector_map.containsKey(kkInfo_tmp)){
//                resultVector_map.put(kkInfo_tmp, new double[DIMENSION_V]);
//            }
//            double[] vectorResultArry = resultVector_map.get(kkInfo_tmp);
//
//            int[] vectorArry = kkVector_map.get(kkInfo_tmp);
//
//            int sum = 0;
//            for (int j = 0; j < vectorArry.length; j++) {
//                sum += vectorArry[j];
//            }
//            for (int i = 0; i < vectorArry.length; i++) {
//                vectorResultArry[i] = numberUtil.save6Decimal(vectorArry[i]/(sum + 0.0));
//            }
//
//        }
//        String jsonStr = JSON.toJSONString(resultVector_map, true);
//        fileUtil.saveToFile(outPath, jsonStr, false);
//
//    }

//    the old version (最大最小标准化--)
public void normalized() throws IOException {
        Map<KKInfo, int[]> kkVector_map = toKKVectorMap(rawPath);
        Map<KKInfo, double[]> resultVector_map = new HashMap<KKInfo, double[]>();

        Set<KKInfo> kkInfo_set = kkVector_map.keySet();
        int[][] max_min = findMaxMin(kkInfo_set,kkVector_map);

        for (KKInfo kkInfo_tmp : kkInfo_set) {
            if (!resultVector_map.containsKey(kkInfo_tmp)){
                resultVector_map.put(kkInfo_tmp, new double[DIMENSION_V]);
            }
            double[] vectorResultArry = resultVector_map.get(kkInfo_tmp);
            int[] vectorArry = kkVector_map.get(kkInfo_tmp);
            for (int i = 0; i < vectorArry.length; i++) {
                int max = max_min[i][0];
                int min = max_min[i][1];
                vectorResultArry[i] = numberUtil.save6Decimal((vectorArry[i] - min)/(max - min - 0.0));
            }
        }

        String jsonStr = JSON.toJSONString(resultVector_map, true);
        fileUtil.saveToFile(outPath, jsonStr, false);
    }

    private int[][] findMaxMin(Set<KKInfo> kkInfo_set, Map<KKInfo, int[]> kkVector_map) {
        int[][] max_min = new int[DIMENSION_V][2];
        intMaxMinArray(max_min);
        for (KKInfo kkInfo_tmp : kkInfo_set) {
            int[] vectorArry = kkVector_map.get(kkInfo_tmp);
            for (int i = 0; i < vectorArry.length; i++) {
                int max_tmp = max_min[i][0];
                int min_tmp = max_min[i][1];

                if(vectorArry[i] > max_tmp) max_min[i][0] = vectorArry[i];
                if(vectorArry[i] < min_tmp) max_min[i][1] = vectorArry[i];
            }
        }
        return max_min;
    }

    private void intMaxMinArray(int[][] max_min) {
        for (int i = 0; i <max_min.length; i++) {
            max_min[i][0] = -1;
            max_min[i][1] = 99999;
        }
    }

    private Map<KKInfo, int[]> toKKVectorMap(String rawPath) throws IOException {
        File file = new File(rawPath);
        String str = fileUtil.readJsonFileToStr(file);
        return JSON.parseObject(str, new TypeReference<Map<KKInfo, int[]>>(){});
    }

    public static void main(String[] args) throws IOException {
        new KKFeatureNormalize().normalized();
    }
}
