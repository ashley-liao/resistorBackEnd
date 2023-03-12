package com.lrz.resistor;

import java.util.HashMap;

public class GetColorList {
    public static HashMap<String, int[]> getColorList() {
        HashMap<String, int[]> colorList = new HashMap<String, int[]>();
        //black
        colorList.put("lower_black", new int[]{0,0,0});
        colorList.put("upper_black", new int[]{180, 255, 46});
        //brown
        colorList.put("lower_brown", new int[]{10,120,46});
        colorList.put("upper_brown", new int[]{30, 255, 150});
        //red1
        colorList.put("lower_red1", new int[]{0,80,46});
        colorList.put("upper_red1", new int[]{10, 255, 255});
        //red2
        colorList.put("lower_red2", new int[]{156,80,46});
        colorList.put("upper_red2", new int[]{180, 255, 255});
        //orange
        colorList.put("lower_orange", new int[]{11,110,46});
        colorList.put("upper_orange", new int[]{25,255,255});
        //yellow
        colorList.put("lower_yellow", new int[]{26,80,46});
        colorList.put("upper_yellow", new int[]{34, 255, 255});
        //green
        colorList.put("lower_green", new int[]{35,80,46});
        colorList.put("upper_green", new int[]{77, 255, 255});
        //blue
        colorList.put("lower_blue", new int[]{100,80,46});
        colorList.put("upper_blue", new int[]{124, 255, 255});
        //violet
        colorList.put("lower_violet", new int[]{125,80,66});
        colorList.put("upper_violet", new int[]{155, 255, 255});
        //gray
        colorList.put("lower_gray", new int[]{0,0,46});
        colorList.put("upper_gray", new int[]{180, 43, 220});
        //white
        colorList.put("lower_white", new int[]{0,0,221});
        colorList.put("upper_white", new int[]{180, 30, 255});
        return colorList;
    }
}

