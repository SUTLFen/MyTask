package core.kkFeatureVector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import dao.KKInfo;
import dao.KKInfoNew;
import util.FileUtil;
import util.NumberUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Fairy_LFen on 2017/5/9.
 */
public class KKEntropyValueCal {
    private String rawPath = "KKDataES\\data\\core_kkFeatureVector\\dbscan_entropy.json";

    FileUtil fileUtil = FileUtil.getInstance();
    private NumberUtil numberUtil = NumberUtil.getInstance();

    public void cal() throws IOException {
        File file = new File(rawPath);
        String str = fileUtil.readJsonFileToStr(file);
        ArrayList<ArrayList<KKInfoNew>> clusters = JSON.parseObject(str, new TypeReference<ArrayList<ArrayList<KKInfoNew>>>(){});

        ArrayList<double[]> argVectorArry = calArgVectorArry(clusters);
        double[] entropyValues = calEntropyValue(argVectorArry);
        System.out.println(Arrays.toString(entropyValues));
    }

    private double[] calEntropyValue(ArrayList<double[]> argVectorArry) {
        double[] entropyValues = new double[argVectorArry.size()];
        for (int j = 0; j < argVectorArry.size(); j++) {
            double[] vector = argVectorArry.get(j);
            for (int i = 0; i < vector.length; i++) {
                if( vector[i] != 0.0f){
                    entropyValues[j] += vector[i] * Math.log(vector[i]);
                }
            }
            entropyValues[j] = -1 * entropyValues[j];
        }
        return entropyValues;
    }

    private ArrayList<double[]> calArgVectorArry(ArrayList<ArrayList<KKInfoNew>> clusters) {
        ArrayList<KKInfoNew> clust = null;
        ArrayList<double[]> argVectorArry = new ArrayList();
        for (int i = 0; i < clusters.size(); i++) {
            clust = clusters.get(i);
            double[] argVector = calArgVectorValue(clust);
            argVectorArry.add(argVector);
        }
        return argVectorArry;
    }

    private double[] calArgVectorValue(ArrayList<KKInfoNew> clust) {
        double[] argVector = new double[20];
        for (KKInfoNew kkInfoNewTmp: clust) {
            double[] vectorTmp = kkInfoNewTmp.getVector();
            for (int i = 0; i <vectorTmp.length; i++) {
                argVector[i] += vectorTmp[i];
            }
        }
        for (int j = 0; j < argVector.length; j++) {
            argVector[j] = argVector[j] / clust.size();
        }
        return argVector;
    }

    public static void main(String[] args) throws IOException {
        new KKEntropyValueCal().cal();
    }
}
