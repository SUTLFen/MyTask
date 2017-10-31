package core.kkFeatureVector;

import com.alibaba.fastjson.JSON;
import dao.KKInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import util.ESUtil;
import util.FileUtil;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by Fairy_LFen on 2017/4/23.
 *
 * 从ES中，搜集卡口信息，卡口ID没有重复
 */
public class CollectAllKKInfo {
    private ESUtil esUtil = ESUtil.getInstance();
   private TransportClient client = esUtil.getTransportClient();
    private FileUtil fileUtil = FileUtil.getInstance();
    public CollectAllKKInfo() throws UnknownHostException {
    }

    public void collectAll(){
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch("kkinfo_index")
                .setTypes("kkInfo_type")
                .setQuery(qb)
                .setSize(10000)
                .execute()
                .actionGet();

        SearchHit[] hit_arry = searchResponse.getHits().getHits();
        int flag = 0;

//        System.out.println(hit_arry.length);

        Map<String, KKInfo> allKKMap = new HashMap<String, KKInfo>();
        Map<String, Object> source_map = null;
        for(int i = 0; i< hit_arry.length; i++){
            source_map = hit_arry[i].getSource();
            KKInfo kkInfo = newKKInfo(source_map);
            String kkId = (String) source_map.get("kkId");
            if(!allKKMap.containsKey(kkId)) {
                allKKMap.put(kkId, kkInfo);
            }
        }
        client.close();

        Set<String> kkid_set = allKKMap.keySet();
        List<KKInfo> allKK_list = new ArrayList<KKInfo>();
        for (String kkid : kkid_set){
            KKInfo kkInfo = allKKMap.get(kkid);
            allKK_list.add(kkInfo);
        }

        String jsonStr = JSON.toJSONString(allKK_list);
        fileUtil.saveToFile("KKDataES\\data\\kkinfoData\\allKKInfo.json", jsonStr, false);
    }

    private KKInfo newKKInfo(Map<String, Object> source_map) {
        KKInfo kkInfo = new KKInfo();

        kkInfo.setKkId((String) source_map.get("kkId"));
        kkInfo.setFxbh((String) source_map.get("fxbh"));
        kkInfo.setLng((String) source_map.get("lng"));
        kkInfo.setLat((String) source_map.get("lat"));
        kkInfo.setKkName((String) source_map.get("kkName"));
        kkInfo.setId((String) source_map.get("id"));
        kkInfo.setKkType((String) source_map.get("kkType"));

        return kkInfo;
    }

    public static void main(String[] args) throws UnknownHostException {
        new CollectAllKKInfo().collectAll();
    }
}
