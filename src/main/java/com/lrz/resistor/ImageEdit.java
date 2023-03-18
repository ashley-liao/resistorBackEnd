package com.lrz.resistor;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class ImageEdit {
    private static final String PATH = "res/img/resistor/";
    public void addRes(Mat src, List<RotatedRect> rectLoc, List<Double> resistance){
        int font = Core.FONT_HERSHEY_COMPLEX;
        double fontScale = 1.0;
        Scalar color = new Scalar(255, 0, 255);
        int thickness = 3;
        for(int i=0;i<rectLoc.size();i++){
            Point[] rect_points = new Point[4];
            rectLoc.get(i).points(rect_points);
            //Point pt = new Point(rect_points[1].x,rect_points[1].y+30);
            Point pt = new Point(rectLoc.get(i).center.x-50,rectLoc.get(i).center.y);
            //Imgproc.putText(src,String.valueOf(resistance.get(i)),pt,font, fontScale, color, thickness);
            Imgproc.putText(src,String.valueOf(resistance.get(i)),pt,font, fontScale, color, thickness);
        }
        Imgcodecs.imwrite(PATH + "edited.jpg", src);
    }
}
