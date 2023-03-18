package com.lrz.resistor;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author HustLrz
 * @Date Created in 8:24 2017/11/8
 */
public class ResIdentify {

    private static final String PATH = "res/img/resistor/identify/";
    private static int threshold = 170;   //二值化阈值，根据具体情况调整
    private static int morphOpenSizeX = 20;     //开操作size
    private static int morphOpenSizeY = 20;     //开操作size
    private static int erodeSizeX0 = 3;
    private static int erodeSizeY0 = 8;   //纵向腐蚀
    private static int erodeSizeX = 5;
    private static int erodeSizeY = 30;   //纵向腐蚀

    //色环BGR值，待测
    private static Scalar[] colorCode = {
    };


    public int resIdentify(Mat src) {

        //去掉边缘，取中间
        src = src.submat(src.rows() / 4, 3 * src.rows() / 4, src.cols() / 6, 5 * src.cols() / 6);

        // 高斯模糊
        Mat src_blur = new Mat();
        Imgproc.GaussianBlur(src, src_blur, new Size(5, 5), 0, 0, 4);

        // 灰度化
        Mat src_gray = new Mat();
        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

        Imgcodecs.imwrite(PATH + "gray.jpg", src_gray);

        // 二值化
        Mat img_threshold = new Mat();
        Imgproc.threshold(src_gray, img_threshold, threshold, 255, Imgproc.THRESH_OTSU);

        Imgcodecs.imwrite(PATH + "threshold.jpg", img_threshold);

        //尝试消除阴影导致的误判
        Core.bitwise_not(img_threshold, img_threshold);
        Mat element0 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeSizeX0, erodeSizeY0));
        Imgproc.erode(img_threshold, img_threshold, element0, new Point(-1, -1), 1);
        Core.bitwise_not(img_threshold, img_threshold);

        Imgcodecs.imwrite(PATH + "threshold0.jpg", img_threshold);

        // 纵向腐蚀，连接色环反光断点,腐蚀两次
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(erodeSizeX, erodeSizeY));
        Imgproc.erode(img_threshold, img_threshold, element, new Point(-1, -1), 2);

        Imgcodecs.imwrite(PATH + "MORPH_RECT.jpg", img_threshold);

        //反色
        Core.bitwise_not(img_threshold, img_threshold);

        Imgcodecs.imwrite(PATH + "bitwise.jpg", img_threshold);

        // 求所有轮廓
        Mat hierarchy = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(img_threshold, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);


        // 画出轮廓
        Imgproc.drawContours(img_threshold, contours, -1, new Scalar(255, 0, 255, 255));

        Imgcodecs.imwrite(PATH + "bitwise.jpg", img_threshold);

        // 画出轮廓的最小外接矩形
        List<RotatedRect> rects = new ArrayList<RotatedRect>();
        for (int i = 0; i < contours.size(); i++) {
            System.out.println(Imgproc.contourArea(contours.get(i)));
            MatOfPoint2f mtx = new MatOfPoint2f(contours.get(i).toArray());
            RotatedRect mr = Imgproc.minAreaRect(mtx);
            rects.add(mr);
        }

        for (int i = 0; i < contours.size(); i++) {
            RotatedRect minRect = rects.get(i);
            Point[] rect_points = new Point[4];
            minRect.points(rect_points);

            // 描边
            for (int j = 0; j < 4; j++) {
                Point pt1 = new Point(rect_points[j].x, rect_points[j].y);
                Point pt2 = new Point(rect_points[(j + 1) % 4].x, rect_points[(j + 1) % 4].y);

                Imgproc.line(img_threshold, pt1, pt2, new Scalar(255, 0, 255, 255), 4, 8, 0);
            }

            double r = minRect.size.width / minRect.size.height;
            double angle = minRect.angle;
            Size size = new Size(minRect.size.width, minRect.size.height);
            if (r < 1) {
                angle = angle + 90;
                size = new Size(minRect.size.height, minRect.size.width);
            }
            Mat rotMat = Imgproc.getRotationMatrix2D(minRect.center, angle, 1);
            Mat img_rotated = new Mat();
            Imgproc.warpAffine(src, img_rotated, rotMat, src.size());

            //色环图像
            Mat colorBandMat = showResultMat(img_rotated, size, minRect.center, i);
        }



        return 0;
    }

    private Mat showResultMat(Mat src, Size rect_size, Point center, int index) {
        Mat img_crop = new Mat();
        Imgproc.getRectSubPix(src, rect_size, center, img_crop);
        Imgcodecs.imwrite(PATH + "debug_crop_" + index + ".jpg", img_crop);
        return img_crop;
    }

    /**
     * 转换到HSV空间分析色环颜色
     * @param src
     */

    public String getColor(Mat src) {

        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2HSV);
        //System.out.println(dst.get(0, 0)[3]);
//        for (int i = 0; i < dst.rows(); i++) {
//            for (int j = 0; j < dst.cols(); j++) {
//                double[] hsv_value = dst.get(i, j);
//                //统计
//                //System.out.println("hsv:"+hsv_value[1]);
//            }
//
//        }
        HashMap<String, int[]> colorListStd = GetColorList.getColorList();
        Map colorListResult = new HashMap<String, Integer>();
        colorListResult.put("black",0);
        colorListResult.put("brown",0);
        colorListResult.put("red1",0);
        colorListResult.put("red2",0);
        colorListResult.put("orange",0);
        colorListResult.put("yellow",0);
        colorListResult.put("green",0);
        colorListResult.put("blue",0);
        colorListResult.put("violet",0);
        colorListResult.put("gray",0);
        colorListResult.put("white",0);
        //System.out.println(colorListStd.get("upper_black")[0]);
        for (int i = 0; i < dst.rows(); i++) {
            for (int j = 0; j < dst.cols(); j++) {
                if (dst.get(i,j)[0]>colorListStd.get("lower_black")[0] && dst.get(i,j)[0]<colorListStd.get("upper_black")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_black")[1] && dst.get(i,j)[1]<colorListStd.get("upper_black")[1] &&
                dst.get(i,j)[2]>colorListStd.get("lower_black")[2] && dst.get(i,j)[2]<colorListStd.get("upper_black")[2]){
                    int counter = (Integer) colorListResult.get("black");
                    colorListResult.put("black",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_brown")[0] && dst.get(i,j)[0]<colorListStd.get("upper_brown")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_brown")[1] && dst.get(i,j)[1]<colorListStd.get("upper_brown")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_brown")[2] && dst.get(i,j)[2]<colorListStd.get("upper_brown")[2]){
                    int counter = (Integer) colorListResult.get("brown");
                    colorListResult.put("brown",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_red1")[0] && dst.get(i,j)[0]<colorListStd.get("upper_red1")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_red1")[1] && dst.get(i,j)[1]<colorListStd.get("upper_red1")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_red1")[2] && dst.get(i,j)[2]<colorListStd.get("upper_red1")[2]){
                    int counter = (Integer) colorListResult.get("red1");
                    colorListResult.put("red1",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_red2")[0] && dst.get(i,j)[0]<colorListStd.get("upper_red2")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_red2")[1] && dst.get(i,j)[1]<colorListStd.get("upper_red2")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_red2")[2] && dst.get(i,j)[2]<colorListStd.get("upper_red2")[2]){
                    int counter = (Integer) colorListResult.get("red2");
                    colorListResult.put("red2",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_orange")[0] && dst.get(i,j)[0]<colorListStd.get("upper_orange")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_orange")[1] && dst.get(i,j)[1]<colorListStd.get("upper_orange")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_orange")[2] && dst.get(i,j)[2]<colorListStd.get("upper_orange")[2]){
                    int counter = (Integer) colorListResult.get("orange");
                    colorListResult.put("orange",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_yellow")[0] && dst.get(i,j)[0]<colorListStd.get("upper_yellow")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_yellow")[1] && dst.get(i,j)[1]<colorListStd.get("upper_yellow")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_yellow")[2] && dst.get(i,j)[2]<colorListStd.get("upper_yellow")[2]){
                    int counter = (Integer) colorListResult.get("yellow");
                    colorListResult.put("yellow",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_green")[0] && dst.get(i,j)[0]<colorListStd.get("upper_green")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_green")[1] && dst.get(i,j)[1]<colorListStd.get("upper_green")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_green")[2] && dst.get(i,j)[2]<colorListStd.get("upper_green")[2]){
                    int counter = (Integer) colorListResult.get("green");
                    colorListResult.put("green",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_blue")[0] && dst.get(i,j)[0]<colorListStd.get("upper_blue")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_blue")[1] && dst.get(i,j)[1]<colorListStd.get("upper_blue")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_blue")[2] && dst.get(i,j)[2]<colorListStd.get("upper_blue")[2]){
                    int counter = (Integer) colorListResult.get("blue");
                    colorListResult.put("blue",counter+=1);
                }else if (dst.get(i,j)[0]>colorListStd.get("lower_violet")[0] && dst.get(i,j)[0]<colorListStd.get("upper_violet")[0]
                        && dst.get(i,j)[1]>colorListStd.get("lower_violet")[1] && dst.get(i,j)[1]<colorListStd.get("upper_violet")[1] &&
                        dst.get(i,j)[2]>colorListStd.get("lower_violet")[2] && dst.get(i,j)[2]<colorListStd.get("upper_violet")[2]){
                    int counter = (Integer) colorListResult.get("violet");
                    colorListResult.put("violet",counter+=1);
                    //gray and white still nee
//                }else if (dst.get(i,j)[0]>colorListStd.get("lower_gray")[0] && dst.get(i,j)[0]<colorListStd.get("upper_gray")[0]
//                        && dst.get(i,j)[1]>colorListStd.get("lower_gray")[1] && dst.get(i,j)[1]<colorListStd.get("upper_gray")[1] &&
//                        dst.get(i,j)[2]>colorListStd.get("lower_gray")[2] && dst.get(i,j)[2]<colorListStd.get("upper_gray")[2]){
//                    int counter = (Integer) colorListResult.get("gray");
//                    colorListResult.put("gray",counter+=1);
//                }else if (dst.get(i,j)[0]>colorListStd.get("lower_white")[0] && dst.get(i,j)[0]<colorListStd.get("upper_white")[0]
//                        && dst.get(i,j)[1]>colorListStd.get("lower_white")[1] && dst.get(i,j)[1]<colorListStd.get("upper_white")[1] &&
//                        dst.get(i,j)[2]>colorListStd.get("lower_white")[2] && dst.get(i,j)[2]<colorListStd.get("upper_white")[2]){
//                    int counter = (Integer) colorListResult.get("white");
//                    colorListResult.put("white",counter+=1);
                }

            }
        }

        //System.out.println(dst.size());
        //System.out.println(colorListResult);
        AtomicReference<String> mostColor = new AtomicReference<>("");
        AtomicInteger mostColorNum = new AtomicInteger();
        colorListResult.forEach((key,value)->{
            if(mostColorNum.get() <(Integer) value){
                mostColor.set((String) key);
                mostColorNum.set((Integer) value);
            }
        });
        //System.out.println(mostColor.get());
        return mostColor.get();
    }
}
