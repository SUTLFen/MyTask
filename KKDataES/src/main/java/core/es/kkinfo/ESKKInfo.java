package core.es.kkinfo;

import com.alibaba.fastjson.JSON;
import dao.KKInfo;
import org.elasticsearch.client.transport.TransportClient;
import util.ESUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/2/12.
 */
public class ESKKInfo {
    private final String rawDataPath = "KKDataES\\data\\poi_data\\kk_info_from_gps.json";

    private FileUtil fileUtil = FileUtil.getInstance();
    private ESUtil esUtil = ESUtil.getInstance();

    private TransportClient client = null;

    public void indexKKGeo() throws IOException {
        List<KKInfo> kkInfo_list = toKKInfoList(rawDataPath);
        client = esUtil.getTransportClient();

        String mapping_str = "{\n" +
                "    \"kkInfo_type_new\": { \n" +
                "      \"_all\":       { \"enabled\": false  }, \n" +
                "      \"properties\": { \n" +
                "        \"id\":    { \"type\": \"keyword\"  }, \n" +
                "        \"kkId\":     { \"type\": \"keyword\"  }, \n" +
                "        \"fxbh\"  :   { \"type\": \"keyword\" },\n" +
                "        \"kkType\"  :   { \"type\": \"keyword\" },\n" +
                "        \"kkName\": {\n" +
                "            \"type\": \"text\",\n" +
                "            \"analyzer\": \"ik_max_word\",\n" +
                "            \"search_analyzer\": \"ik_max_word\",\n" +
                "            \"include_in_all\": \"true\",\n" +
                "            \"boost\": 8\n" +
                "        },\n" +
                "        \"lng\": {\"type\": \"keyword\"},\n" +
                "        \"lat\": {\"type\": \"keyword\"}\n" +
                "        }\n" +
                "      }\n" +
                "    }";
        client.admin().indices().prepareCreate("kkinfo_index_new")
                .addMapping("kkInfo_type_new", mapping_str)
                .get();

        for(KKInfo kkInfo : kkInfo_list){
            String jsonStr = JSON.toJSONString(kkInfo);
            client.prepareIndex("kkinfo_index_new", "kkInfo_type_new")
                    .setSource(jsonStr)
                    .get();
        }

        client.close();
    }

    private List<KKInfo> toKKInfoList(String rawDataPath) throws IOException {
        File rawFile = new File(rawDataPath);
        String jsonStr = fileUtil.readJsonFileToStr(rawFile);
        List<KKInfo> kkInfo_list = JSON.parseArray(jsonStr, KKInfo.class);
        return kkInfo_list;
    }

    public static void main(String[] args) throws IOException {
            new ESKKInfo().indexKKGeo();
    }
}

