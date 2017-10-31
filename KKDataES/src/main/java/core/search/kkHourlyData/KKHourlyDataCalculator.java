package core.search.kkHourlyData;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import util.ESUtil;
import util.FileUtil;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/3/8.
 */
public class KKHourlyDataCalculator {
    private String indexName = "kkdata_20160104_5";
    private String typeName = "PrCarDataIn0104_5";

    private ESUtil esUtil = ESUtil.getInstance();
    private FileUtil fileUtil = FileUtil.getInstance();
    private TransportClient client = null;

    private List<KKInfoTemp> list  = new ArrayList<KKInfoTemp>();
    {
        list.add(new KKInfoTemp("天目山路紫金港路_由北向南", "310003000276", "04"));
        list.add(new KKInfoTemp("天目山路紫金港路_由东向西", "310003000276", "01"));
        list.add(new KKInfoTemp("天目山路紫金港路_由西向东", "310003000276", "02"));
        list.add(new KKInfoTemp("天目山路紫金港路_由南向北", "310003000276", "03"));

        list.add(new KKInfoTemp("天目山路古墩路_由北向南", "310003000022", "04"));
        list.add(new KKInfoTemp("天目山路古墩路_由东向西", "310003000022", "01"));
        list.add(new KKInfoTemp("天目山路古墩路_由西向东", "310003000022", "02"));
        list.add(new KKInfoTemp("天目山路古墩路_由南向北", "310003000022", "03"));

        list.add(new KKInfoTemp("留和路留泗路_由北向南", "310003000112", "04"));
        list.add(new KKInfoTemp("留和路留泗路_由东向西", "310003000112", "01"));
        list.add(new KKInfoTemp("留和路留泗路_由西向东", "310003000112", "02"));

        list.add(new KKInfoTemp("杭州绕城高速卡口下沙_进城", "310003000171", "17"));
        list.add(new KKInfoTemp("杭州绕城高速卡口下沙_出城", "310003000172", "18"));

        list.add(new KKInfoTemp("艮山东路彭埠上高速_由西向东", "310003000052", "02"));
        list.add(new KKInfoTemp("艮山东路彭埠上高速_由东向西", "310003000052", "01"));

        list.add(new KKInfoTemp("时代高架滨文路_下匝道", "310003000160", "20"));
        list.add(new KKInfoTemp("时代高架滨文路_上匝道", "310003000160", "19"));

        list.add(new KKInfoTemp("留和路石马路_由北向南", "310003000204", "04"));
        list.add(new KKInfoTemp("留和路石马路_由南向北", "310003000204", "03"));
        list.add(new KKInfoTemp("留和路石马路_由南向北", "310003000204", "03"));
    }
    private List<String> time_list = esUtil.getHourlyTimeList();

    public void  calculator() throws UnknownHostException {
        client = esUtil.getTransportClient();
        String outPath = null;
        List<Integer> result_list = new ArrayList<Integer>();
        //每个卡口
        for(KKInfoTemp kkInfoTemp: list) {
            outPath = String.format("KKDataES\\data_kk_hourly\\%s_%s.json", kkInfoTemp.getKkName(), kkInfoTemp.getFxbh());
            result_list.clear();

            QueryBuilder qb = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termQuery("kkid", kkInfoTemp.getKkId()))
                    .must(QueryBuilders.termQuery("direction", kkInfoTemp.getFxbh()));

            //循环遍历24小时。。。
            for(int i = 0 ; i < 48 ; i+=2){
                String time_start = time_list.get(i);
                String time_end = time_list.get(i + 1);
                SearchResponse searchResponse = client.prepareSearch(indexName)
                        .setTypes(typeName)
                        .setQuery(qb)
                        .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from(time_start).to(time_end))
                        .setSize(2000)
                        .get();
                SearchHit[] hit_arry = searchResponse.getHits().getHits();
                result_list.add(hit_arry.length);
            }
            String jsonStr = JSON.toJSONString(result_list);
            fileUtil.saveToFile(outPath, jsonStr, true);
        }
        client.close();
    }

    public static void main(String[] args) throws UnknownHostException {
        new KKHourlyDataCalculator().calculator();
    }
}
