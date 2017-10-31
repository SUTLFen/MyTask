package util;

import java.text.DecimalFormat;

/**
 * Created by Fairy_LFen on 2017/3/12.
 */
public class NumberUtil {
    private static NumberUtil instance = null;
    private NumberUtil(){}
    public static NumberUtil getInstance(){
        if(instance == null){
            instance = new NumberUtil();
        }
        return instance;
    }

    public double save6Decimal(double value_d){
        DecimalFormat decimalFormat = new DecimalFormat("0.000000");
        String result_str = decimalFormat.format(value_d);
        System.out.println(result_str);
        double result_d = Double.valueOf(result_str);
        return result_d;
    }

    public static void main(String[] args) {
        NumberUtil numberUtil = NumberUtil.getInstance();
        System.out.println(numberUtil.save6Decimal(0.1234567));
    }
}
