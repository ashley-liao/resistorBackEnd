package com.lrz.resistor;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import com.lrz.resistor.ResIdentify;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CalculateRes {
    public List<Double> calculateAllRes(){
        List<Double> resResult = new ArrayList<Double>();
        ResIdentify resIdentify = new ResIdentify();
        for(int i=0;i<10;i++){
            String path = "res/img/resistor/debug_crop_" + i + ".jpg";
            File file = new File(path);
            if(file.exists()) {
                Mat src1 = Imgcodecs.imread(path);
                resIdentify.resIdentify(src1);
                resResult.add(calculateRes());
            }
        }
        return resResult;
    }

    public double calculateRes(){
        List<String> ringList = new ArrayList<String>();
        for(int i=0;i<10;i++){
            String path = "res/img/resistor/identify/debug_crop_" + i + ".jpg";
            File file = new File(path);
            if(file.exists()) {
                Mat src1 = Imgcodecs.imread(path);
                ResIdentify resIdentify = new ResIdentify();
                ringList.add(resIdentify.getColor(src1));
            }
        }
        System.out.println(ringList);
        //four-ring Res
        if(ringList.size()==4){
            return ((color2Num(ringList.get(0))*10+color2Num(ringList.get(1)))*color2Multiplier(ringList.get(2)));
        }else{
            return -1;
        }
    }
    public static int color2Num(String color){
        if(color == "black"){
            return 0;
        }else if(color == "brown"){
            return 1;
        }else if(color == "red1" || color == "red2"){
            return 2;
        }else if(color == "orange"){
            return 3;
        }else if(color == "yellow"){
            return 4;
        }else if(color == "green"){
            return 5;
        }else if(color == "blue"){
            return 6;
        }else if(color == "violet"){
            return 7;
        }else if(color == "gray"){
            return 8;
        }else if(color == "white"){
            return 9;
        }else{
            return -1;
        }
    }

    public static double color2Multiplier(String color){
        if(color == "black"){
            return 1;
        }else if(color == "brown"){
            return 10;
        }else if(color == "red1" || color == "red2"){
            return 100;
        }else if(color == "orange"){
            return 1000;
        }else if(color == "yellow"){
            return 10000;
        }else if(color == "green"){
            return 100000;
        }else if(color == "blue"){
            return 1000000;
        }else if(color == "violet"){
            return 10000000;
        }else if(color == "gray"){
            return 100000000;
        }else if(color == "white"){
            return 1000000000;
        }else{
            return -1;
        }
    }
}
