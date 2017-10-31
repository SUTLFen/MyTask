package core.es.poi;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.client.transport.TransportClient;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/3/10.
 */
public class ESHangzhouPOI {
    private final String indexName = "poi";
    private final String typeName = "poi_hanghzou";

    private String rawDataPath = "KKDataES\\data\\poi_data\\poi_hanghzou.json";
    private FileUtil fileUtil = FileUtil.getInstance();


    private TransportClient client = null;

    public void indexHangzhouPOI() throws IOException {
        List<POI> poi_list = toPOIList(rawDataPath);
//        hello
    }
    private List<POI> toPOIList(String rawDataPath) throws IOException {
        File rawFile = new File(rawDataPath);
        String jsonStr = fileUtil.readJsonFileToStr(rawFile);
        List<POI> poiList = JSON.parseArray(jsonStr, POI.class);
        return poiList;
    }
}
