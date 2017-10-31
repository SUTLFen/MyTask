package confusion;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sun.deploy.util.StringUtils;
import dao.KKInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import util.DateUtil;
import util.ESUtil;
import util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Created by Fairy_LFen on 2017/4/7.
 */
public class MatrixCreator_D {
    private String rawPath_region = "KKDataES\\data\\confusion\\kk_dbscan_result_index.json";
    private String outPath = "KKDataES\\data\\confusion\\D_KKFlow.txt";
    private final String indexName = "kkdata_20160104_5";
    private final String typeName = "PrCarDataIn0104_5";


    private FileUtil fileUtil = FileUtil.getInstance();
    private DateUtil dateUtil = DateUtil.getInstance();
    private ESUtil esUtil = ESUtil.getInstance();
    private     TransportClient client = esUtil.getTransportClient();

    public MatrixCreator_D() throws UnknownHostException {
    }

    public void createMatrixD() throws IOException {
        Map<Integer, ArrayList<KKInfo>> regions_map = collectAllHangzhouKKInfo(rawPath_region);
        String startTime, endTime;
        File outFile = new File(outPath);
        BufferedWriter bw = fileUtil.getBufferedWriter(outFile);
        int[] flowOfRegions_row;
        String rowStr;
        for (int i = 0; i < 24; i++) {
            startTime = dateUtil.newStartTime("2016-01-04", i);
            endTime = dateUtil.newEndTime("2016-01-04", i);
//            矩阵的某一行
            System.out.println("------ "+i+" ----------------------------");
            flowOfRegions_row = flowsWithinSpecifiedTime(startTime, endTime, regions_map);
            rowStr = toStr(flowOfRegions_row);

            bw.append(rowStr);
            bw.append("\n");
            bw.flush();
        }
        bw.close();
    }
    private String toStr(int[] numsOfPois) {
        List<String> strs = new ArrayList<String>();
        for (int i = 0; i < numsOfPois.length; i++) {
            strs.add(numsOfPois[i] + "");
        }
        String str = StringUtils.join(strs, " ");
        return str;
    }

/**
 * 矩阵D的某一行。。。
 * */
    private int[] flowsWithinSpecifiedTime(String startTime, String endTime, Map<Integer, ArrayList<KKInfo>> regions_map) throws UnknownHostException {
        int[] rowFlowOfRegions = new int[regions_map.size()];
        ArrayList<KKInfo> region = null;
        for (int i = 0; i < regions_map.size(); i++) {
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%");
            region = regions_map.get(i);
            rowFlowOfRegions[i] =  searchWithinSpecTimeAndRegion(startTime, endTime, region);
        }
        return rowFlowOfRegions;
    }
/**
 * 搜索区域的车流量；
 * */
    protected int searchWithinSpecTimeAndRegion(String startTime, String endTime, ArrayList<KKInfo> region) throws UnknownHostException {
        SearchResponse searchResponse;
        QueryBuilder qb = null;
        int sum = 0;
        for (KKInfo kkInfo:region) {
            qb = QueryBuilders.boolQuery()
                    .must(termQuery("kkid", kkInfo.getKkId()))
                    .must(termQuery("direction", kkInfo.getFxbh()));
            searchResponse = client.prepareSearch(indexName)
                    .setTypes(typeName)
                    .setQuery(qb)
                    .setPostFilter(QueryBuilders.rangeQuery("stayPointDate").from(startTime).to(endTime))
                    .get();
            SearchHit[] hit_arry = searchResponse.getHits().getHits();
            System.out.print(hit_arry.length+"  ");
            sum += hit_arry.length;
        }
        System.out.println();
        System.out.println("sum : " + sum);
        return sum;
    }

    private Map<Integer, ArrayList<KKInfo>> collectAllHangzhouKKInfo(String rawPath_region) throws IOException {
        File file = new File(rawPath_region);
        String jsonStr = fileUtil.readJsonFileToStr(file);
        Map<Integer, ArrayList<KKInfo>> regions_map = JSON.parseObject(jsonStr,
                new TypeReference<Map<Integer, ArrayList<KKInfo>>>(){});
        return regions_map;
    }

    public static void main(String[] args) throws IOException {
        new MatrixCreator_D().createMatrixD();
    }
}
