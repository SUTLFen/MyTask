package core.multiview;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.KKInfo;
import dao.KKInfoNew;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fairy_LFen on 2017/7/28.
 *
 * 整理数据，将cluster中最大的cluster整理出来，转为它的特征向量文件。
 */
public class ToOnClusterFeature {

    private String rawPath =  "KKDataES\\data\\core-multiview\\kk_dbscan_result_20170631_v1.json";
    private String outPath = "KKDataES\\data\\core-multiview\\bigestRegion_feature.json";
    private FileUtil fileUtil = FileUtil.getInstance();

    public void convert() throws IOException {
        String str = fileUtil.readJsonFileToStr(new File(rawPath));
        List<List<KKInfoNew>> clusters = JSON.parseObject(str, new TypeReference<List<List<KKInfoNew>>>(){});

        List<KKInfoNew> cluster = null;
        int max = 0, flag = -1 ;
        for (int i = 0; i < clusters.size(); i++) {
            cluster = clusters.get(i);
            int sizeTmp = cluster.size();
            if(sizeTmp > max) {
                max = sizeTmp;
                flag = i;
            }
        }
        cluster = clusters.get(flag);
        Map<KKInfo, double[]> result_map = new HashMap<KKInfo, double[]>();
        for (KKInfoNew kkInfoNew : cluster) {
            double[] vector = kkInfoNew.getVector();
            KKInfo kkInfo = new KKInfo();
            kkInfo.setFxbh(kkInfoNew.getFxbh());
            kkInfo.setId(kkInfoNew.getId());
            kkInfo.setKkName(kkInfoNew.getKkName());
            kkInfo.setKkType(kkInfoNew.getKkType());
            kkInfo.setLat(kkInfoNew.getLat());
            kkInfo.setLng(kkInfoNew.getLng());

            result_map.put(kkInfo, vector);
        }
        String jsonStr = JSON.toJSONString(result_map, true);
        fileUtil.saveToFile(outPath, jsonStr, false);
    }

    public static void main(String[] args) throws IOException {
        new ToOnClusterFeature().convert();
    }
}
