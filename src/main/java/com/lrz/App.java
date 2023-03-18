package com.lrz;

import com.lrz.missparts.MissParts;
import com.lrz.resistor.CalculateRes;
import com.lrz.resistor.ResIdentify;
import com.lrz.resistor.ResLocate;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;
import org.opencv.imgcodecs.Imgcodecs;
import com.lrz.resistor.ImageEdit;

import java.util.ArrayList;
import java.util.List;


public class App {
    static {
        //加载opencv动态链接库
        String path = "F:\\cisc498\\opencv\\build\\java\\x64\\opencv_java320.dll";
        //String path = "F:\\opencv\\build\\java\\x64\\opencv_java455.dll";
        System.load(path);
    }

    public static void main(String[] args) {

        Mat image = Imgcodecs.imread("res\\img\\src.jpg");
        //定位电阻
        List<RotatedRect> rectLoc = new ArrayList<>();
        ResLocate resLocate = new ResLocate();
        resLocate.resLocate(image,rectLoc);
        System.out.println(rectLoc);

        //定位色环
//        Mat resistor = Imgcodecs.imread("res\\img\\resistor/debug_crop_0.jpg");
//        ResIdentify resIdentify = new ResIdentify();
//        resIdentify.resIdentify(resistor);

        //识别色环颜色
//        Mat src1 = Imgcodecs.imread("res/img/resistor/identify/debug_crop_0.jpg");
//        resIdentify.getColor(src1);

        //System.out.println(CalculateRes.calculateRes());

        //计算定位电阻阻值
        CalculateRes calculateRes = new CalculateRes();
        List<Double> resistance = new ArrayList<>();
        resistance = calculateRes.calculateAllRes();

        //编辑图片
        ImageEdit imageEdit = new ImageEdit();
        Mat origin = Imgcodecs.imread("res\\img\\resistor\\originWithContour.jpg");
        imageEdit.addRes(origin,rectLoc,resistance);

//        SVMTrain svm = new SVMTrain();
//        svm.svmTrain(true, false);

//        MissParts missParts = new MissParts();
//        missParts.subtract();
    }

}
