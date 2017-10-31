package core.od.layout;
import com.alibaba.fastjson.JSON;
import dao.ODPair;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
/**
 * Created by Fairy_LFen on 2017/3/7.
 * core.convertor ODPair_0104.json to data format for d3.ForceBundle usage
 * 用于绘制边绑定图
 */
public class ODPairJSONConvertor {
//    private String rawDataPath = "KKDataES\\data\\ODPair_0104_seg_01_new_01.json";
    private String rawDataPath = "KKDataES\\data_0101\\ODPair_0101_seg_01_new_01.json";

    private String outDataPath_nodes = "KKDataES\\data_0101\\nodes_0101.json";
    private String outDataPath_links = "KKDataES\\data_0101\\links_0101.json";

    private FileUtil fileUtil = FileUtil.getInstance();
    private Map<String, Integer> nodes_map_tmp = new HashMap<String, Integer>();   //key-lnglatStr, value-"flag"

    public void convertODPairJsonFile() throws IOException {
        File rawFile = new File(rawDataPath);
        String str = fileUtil.readJsonFileToStr(rawFile);
        List<ODPair> odPair_list = JSON.parseArray(str, ODPair.class);
        extractNodes(odPair_list);
        extractLinks(odPair_list);
    }

    private void extractLinks(List<ODPair> odPair_list) {
        List<GeoLink> linkResult_list = new ArrayList<GeoLink>();
        for (ODPair odPair : odPair_list){
            String o_lnglat = odPair.getO_lnglat();
            String d_lnglat = odPair.getD_lnglat();

            String o_flag = nodes_map_tmp.get(o_lnglat) + "";
            String d_flag = nodes_map_tmp.get(d_lnglat) + "";

            GeoLink geoLink = new GeoLink(o_flag, d_flag);
            linkResult_list.add(geoLink);
        }
        String jsonStr = JSON.toJSONString(linkResult_list);
        fileUtil.saveToFile(outDataPath_links, jsonStr, true);
    }

    private void extractNodes(List<ODPair> odPair_list) {
        int flag = 0;
        List<String> lngLat_list = new ArrayList<String>();
        List<GeoNode> nodeResult_list = new ArrayList<GeoNode>();
        for(ODPair odPair : odPair_list){
            String o_lnglat = odPair.getO_lnglat();
            String d_lnglat = odPair.getD_lnglat();

            if(!lngLat_list.contains(o_lnglat)) {
                lngLat_list.add(o_lnglat);
                toAddListAndMap(o_lnglat, nodeResult_list, flag);
                flag++;
            }
            if(!lngLat_list.contains(d_lnglat)) {
                lngLat_list.add(d_lnglat);
                toAddListAndMap(d_lnglat, nodeResult_list, flag);
                flag++;
            }
        }

        String jsonStr = JSON.toJSONString(nodeResult_list, true);
        fileUtil.saveToFile(outDataPath_nodes, jsonStr, true);
    }

    private void toAddListAndMap(String lngLatStr, List<GeoNode> nodeResult_list, int flag) {
            double[] lngLat = newLngLat(lngLatStr);
            GeoNode geoNode = new GeoNode(flag, lngLat[0], lngLat[1]);
            nodeResult_list.add(geoNode);
            nodes_map_tmp.put(lngLatStr, flag);
    }

    private double[] newLngLat(String lnglat) {
        String[] str_arry = lnglat.split(",");
        double lng = Double.valueOf(str_arry[0]);
        double lat = Double.valueOf(str_arry[1]);
        return new double[]{lng, lat};
    }

    public static void main(String[] args) throws IOException {
        new ODPairJSONConvertor().convertODPairJsonFile();
    }
}
