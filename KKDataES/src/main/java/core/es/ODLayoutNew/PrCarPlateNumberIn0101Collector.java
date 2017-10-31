package core.es.ODLayoutNew;
import com.alibaba.fastjson.JSON;
import dao.StayPointInfo;
import util.ESUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * Created by Fairy_LFen on 2017/3/5.
 * 搜集所有的私家车；
 * 目前使用的数据：私家车（为固有的私家车的一半）
 */

public class PrCarPlateNumberIn0101Collector {
//   2016-01-04
    //    private String rawDataPath = "KKDataES\\data\\es_kkdata\\PrCarDataIn0104_%5.json";
//    private String outPath = "KKDataES\\data\\OD_layout\\ODLayoutNew\\data_0104\\AllPlateNumberIn0104_5.json";

    //   2016-01-01
        private String rawDataPath = "KKDataES\\data\\es_kkdata\\PrCarDataIn0101.json";
    private String outPath = "KKDataES\\data\\OD_layout\\ODLayoutNew\\data_0101\\AllPlateNumberIn0101_5.json";

    private ESUtil esUtil = ESUtil.getInstance();
    private FileUtil fileUtil = FileUtil.getInstance();

    public void colletPrCarPlateNumberIn0101() throws IOException {
        File rawFile = new File(rawDataPath);
        String jsonStr = fileUtil.readJsonFileToStr(rawFile);
        List<StayPointInfo> list = JSON.parseArray(jsonStr, StayPointInfo.class);
        Map<String, Integer> plateNumber_map = new HashMap<String, Integer>();
        int flag = 0;
        for(StayPointInfo stayPointInfo: list){
            String plateNumber = stayPointInfo.getPlateNumber();
            if(!plateNumber_map.containsKey(plateNumber)){
                plateNumber_map.put(plateNumber, flag++);
            }
        }
        Set<String> plateNumber_set = plateNumber_map.keySet();
        String str = JSON.toJSONString(plateNumber_set);
        fileUtil.saveToFile(outPath, str, true);

    }

    public static void main(String[] args) throws IOException {
        new PrCarPlateNumberIn0101Collector().colletPrCarPlateNumberIn0101();
    }
}
