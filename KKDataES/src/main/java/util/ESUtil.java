package util;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/2/13.
 */
public class ESUtil {
    private static ESUtil esUtil;

    public static ESUtil getInstance(){
        if(esUtil == null){
            esUtil = new ESUtil();
            return esUtil;
        }else{
            return esUtil;
        }
    }

    public int calCountByHourlyTime(String indexName, String typeName, String startTimeStr, String endTimeStr) throws UnknownHostException {
        TransportClient client = this.getTransportClient();

        SearchResponse searchResponse = client.prepareSearch(indexName)
                .setTypes(typeName)
                .setQuery(QueryBuilders.matchAllQuery())
                .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from(startTimeStr).to(endTimeStr))
                .get();

        SearchHit[] hits = searchResponse.getHits().getHits();
        System.out.println(hits.length);
        return 0;
    }


    public TransportClient getTransportClient() throws UnknownHostException {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        return client;
    }

    public List<String> getHourlyTimeList(){
        List<String> time_list = new ArrayList<String>();
        String hourStr = null;
        for(int i = 0; i < 24; i++){
            if(i < 10){
                hourStr = "0" + i;
            }else { hourStr = i+"";}

            String timeStr_start = MessageFormat.format("2016-01-04 {0}:00:00", hourStr);
            String timeStr_end = MessageFormat.format("2016-01-04 {0}:59:59", hourStr);

            time_list.add(timeStr_start);
            time_list.add(timeStr_end);
        }
        return time_list;
    }



}
