package core.search.kkHourlyData;

/**
 * Created by Fairy_LFen on 2017/3/9.
 */
public class CountObject {
    private int date ;
    private int count;

    public CountObject() {
    }

    public CountObject(int i, Integer count) {
        this.date = i ;
        this.count = count;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
