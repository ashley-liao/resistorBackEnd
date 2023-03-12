package com.lrz.resistor;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

//读取纯色图片，输出颜色HSV值的均值
public class ColorExtract {
    static {
        //加载opencv动态链接库
        String path = "F:\\cisc498\\opencv\\build\\java\\x64\\opencv_java320.dll";
        //String path = "F:\\opencv\\build\\java\\x64\\opencv_java455.dll";
        System.load(path);
    }

    public static void main(String[] args) {
        //读取颜色图片，尽量减少输入图片中的干扰像素
        Mat src = Imgcodecs.imread("res/img/resistor/identify/orange.jpg");
        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2HSV);
        List<Double> H = new ArrayList<Double>();
        List<Double> S = new ArrayList<Double>();
        List<Double> V = new ArrayList<Double>();
        //分别输出HSV的平均值
        for (int i = 0; i < dst.rows(); i++) {
            for (int j = 0; j < dst.cols(); j++) {
                H.add(dst.get(i,j)[0]);
            }
        }
        for (int i = 0; i < dst.rows(); i++) {
            for (int j = 0; j < dst.cols(); j++) {
                S.add(dst.get(i,j)[1]);
            }
        }
        for (int i = 0; i < dst.rows(); i++) {
            for (int j = 0; j < dst.cols(); j++) {
                V.add(dst.get(i,j)[2]);
            }
        }

        //去除离群值,输出均值
        H = deleteOutliers(H);
        System.out.println("H:"+getMean(H));
        S = deleteOutliers(S);
        System.out.println("S:"+getMean(S));
        V = deleteOutliers(V);
        System.out.println("V:"+getMean(V));




    }
    //求标准差
    public static double getStandardDiviation(List<Double> inputList) {
        int m = inputList.size();
        double sum = 0;
        for (int i = 0; i < m; i++) {// 求和
            sum += inputList.get(i);
        }
        double dAve = sum / m;// 求平均值
        double dVar = 0;
        for (int i = 0; i < m; i++) {// 求方差
            dVar += (inputList.get(i) - dAve) * (inputList.get(i) - dAve);
        }
        return Math.sqrt(dVar / m);
    }
    //求均值
    public static double getMean(List<Double> inputList) {
        int m = inputList.size();
        double sum = 0;
        for (int i = 0; i < m; i++) {// 求和
            sum += inputList.get(i);
        }
        return sum / m;
    }
    public static List<Double> deleteOutliers(List<Double> inputList) {
        //使用3sigma去除离群值
        double mean = getMean(inputList);
        double std = getStandardDiviation(inputList);
        List<Integer> removeIndex = new ArrayList<Integer>();
        for (int i = 0; i < inputList.size(); i++) {
            if ((inputList.get(i) > mean + 3 * std) || (inputList.get(i) < mean - 3 * std)) {
                removeIndex.add(i);
            }
        }
        for (int i = 0; i < removeIndex.size(); i++) {
            inputList.remove(removeIndex.get(i));
        }
        return inputList;
    }
}
