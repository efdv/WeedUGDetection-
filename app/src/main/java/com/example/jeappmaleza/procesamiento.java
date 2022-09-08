package com.example.jeappmaleza;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import java.util.ArrayList;
import java.util.List;
import static org.opencv.core.Core.add;
import static org.opencv.core.Core.bitwise_and;
import static org.opencv.core.Core.bitwise_not;
import static org.opencv.core.Core.bitwise_or;
import static org.opencv.core.Core.inRange;
import static org.opencv.core.Core.mean;
import static org.opencv.core.Core.minMaxLoc;
import static org.opencv.core.Core.multiply;
import static org.opencv.core.Core.subtract;
import static org.opencv.core.CvType.CV_32FC3;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2Lab;
import static org.opencv.imgproc.Imgproc.MORPH_ELLIPSE;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.getStructuringElement;

public class procesamiento {

    public void procesamiento(Mat img,Mat imgR)
    {
        Mat lab = new Mat();
        Mat mask = new Mat();
        Mat mask1 = new Mat();
        Mat mask2 = new Mat();
        Mat mask3 = new Mat();
        Mat mask4 = new Mat();
        Mat mask5 = new Mat();
        Mat mask6 = new Mat();
        Mat mask12 = new Mat();
        Mat mask34 = new Mat();
        Mat mask56 = new Mat();
        Mat mask1234 = new Mat();

        Mat imgO = img.clone();

        img.convertTo(img,CV_32FC3);
        img.convertTo(img,CV_32FC3,1.0/255,0);

        cvtColor(img,lab,COLOR_RGB2Lab);

        inRange(lab, new Scalar(10, -28, 5), new Scalar(20, -5, 27), mask1);
        inRange(lab, new Scalar(21, -35, 7), new Scalar(40, -2, 45), mask2);
        add(mask1, mask2, mask12);
        inRange(lab, new Scalar(41, -40, 10), new Scalar(50, -5, 50), mask3);
        inRange(lab, new Scalar(51, -42, 0), new Scalar(62, -3, 62), mask4);
        add(mask3, mask4, mask34);
        inRange(lab, new Scalar(63, -45, 10), new Scalar(80, -2, 65), mask5);
        inRange(lab, new Scalar(81, -30, 20), new Scalar(90, -10, 70), mask6);
        add(mask5, mask6, mask56);
        add(mask12, mask34, mask1234);
        add(mask1234, mask56, mask);


        Mat kernelDilate = getStructuringElement(MORPH_ELLIPSE, new Size(6,6));
        Mat kernelErode = getStructuringElement(MORPH_ELLIPSE, new Size(6,6));

        dilate(mask, mask, kernelDilate,new Point(-1,-1),1);
        erode(mask,mask,kernelErode,new Point(-1,-1),1);

        erode(mask,mask,kernelErode,new Point(-1,-1),1);
        dilate(mask,mask,kernelDilate,new Point(-1,-1),1);

        Mat maskInv = new Mat();
        Mat back = new Mat();
        Mat front = new Mat();
        Mat green = new Mat(mask.rows(),mask.cols(),CV_8UC3,new Scalar(0,255,0));
        bitwise_not(mask,maskInv);

        //contiene fondo
        bitwise_and(imgO,imgO,back,maskInv);
        histogramStretching(back,100,255,back);


        //contiene frente
        bitwise_and(green,green,front,mask);


        List<Mat> rgbBack = new ArrayList<>();
        Core.split(back, rgbBack);

        List<Mat> rgbFront = new ArrayList<>();
        Core.split(front, rgbFront);

        List<Mat> rgbR = new ArrayList<>();
        Core.split(imgO, rgbR);

        bitwise_or(rgbBack.get(0),rgbFront.get(0),rgbR.get(0));
        bitwise_or(rgbBack.get(1),rgbFront.get(1),rgbR.get(1));
        bitwise_or(rgbBack.get(2),rgbFront.get(2),rgbR.get(2));



        Core.merge(rgbR,imgO);

        imgO.convertTo(imgR,CV_8UC3);
        imgO.convertTo(mask,CV_8UC3);

    }

    public void histogramStretching(Mat In,double minO, double maxO,Mat Out)
    {
        List<Mat> rgb = new ArrayList<>();
        Core.split(In, rgb);

        double maxir,maxig,maxib,minir,minig,minib;
        maxir = minMaxLoc(rgb.get(0)).maxVal;
        maxig = minMaxLoc(rgb.get(1)).maxVal;
        maxib = minMaxLoc(rgb.get(2)).maxVal;
        minir = minMaxLoc(rgb.get(0)).minVal;
        minig = minMaxLoc(rgb.get(1)).minVal;
        minib = minMaxLoc(rgb.get(2)).minVal;

        double rangor = (maxO - minO)/(maxir-minir);
        double rangog = (maxO - minO)/(maxig-minig);
        double rangob = (maxO - minO)/(maxib-minib);

        subtract(rgb.get(0),new Scalar(minir),rgb.get(0));
        subtract(rgb.get(1),new Scalar(minig),rgb.get(1));
        subtract(rgb.get(2),new Scalar(minib),rgb.get(2));

        multiply(rgb.get(0),new Scalar(rangor),rgb.get(0));
        multiply(rgb.get(1),new Scalar(rangog),rgb.get(1));
        multiply(rgb.get(2),new Scalar(rangob),rgb.get(2));

        add(rgb.get(0),new Scalar(minO),rgb.get(0));
        add(rgb.get(1),new Scalar(minO),rgb.get(1));
        add(rgb.get(2),new Scalar(minO),rgb.get(2));

        Core.merge(rgb,Out);
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
