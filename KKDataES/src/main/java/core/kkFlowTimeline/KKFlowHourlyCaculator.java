package core.kkFlowTimeline;

import util.DateUtil;
import util.ESUtil;

import java.net.UnknownHostException;
import java.util.List;

public class KKFlowHourlyCaculator {

    private String outPath = "KKDataES\\data\\core-kkFlowTimeline";
    private final String dayStr = "2016-01-04";

    private DateUtil dateUtil = DateUtil.getInstance();
    private ESUtil esUtil = ESUtil.getInstance();

    public void calKKHourlyFlow() throws UnknownHostException {
        List<String> hourlyStrs = dateUtil.getHourlyTimeStrList(dayStr);

        String startTime, endTime;
        for (int i = 0; i < hourlyStrs.size(); i += 2) {
            startTime = hourlyStrs.get(i);
            endTime = hourlyStrs.get(i + 1);

            esUtil.calCountByHourlyTime("kkdata_20160104_%5", "PrCarDataIn010",
                startTime, endTime);

        }
    }

    public static void main(String[] args) throws UnknownHostException {
        new KKFlowHourlyCaculator().calKKHourlyFlow();
    }

}
