package core.es.trajpoints;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.IndivisualPrCarInfo;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Fairy_LFen on 2017/2/28.
 */
public class FormatPrTrajPoints {
    private String rawDataPath = "KKDataES\\data\\PrCarTrajStayPoints.json";
    private String outDataPath = "KKDataES\\data\\PrCarTrajStayPoints_new.json";
    private FileUtil fileUtil = FileUtil.getInstance();

    public void formatPrTrajPoints() throws IOException {
        File file = new File(rawDataPath);
        String str = fileUtil.readJsonFileToStr(file);
        Map<String, IndivisualPrCarInfo> map = JSON.parseObject(str, new TypeReference<Map<String, IndivisualPrCarInfo>>(){});
        List<IndivisualPrCarInfo> list = new ArrayList<IndivisualPrCarInfo>();

        Set<String> keySet = map.keySet();
        for(String keySetItem : keySet){
            IndivisualPrCarInfo indivisualPrCarInfo = map.get(keySetItem);
            list.add(indivisualPrCarInfo);
        }

        String jsonStr = JSON.toJSONString(list);
//        fileUtil.saveToFile();
    }

    public static void main(String[] args) throws IOException {
        new FormatPrTrajPoints().formatPrTrajPoints();
    }
}
