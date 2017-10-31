package confusion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.KKInfo;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fairy_LFen on 2017/4/2.
 */
public class AddIndexToRegions {
    private String rawPath_region = "KKDataES\\data\\dbscan_clust4j\\kk_dbscan_result.json";
    private String outPath_region_index = "KKDataES\\data\\confusion\\kk_dbscan_result_index.json";
    private FileUtil fileUtil = FileUtil.getInstance();

    public void addIndex() throws IOException {
        ArrayList<ArrayList<KKInfo>> kkInfoSmpl_list = collectAllKKInfoSimple(rawPath_region);

        Map<Integer, ArrayList<KKInfo>> map = new HashMap<Integer, ArrayList<KKInfo>>();
        for (int i = 0; i < kkInfoSmpl_list.size(); i++) {
            map.put(i, kkInfoSmpl_list.get(i));
        }
        String jsonStr = JSON.toJSONString(map);
        fileUtil.saveToFile(outPath_region_index, jsonStr, false);
    }

    private ArrayList<ArrayList<KKInfo>> collectAllKKInfoSimple(String rawPath_region) throws IOException {
        File poiFile = new File(rawPath_region);
        String jsonStr = fileUtil.readJsonFileToStr(poiFile);
        ArrayList<ArrayList<KKInfo>> kkInfoSmpl_list = JSON.parseObject(jsonStr, new TypeReference<ArrayList<ArrayList<KKInfo>>>(){});
        return kkInfoSmpl_list;
    }
    public static void main(String[] args) throws IOException {
        new AddIndexToRegions().addIndex();
    }
}
