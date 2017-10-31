package core.search.kkHourlyData;
import com.alibaba.fastjson.JSON;
import dao.ODPair;
import dao.TimeSeg;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import util.ESUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by Fairy_LFen on 2017/3/5.
 *
 */
public class ODPairSearcher {
    private ESUtil esUtil = ESUtil.getInstance();
    private TransportClient client = null;
    private FileUtil fileUtil = FileUtil.getInstance();
    private List<ODPair> odPair_list = new ArrayList<ODPair>();

    private String inPath = "KKDataES\\data\\AllPlateNumberIn0101.json";
    private String outPath = "KKDataES\\data\\ODPairIn0101.json";

    public ODPairSearcher() throws UnknownHostException {
    }

    public void searchODPair() throws IOException, ParseException {
        File file = new File(inPath);
        String jsonStr = fileUtil.readJsonFileToStr(file);
        List<String>  list = JSON.parseArray(jsonStr, String.class);

        client = esUtil.getTransportClient();
        for(String plateNumberStr : list){
            searchByPlateNumber(plateNumberStr);
        }
        writeODPairListToFile();
        client.close();
    }

    private void writeODPairListToFile() {
        String jsonStr = JSON.toJSONString(odPair_list);
        fileUtil.saveToFile(outPath, jsonStr, true);
    }

    private void searchByPlateNumber(String plateNumberStr) throws ParseException {
        SearchResponse searchResponse = client.prepareSearch("kkdata_201601_index")
                .setTypes("PrCarDataIn0101_type")
                .setQuery(QueryBuilders.termQuery("plateNumber", plateNumberStr))
                .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from(TimeSeg.getSeg_01_time()).to(TimeSeg.getSeg_02_time()))
                .addSort("stayPointDate", SortOrder.ASC)
                .get();

        SearchHit[] hit_arry = searchResponse.getHits().getHits();
        System.out.println("hit_arry.length : " + hit_arry.length);
        if(hit_arry.length > 0){
            Map<String, Object> o_source_map = hit_arry[0].getSource();
            Map<String, Object> d_source_map = hit_arry[hit_arry.length - 1].getSource();

            System.out.println((String)o_source_map.get("kkid")+"   "+(String) o_source_map.get("direction"));
            String o_lnglat = searchLnglatByKKId((String)o_source_map.get("kkid"), (String) o_source_map.get("direction"));
            String d_lnglat = searchLnglatByKKId((String)d_source_map.get("kkid"), (String) d_source_map.get("direction"));

            if(o_lnglat != null && d_lnglat != null){
                System.out.println(o_lnglat + " " + d_lnglat);
//                ODPair odPair = new ODPair(o_source_map, d_source_map, o_lnglat, d_lnglat);
//                odPair.setPlateNumber(plateNumberStr);
//                odPair_list.add(odPair);
            }
        }
    }
    private String searchLnglatByKKId(String kkid, String direction) {
      try{
          QueryBuilder qb =  QueryBuilders.boolQuery()
                  .must(QueryBuilders.prefixQuery("kkId", kkid))
                  .must(QueryBuilders.prefixQuery("fxbh", direction));

          SearchResponse searchResponse = client.prepareSearch("kkinfo_index")
                  .setTypes("kkInfo_type")
                  .setQuery(qb)
                  .execute()
                  .actionGet();

          SearchHit[] hit_arry = searchResponse.getHits().getHits();
          String resultStr ;
          if(hit_arry.length > 0){
              Map<String, Object> source_map = hit_arry[0].getSource();
              String latStr = (String) source_map.get("lat");
              String lngStr = (String) source_map.get("lng");
              if(latStr.equals("") || lngStr.equals("")){
                  resultStr = null;
              }
              else{
                  resultStr = lngStr+","+latStr;
              }
          } else resultStr = null;
          return resultStr;
      }catch(Exception e){
          return null;
      }

    }

    public static void main(String[] args) throws IOException, ParseException {
        new ODPairSearcher().searchODPair();
    }
}
