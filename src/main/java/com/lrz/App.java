package com.lrz;

import com.lrz.capacity.PolarDetect;
import com.lrz.missparts.MissParts;
import com.lrz.resistor.ResLocate;
import com.lrz.resistor.ResIdentify;
import com.lrz.train.SVMTrain;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


public class App {
    static {
        //加载opencv动态链接库
        String path = "F:\\cisc498\\opencv\\build\\java\\x64\\opencv_java320.dll";
        //String path = "F:\\opencv\\build\\java\\x64\\opencv_java455.dll";
        System.load(path);
    }

    public static void main(String[] args) {
        //Mat src = Imgcodecs.imread("res/img/capacity/debug_crop_21.jpg"); //读取原始电路板图片
        //Mat src = Imgcodecs.imread("res/img/capacity/mcd32.jpg");
        //Mat src = Imgcodecs.imread("res/img/capacity/resistor3.jpg");

        //定位电阻
//        Mat src = Imgcodecs.imread("res\\img\\capacity/resistor0.jpg");
//
//        ResLocate resLocate = new ResLocate();
//        resLocate.resLocate(src);

        //定位色环
//        Mat src = Imgcodecs.imread("res\\img\\resistor/debug_crop_0.jpg");
        ResIdentify resIdentify = new ResIdentify();
//        resIdentify.resIdentify(src);

        //识别色环颜色
        Mat src1 = Imgcodecs.imread("res/img/resistor/identify/debug_crop_2.jpg");
        resIdentify.getColor(src1);

//        SVMTrain svm = new SVMTrain();
//        svm.svmTrain(true, false);
//
//        CapLocate capLocate = new CapLocate();
//        capLocate.capLocate(src);
//        PolarDetect polarDetect = new PolarDetect();
//        polarDetect.detect(src);

        MissParts missParts = new MissParts();
        missParts.subtract();
    }

}
