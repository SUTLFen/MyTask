package dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fairy_LFen on 2017/5/13.
 */
public class ODPairNew {
    private int o_kk_index ; // 起点卡口所在的聚簇
    private int d_kk_index ; // 终点卡口所在的聚簇
    public int weight;
    private List<Long> durationList = new ArrayList<Long>();  //起始点之间花费的时间

    public void setDurationList(List<Long> durationList) {
        this.durationList = durationList;
    }

    public List<Long> getDurationList() {
        return durationList;
    }

    public int getO_kk_index() {
        return o_kk_index;
    }

    public void setO_kk_index(int o_kk_index) {
        this.o_kk_index = o_kk_index;
    }

    public int getD_kk_index() {
        return d_kk_index;
    }

    public void setD_kk_index(int d_kk_index) {
        this.d_kk_index = d_kk_index;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
