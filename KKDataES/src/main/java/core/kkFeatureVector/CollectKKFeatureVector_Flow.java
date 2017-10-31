package core.kkFeatureVector;

import com.alibaba.fastjson.JSON;
import dao.KKInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import util.DateUtil;
import util.ESUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by Fairy_LFen on 2017/4/24.
 * 搜集卡口24小时流量，并构成特征向量。
 */
public class CollectKKFeatureVector_Flow {

    private String kkInfoPath = "KKDataES\\data\\core_kkFeatureVector\\allKKInfo.json";
    private String outPath = "KKDataES\\data\\core_kkFeatureVector\\flow\\kk_feature_vector_flow.json";   //只含卡口流量的特征向量
    private Map<KKInfo, int[]> resultMap = new HashMap<KKInfo, int[]>();

    private FileUtil fileUtil = FileUtil.getInstance();
    private DateUtil dateUtil = DateUtil.getInstance();
    private ESUtil esUtil = ESUtil.getInstance();

    public CollectKKFeatureVector_Flow() throws UnknownHostException {
    }

    public void collectKKFlow() throws IOException {
        TransportClient client = esUtil.getTransportClient();
        List<KKInfo> kkInfoList = toKKInfoList(kkInfoPath);
        for (KKInfo kkInfoTmp: kkInfoList) {
            int[] hourlyFlow = collectKKHourlyFlow(kkInfoTmp, client);
            resultMap.put(kkInfoTmp, hourlyFlow);
        }
        client.close();

        String jsonStr = JSON.toJSONString(resultMap);
        fileUtil.saveToFile(outPath, jsonStr, false);
    }

    private int[] collectKKHourlyFlow(KKInfo kkInfoTmp, TransportClient client) {
        int[] flowVector = new int[24];
        for (int hour = 0; hour < 24; hour++) {
            String startTime = dateUtil.newStartTime("2016-01-04", hour);
            String endTime = dateUtil.newEndTime("2016-01-04", hour);

//            QueryBuilder qb = QueryBuilders.boolQuery()
//                    .must(termQuery("kkid", kkInfoTmp.getKkId()))
//                    .must(termQuery("direction", kkInfoTmp.getFxbh()));

            SearchResponse searchResponse = client.prepareSearch("kkdata_20160104_5")
                    .setTypes("PrCarDataIn0104_5")
                    .setQuery(termQuery("kkid", kkInfoTmp.getKkId()))
                    .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from(startTime).to(endTime))
                    .setSize(9999)
                    .get();

            SearchHit[] hit_arry = searchResponse.getHits().getHits();
            flowVector[hour] = hit_arry.length;
        }
        return flowVector;
    }

    private List<KKInfo> toKKInfoList(String kkInfoPath) throws IOException {
        File file = new File(kkInfoPath);
        String str = fileUtil.readJsonFileToStr(file);
        List<KKInfo> kkInfo_list = JSON.parseArray(str, KKInfo.class);
        return kkInfo_list;
    }

    public static void main(String[] args) throws IOException {
        new CollectKKFeatureVector_Flow().collectKKFlow();
    }
}
