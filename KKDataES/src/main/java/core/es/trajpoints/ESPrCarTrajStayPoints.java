package core.es.trajpoints;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.IndivisualPrCarInfo;
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
import java.util.Map;
import java.util.Set;

/**
 * Created by Fairy_LFen on 2017/2/28.
 */
public class ESPrCarTrajStayPoints {

    private String rawDataPath = "KKDataES\\data\\PrCarTrajStayPoints.json";
    private FileUtil fileUtil = FileUtil.getInstance();

    public void indexPrCarTrajStayPoints() throws IOException {
        TransportClient client = newTransportClient();
        File rawDataFile = new File(rawDataPath);
        String str = fileUtil.readJsonFileToStr(rawDataFile);
        Map<String, IndivisualPrCarInfo> map = JSON.parseObject(str, new TypeReference<Map<String, IndivisualPrCarInfo>>(){});

        Set<String> keySet = map.keySet();
        for(String keySetItem : keySet){
            IndivisualPrCarInfo indivisualPrCarInfo = map.get(keySetItem);
            String jsonStr = JSON.toJSONString(indivisualPrCarInfo);
            IndexResponse response = client.prepareIndex("kkdata_index", "prCarTraj_20160101_type")
                    .setSource(jsonStr)
                    .get();
        }
        client.close();
    }

    private TransportClient newTransportClient() throws UnknownHostException {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        return client;
    }

    public static void main(String[] args) throws IOException {
        new ESPrCarTrajStayPoints().indexPrCarTrajStayPoints();
    }
}
