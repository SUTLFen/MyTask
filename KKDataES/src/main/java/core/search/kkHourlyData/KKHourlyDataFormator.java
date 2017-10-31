package core.search.kkHourlyData;

import com.alibaba.fastjson.JSON;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/3/9.
 */
public class KKHourlyDataFormator {
    private String rawPath = "KKDataES\\data_kk_hourly";
    private String outPath = "KKDataES\\data_kk_hourly_new";
    private FileUtil fileUtil = FileUtil.getInstance();

    public void format() throws IOException {
        File file = new File(rawPath);
        File[] files = file.listFiles();
        for(File fileItem : files){
            String jsonStr = fileUtil.readJsonFileToStr(fileItem);
            List<Integer> count_list = JSON.parseArray(jsonStr, Integer.class);
            formatList(count_list, fileItem.getName());
        }
    }

    private void formatList(List<Integer> count_list, String fileName) {
        String outDataPath = String.format(outPath+"\\%s", fileName);
        List<CountObject> countObject_list = new ArrayList<CountObject>();
        for(int i = 0; i < count_list.size(); i++){
            CountObject countObject = new CountObject(i, count_list.get(i));
            countObject_list.add(countObject);
        }
        String jsonStr = JSON.toJSONString(countObject_list, true);
        fileUtil.saveToFile(outDataPath, jsonStr, true);
    }

    public static void main(String[] args) throws IOException {
        new KKHourlyDataFormator().format();
    }
}
