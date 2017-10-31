package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Fairy_LFen on 2017/3/5.
 */
public class TimeSeg {
    public static long getSeg_01_time() throws ParseException {
        String seg_str = "2016-01-01 06:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(seg_str);
        return date.getTime();
    }
    public static long getSeg_02_time() throws ParseException {
        String seg_str = "2016-01-01 10:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(seg_str);
        return date.getTime();
    }
}
