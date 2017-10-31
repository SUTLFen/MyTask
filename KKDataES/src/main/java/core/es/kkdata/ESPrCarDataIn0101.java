package core.es.kkdata;

import com.alibaba.fastjson.JSON;
import dao.StayPointInfo;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/3/3.
 */
public class ESPrCarDataIn0101 {
    private String rawDataPath = "KKDataES\\data\\PrCarDataIn0101.json";
    private String clusterName = "kkdata_20160101_5";
    private String typeName = "PrCarDataIn0101_5";

    private FileUtil fileUtil = FileUtil.getInstance();

    public void indexPrCarDataIn0101() throws IOException {
        TransportClient client = newTransportClient();
        List<StayPointInfo> list = newESSourceData(rawDataPath);

        String mapping_str = "{\n" +
                "    \"PrCarDataIn0101_type\": { \n" +
                "      \"_all\":       { \"enabled\": false  }, \n" +
                "      \"properties\": { \n" +
                "        \"direction\":    { \"type\": \"keyword\"  }, \n" +
                "        \"kkid\":     { \"type\": \"keyword\"  }, \n" +
                "        \"plateColor\"  :   { \"type\": \"keyword\" },\n" +
                "        \"plateNumber\"  :   { \"type\": \"keyword\" },\n" +
                "        \"speed\": {\"type\": \"integer\"},\n" +
                "        \"stayPointDate\": {\"type\": \"long\"},\n" +
                "        \"stayPointDuration\": {\"type\": \"long\"},\n" +
                "        \"vehicleColor\": {\"type\": \"keyword\"},\n" +
                "        \"vehicleType\": {\"type\": \"keyword\"}\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        client.admin().indices().prepareCreate("kkdata_201601_index")
                .addMapping("PrCarDataIn0101_type", mapping_str)
                .get();
        for(StayPointInfo stayPointInfo : list){
            String jsonStr = JSON.toJSONString(stayPointInfo);
            IndexResponse response = client.prepareIndex("kkdata_201601_index", "PrCarDataIn0101_type")
                    .setSource(jsonStr)
                    .get();
        }
    }

    private List<StayPointInfo> newESSourceData(String rawDataPath) throws IOException {
        File rawDataFile = new File(rawDataPath);
        String str = fileUtil.readJsonFileToStr(rawDataFile);
        List<StayPointInfo> list =  JSON.parseArray(str, StayPointInfo.class);
        return list;
    }

    private TransportClient newTransportClient() throws UnknownHostException {

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }

    public void test() throws UnknownHostException {
        TransportClient client = newTransportClient();
        client.admin().indices().prepareCreate("twitter")
                .addMapping("tweet", "{\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"keyword\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }")
                .get();
    }
    public static void main(String[] args) throws IOException {
        new ESPrCarDataIn0101().indexPrCarDataIn0101();
//        new ESPrCarDataIn0101().test();
    }

}
