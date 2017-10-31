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
 * Created by Fairy_LFen on 2017/3/6.
 * 建立ES索引：数据来源2016-01-04 1/5的数据。。。
 */
public class ESPrCarDataIn0104 {
//    private String rawDataPath = "KKDataES\\data\\PrCarDataIn0104_%5.json";
//    private String indexName = "kkdata_20160104_5";
//    private String typeName = "PrCarDataIn0104_5";

    private String rawDataPath = "KKDataES\\data\\es_kkdata\\PrCarDataIn0101.json";
    private String indexName = "kkdata_20160101_5_v2";
    private String typeName = "PrCarDataIn0101_5_v2";

    private FileUtil fileUtil = FileUtil.getInstance();

    public void indexPrCarDataIn0104() throws IOException {
        TransportClient client = newTransportClient();
        List<StayPointInfo> list = newESSourceData(rawDataPath);
        System.out.println(list.size());
        String mapping_str = "{\n" +
                "    \""+typeName+"\": { \n" +
                "      \"_all\":       { \"enabled\": false  }, \n" +
                "      \"properties\": { \n" +
                "        \"direction\":    { \"type\": \"keyword\"  }, \n" +
                "        \"kkid\":     { \"type\": \"keyword\"  }, \n" +
                "        \"plateColor\"  :   { \"type\": \"keyword\" },\n" +
                "        \"plateNumber\"  :   { \"type\": \"keyword\" },\n" +
                "        \"speed\": {\"type\": \"integer\"},\n" +
                "        \"stayPointDate\": {\n" +
                "          \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\n" +
                "          \"type\": \"date\"},\n" +
                "        \"stayPointDuration\": {\n" +
                "          \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis\",\n" +
                "          \"type\": \"date\"},\n" +
                "        \"vehicleColor\": {\"type\": \"keyword\"},\n" +
                "        \"vehicleType\": {\"type\": \"keyword\"}\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        client.admin().indices().prepareCreate(indexName)
                .addMapping(typeName, mapping_str)
                .get();
        int flag = 0;
        for(StayPointInfo stayPointInfo : list){
            System.out.println(flag++);
            String jsonStr = JSON.toJSONString(stayPointInfo);
            IndexResponse response = client.prepareIndex(indexName, typeName)
                    .setSource(jsonStr)
                    .get();
        }
        client.close();
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


    public static void main(String[] args) throws IOException {
        new ESPrCarDataIn0104().indexPrCarDataIn0104();
    }
}
