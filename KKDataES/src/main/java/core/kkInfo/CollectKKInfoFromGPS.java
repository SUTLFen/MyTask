package core.kkInfo;

import com.alibaba.fastjson.JSON;
import dao.KKInfo;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Fairy_LFen on 2017/5/3.
 * 搜集卡口（去除重复卡口ID）
 */

public class CollectKKInfoFromGPS {
    private String rawPath = "KKDataES\\data\\kkInfo\\kk_info_from_gps.json";
    private String outPath = "KKDataES\\data\\kkInfo\\allKKInfo_gps.json";

    private FileUtil fileUtil = FileUtil.getInstance();

    public void collect() throws IOException {
        File rawFile = new File(rawPath);
        String str = fileUtil.readJsonFileToStr(rawFile);
        List<KKInfo> kkInfoList = JSON.parseArray(str, KKInfo.class);
        List<KKInfo> kkInfoList_result = removeDuplicates(kkInfoList);
        String jsonStr = JSON.toJSONString(kkInfoList_result);
        fileUtil.saveToFile(outPath, jsonStr, false);
    }

    private List<KKInfo> removeDuplicates(List<KKInfo> kkInfoList) {
        Map<String, KKInfo> resultMap = new HashMap<String, KKInfo>();
        for (KKInfo kkInfoItm : kkInfoList){
            String kkInfoName = kkInfoItm.getKkName();
            String[] strArry = kkInfoName.split("_");

            if(!resultMap.containsKey(strArry[0])){
                resultMap.put(strArry[0], kkInfoItm);
            }
        }

        List<KKInfo> kkInfoList_result = valueSet(resultMap);
        return kkInfoList_result;
    }

    private List<KKInfo> valueSet(Map<String, KKInfo> resultMap) {
        Set<String> keySet = resultMap.keySet();
        List<KKInfo> result = new ArrayList<KKInfo>();
        for (String key : keySet){
            result.add(resultMap.get(key));
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        new CollectKKInfoFromGPS().collect();
    }
}
