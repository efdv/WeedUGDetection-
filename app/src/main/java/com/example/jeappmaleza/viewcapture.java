package com.example.jeappmaleza;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import static org.opencv.android.Utils.bitmapToMat;
import static org.opencv.android.Utils.matToBitmap;
import static org.opencv.core.Core.mean;

public class viewcapture extends AppCompatActivity {

    ImageView viewCap;
    procesamiento p = new procesamiento();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewcapture);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            if(status == LoaderCallbackInterface.SUCCESS){
                //codigo opencv

                Mat imgIn = new  Mat();
                Mat imgOut = new  Mat();
                Bundle b = getIntent().getExtras();
                String str = b.getString("image");
                Bitmap bitmap = BitmapFactory.decodeFile(str);

                viewCap = (ImageView)findViewById(R.id.viewCap);

                if(bitmap.getWidth() > bitmap.getHeight()) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);
                }
                else
                {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 480, 640, true);
                }

                bitmapToMat(bitmap,imgIn);
                p.procesamiento(imgIn, imgOut);
                matToBitmap(imgOut,bitmap);
                viewCap.setImageBitmap(bitmap);
            }
            else
            {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
    }

}
