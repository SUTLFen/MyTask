package core.es.ODLayoutNew;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.KKInfoNew;
import dao.ODPair;
import dao.ODPairNew;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import util.DateUtil;
import util.ESUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Fairy_LFen on 2017/5/14.
 */
public class ODLayoutDataGenerator {

    private String rawPath_kkClust = "KKDataES\\data\\core_kkFeatureVector\\gps\\kk_dbscan_result_v_gps.json";

    private String rawPath_allPlateNumber = "KKDataES\\data\\OD_layout\\ODLayoutNew\\data_0101\\AllPlateNumberIn0101_5.json";
    private String outPath_ODPair = "KKDataES\\data\\OD_layout\\ODLayoutNew\\data_0101\\ODPair_0101_seg_02.json";

    private final String indexName = "kkdata_20160101_5_v2";
    private final String typeName = "PrCarDataIn0101_5_v2";
//    private final String timePeriod = "06:00:00-10:00:00";
//    private final String timePeriod = "16:00:00-20:00:00";
    private final String timePeriod = "10:00:00-16:00:00";
//    private final String timePeriod = "20:00:00-06:00:00";

    private ESUtil esUtil = ESUtil.getInstance();
    private FileUtil fileUtil = FileUtil.getInstance();
    private DateUtil dateUtil = DateUtil.getInstance();
    TransportClient client = esUtil.getTransportClient();

    public ODLayoutDataGenerator() throws UnknownHostException {
    }

    public void generateODLayoutData() throws IOException, ParseException {
        ArrayList<ArrayList<KKInfoNew>> clusters = toClusts(rawPath_kkClust);
        List<String> plateNumberList = collectAllPlateNumber(rawPath_allPlateNumber);
        List<ODPair> odPairList = searchAllODPairs(timePeriod, plateNumberList, client);

        ODPairNew[][] resultArry = toODPairNew(odPairList, clusters);

        printResultArry(resultArry);

        String jsonStr = JSON.toJSONString(resultArry);
        fileUtil.saveToFile(outPath_ODPair, jsonStr, false);
    }

    private void printResultArry(ODPairNew[][] resultArry) {
        System.out.println("----------------------------result------------------------------");
        for (int i = 0 ; i < resultArry.length; i++){
            for (int j = 0; j < resultArry[i].length; j++) {
                System.out.print(resultArry[i][j].getWeight() + "  ");
            }
            System.out.println();
        }
    }

    private ODPairNew[][] toODPairNew(List<ODPair> odPairList, ArrayList<ArrayList<KKInfoNew>> clusters) throws ParseException {
        ODPairNew[][] odPairNewArry = new ODPairNew[clusters.size()][clusters.size()];
        for (int i = 0; i < odPairNewArry.length; i++) {
            for (int j = 0; j < odPairNewArry.length; j++) {
                odPairNewArry[i][j] = new ODPairNew();
                odPairNewArry[i][j].setO_kk_index(i);
                odPairNewArry[i][j].setD_kk_index(j);
            }
        }

        List<Long> durationList = null;
        for (ODPair odPairTmp : odPairList) {
            String o_kkid = odPairTmp.getO_kkid();
            String d_kkid = odPairTmp.getD_kkid();

            int o_index = toClustIndex(o_kkid, clusters);
            int d_index = toClustIndex(d_kkid, clusters);


            System.out.println("o_index : " + o_index);
            System.out.println("d_index : " + d_index);

            if(o_index >= 0 && d_index >= 0){
                ODPairNew odPairNew = odPairNewArry[o_index][d_index];
//                odPairNew.setO_kk_index(o_index);
//                odPairNew.setD_kk_index(d_index);
                long duration = calDuration(odPairTmp.getO_time(), odPairTmp.getD_time());
                durationList = odPairNew.getDurationList();
                durationList.add(duration);
                odPairNew.weight++;
            }
        }
        return odPairNewArry;
    }

    private long calDuration(String o_time, String d_time) throws ParseException {
        long o_time_long  = dateUtil.getTime(o_time);
        long d_time_long  = dateUtil.getTime(d_time);
        return (d_time_long - o_time_long)/1000;
    }

    private int toClustIndex(String kkid, ArrayList<ArrayList<KKInfoNew>> clusters) {
        ArrayList<KKInfoNew> clust = null;
        String kkIdTmp = null;
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            for (KKInfoNew kkInfoNewTmp: clust) {
                kkIdTmp = kkInfoNewTmp.getKkId();
                if(kkIdTmp.equals(kkid)){
                    return i;
                }
            }
        }
        return -1;
    }

    private ArrayList<ArrayList<KKInfoNew>> toClusts(String rawPath) throws IOException {
        File clustsFile = new File(rawPath_kkClust);
        String str = fileUtil.readJsonFileToStr(clustsFile);
        ArrayList<ArrayList<KKInfoNew>> result = JSON.parseObject(str, new TypeReference<ArrayList<ArrayList<KKInfoNew>>>(){});
        return result;
    }

    private List<String> collectAllPlateNumber(String rawPath_allPlateNumber) throws IOException {
//        File plateNumberFile = new File(outPath_allPlateNumber);
//        List<String> plateNumberList = null;
//        if(!plateNumberFile.exists()){
//            plateNumberList = collectAllPlateNumber(client, indexName, typeName);
//            String str = JSON.toJSONString(plateNumberList);
//            fileUtil.saveToFile(outPath_allPlateNumber, str, false);
//        }
//        else {
//            plateNumberList = toPlateNumberList(plateNumberFile);
//        }
//
//        return plateNumberList;
        File plateNumberFile = new File(rawPath_allPlateNumber);
        String str = fileUtil.readJsonFileToStr(plateNumberFile);
        List<String> plateNumberList = JSON.parseArray(str, String.class);
        return plateNumberList;

    }



    private List<ODPair> searchAllODPairs(String timePeriod, List<String> plateNumberList, TransportClient client) {
        System.out.println(timePeriod);
        String[] timeArry = timePeriod.split("-");
        String startTime = MessageFormat.format("2016-01-01 {0}", timeArry[0]);
        String endTime = MessageFormat.format("2016-01-01 {0}", timeArry[1]);

        String plateNumberTmp = null;
        SearchResponse sr = null;
        List<ODPair> odPairList = new ArrayList<ODPair>();

        for (int i = 0; i < plateNumberList.size(); i++) {
            plateNumberTmp =  plateNumberList.get(i);
            sr = client.prepareSearch(indexName)
                    .setTypes(typeName)
                    .setQuery(QueryBuilders.termQuery("plateNumber", plateNumberTmp))
                    .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from(startTime).to(endTime))
                    .addSort(SortBuilders.fieldSort("stayPointDate").order(SortOrder.ASC))
                    .setSize(100)
                    .get();

            SearchHit[] hits = sr.getHits().getHits();
            printInfo(hits);
            if(hits.length >= 2){
                Map<String, Object> o_stayPoint = hits[0].getSource();
                Map<String, Object>  d_stayPoint = hits[1].getSource();
                ODPair odPair = new ODPair(plateNumberTmp, o_stayPoint, d_stayPoint);
                odPairList.add(odPair);
            }
        }
        return odPairList;
    }

    private void printInfo(SearchHit[] hits) {
        System.out.println("-----------------------------");
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSource();
            System.out.println((String) source.get("stayPointDate"));
        }
    }

    private List<String> toPlateNumberList(File plateNumberFile) throws IOException {
        String str = fileUtil.readJsonFileToStr(plateNumberFile);
        List<String> plateNumberList = JSON.parseArray(str, String.class);
        return plateNumberList;
    }

    private List<String> collectAllPlateNumber(TransportClient client, String indexName, String typeName) {
        List<String> plateNumberList = new ArrayList<String>();
        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation( AggregationBuilders.terms("group_by_plateNumber").field("plateNumber").size(10000))
                .setScroll(new TimeValue(60000))
                .setSize(10000)
                .get();

        Terms agg1 = searchResponse.getAggregations().get("group_by_plateNumber");
        List<Terms.Bucket> buckets = agg1.getBuckets();
        do{
            System.out.println(buckets.size());
            for (Terms.Bucket bucket : buckets){
                String keystr = bucket.getKeyAsString();
//                System.out.println(keystr);
                plateNumberList.add(keystr);
            }

            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(60000))
                    .execute()
                    .actionGet();
        }while(searchResponse.getHits().getHits().length != 0);
        return plateNumberList;
    }



    public static void main(String[] args) throws IOException, ParseException {
        new ODLayoutDataGenerator().generateODLayoutData();
    }
}
