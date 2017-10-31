package core.search;
import dao.KKInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WrapperQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import util.ESUtil;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by Fairy_LFen on 2017/3/4.
 */

public class ESSearchTest {
    private ESUtil esUtil = ESUtil.getInstance();
    private TransportClient client = esUtil.getTransportClient();

    public ESSearchTest() throws UnknownHostException {
    }

    public void queryStringTest(){
        String queryString = "\"match\": {\n" +
                "      \"last_name\": \"Smith\"\n" +
                "    }";
        WrapperQueryBuilder builder = QueryBuilders.wrapperQuery(queryString);
        SearchResponse searchRequestBuilder = client.prepareSearch("megacorp")
                .setTypes("employee")
                .setQuery(builder)
                .execute()
                .actionGet();
        client.close();
    }

    public void matchAllQueryTest(){
        QueryBuilder qb = QueryBuilders.matchAllQuery();
        SearchResponse searchResponse = client.prepareSearch("kkdata_20160104_5")
                .setTypes("PrCarDataIn0104_5")
                .setQuery(qb)
                .setScroll(new TimeValue(60000))
                .setSize(10000)
                .execute()
                .actionGet();

        SearchHit[] hits = searchResponse.getHits().getHits();
        do{
            System.out.println(hits.length);
            for (SearchHit hit : hits) {
                Map<String, Object> source_map = hit.getSource();
            }

            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(60000))
                    .execute()
                    .actionGet();
        }while(searchResponse.getHits().getHits().length != 0);
        client.close();
    }


    public void termQueryTest(){
        SearchResponse searchResponse = client.prepareSearch("megacorp_02")
                .setTypes("employee_01")
                .setQuery(QueryBuilders.termQuery("last_name", "Smith"))
                .get();
        SearchHit[] hit_arry = searchResponse.getHits().getHits();
        System.out.println(hit_arry.length);
//        int flag = 0;
//        for(int i = 0; i< hit_arry.length; i++){
//            Map<String, Object> source_map = hit_arry[i].getSource();
//        }

        client.close();
    }
    public void rangeAndSortTest(){
        SearchResponse searchResponse = client.prepareSearch("kkdata_index")
                .setTypes("prCarTraj_type")
//                .setQuery(QueryBuilders.wildcardQuery("last_name", "Smit?"))
//                .setQuery(QueryBuilders.fuzzyQuery("last_name", "S*"))
                .setQuery(QueryBuilders.termQuery("plateNumbe", "浙AP5M12"))
//                .setQuery(QueryBuilders.matchAllQuery())
//                .setPostFilter(QueryBuilders.rangeQuery("age").from(20).to(33))
//                .addSort("age", SortOrder.ASC)
                .get();
//                .execute()
//                .actionGet();

        SearchHit[] hit_arry = searchResponse.getHits().getHits();
        System.out.println("hit_arry.length : " + hit_arry.length);
//        for(int i = 0; i< hit_arry.length; i++){
//            Map<String, Object> source_map = hit_arry[i].getSource();
//            String first_name = (String) source_map.get("first_name");
//            String last_name = (String) source_map.get("last_name");
//            int age = (Integer) source_map.get("age");
//            System.out.println(first_name+" "+last_name + " : " + age);
//        }
        client.close();
    }

/**
 * boolQuery
 * **/
    public void stringDateTest(){
        KKInfo kkInfo = new KKInfo();
        kkInfo.setFxbh("04");
        kkInfo.setKkId("310003000098");
        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(termQuery("kkid", kkInfo.getKkId()))
                .must(termQuery("direction", kkInfo.getFxbh()));
        SearchResponse searchResponse = client.prepareSearch("kkdata_20160104_5")
                .setTypes("PrCarDataIn0104_5")
                .setQuery(qb)
                .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from("2016-01-04 00:07:03").to("2016-01-04 00:59:59"))
                .get();
        SearchHit[] hit_arry = searchResponse.getHits().getHits();
    }

//**** aggregation test
    public void aggregationTest(){
        SearchResponse searchResponse = client.prepareSearch("kkdata_20160104_5")
                .setTypes("PrCarDataIn0104_5")
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation( AggregationBuilders.terms("group_by_plateNumber").field("plateNumber").size(1000000))
                .setSize(1000000)
                .get();


        SearchHit[] hit_arry = searchResponse.getHits().getHits();
        Terms agg1 = searchResponse.getAggregations().get("group_by_plateNumber");
        List<Terms.Bucket> buckets = agg1.getBuckets();

        System.out.println(buckets.size());
        for (Terms.Bucket bucket : buckets) {
            String keystr = bucket.getKeyAsString();
            System.out.println(keystr);
        }

    }

    public static void main(String[] args) throws UnknownHostException {
//        new ESSearchTest().rangeAndSortTest();
//        new ESSearchTest().termQueryTest();
//        new ESSearchTest().stringDateTest();
        new ESSearchTest().matchAllQueryTest();
//        new ESSearchTest().aggregationTest();
    }
}
