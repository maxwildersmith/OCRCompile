package com.example.max.aicomplier;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.style.AbsoluteSizeSpan;
import android.util.AndroidException;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;

import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.text.BaseOCR;
import org.opencv.text.OCRTesseract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.CvType.CV_32FC2;
import static org.opencv.core.CvType.CV_32FC4;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.bilateralFilter;
import static org.opencv.imgproc.Imgproc.findContours;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener{

    private TessBaseAPI mTess;
    private String data;

    private Button next,cancel;
    CameraBridgeViewBase imga;
    private Mat currentMat;
    private boolean update = true;
    private ImageView point1,point2,point3,point4;
    private int button=0;
    private boolean moved = false;


    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("asdf", "OpenCV loaded successfully");
                    imga.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();
        Log.i("asdf", "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback)) {
            Log.e("asdf", "Cannot connect to OpenCV Manager");
        }

        point1 = (ImageView) findViewById(R.id.c1);
        point2 = (ImageView) findViewById(R.id.c2);
        point3 = (ImageView) findViewById(R.id.c3);
        point4 = (ImageView) findViewById(R.id.c4);

        currentMat = new Mat();
        next = (Button) findViewById(R.id.next);
        cancel = (Button) findViewById(R.id.cancel);
        imga = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        imga.setVisibility(SurfaceView.VISIBLE);
        imga.setCvCameraViewListener(this);
        imga.enableView();

        imga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (update) {
                    update = false;
                    imga.disableView();
                    cancel.setVisibility(View.VISIBLE);
                    point1.setVisibility(View.VISIBLE);
                    point2.setVisibility(View.VISIBLE);
                    point3.setVisibility(View.VISIBLE);
                    point4.setVisibility(View.VISIBLE);
                }
            }
        });

        point1.setOnTouchListener(this);
        point2.setOnTouchListener(this);
        point3.setOnTouchListener(this);
        point4.setOnTouchListener(this);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update = true;
                imga.enableView();
                cancel.setVisibility(View.INVISIBLE);
                point1.setVisibility(View.INVISIBLE);
                point2.setVisibility(View.INVISIBLE);
                point3.setVisibility(View.INVISIBLE);
                point4.setVisibility(View.INVISIBLE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               transform();
            }
        });
    }
        private Mat transform() {
            Mat warp = currentMat.clone();

            org.opencv.core.Point tl = new org.opencv.core.Point(point1.getX(),point1.getY());
            org.opencv.core.Point tr = new org.opencv.core.Point(point2.getX(),point2.getY());
            org.opencv.core.Point bl = new org.opencv.core.Point(point3.getX(),point3.getY());
            org.opencv.core.Point br = new org.opencv.core.Point(point4.getX(),point4.getY());
            org.opencv.core.Point tmp;

            if(tl.y>bl.y){
                tmp = tl;
                tl = bl;
                bl = tmp;
            }
            if(tr.y>br.y){
                tmp = tr;
                tr = br;
                br = tmp;
            }
            if(tl.x>tr.x){
                tmp = tr;
                tr = tl;
                tl = tmp;
            }
            if(bl.x>br.x){
                tmp = br;
                br = bl;
                bl = tmp;
            }

            if(moved) {


                Log.e("DEBUG", "Top left: " + tl.toString() + " Top Right: " + tr.toString() + " Bottom Left: " + bl.toString() + " Bottom Right: " + br.toString());

                int topWidth = (int) (tr.x - tl.x);
                int bottomWidth = (int) (br.x - bl.x);
                int resultWidth;
                if (topWidth > bottomWidth)
                    resultWidth = topWidth;
                else
                    resultWidth = bottomWidth;

                int topHeight = (int) (tr.x - tl.x);
                int bottomHeight = (int) (br.x - bl.x);
                int resultHeight;
                if (topHeight > bottomHeight)
                    resultHeight = topHeight;
                else
                    resultHeight = bottomHeight;

                float[] source = {(float) tl.x, (float) tl.y, (float) tr.x, (float) tr.y, (float) bl.x, (float) bl.y, (float) br.x, (float) br.y};
                float[] destination = {0, 0, resultWidth, 0, 0, resultHeight, resultWidth, resultHeight};

                Mat src = new Mat(4, 1, CV_32FC2);
                Mat dst = new Mat(4, 1, CV_32FC2);
                src.put(0, 0, source);
                dst.put(0, 0, destination);

                Mat homography = Imgproc.getPerspectiveTransform(src, dst);
                Log.e("asdf", homography.toString());
                Mat finalMat = warp.clone();
                Imgproc.warpPerspective(warp, finalMat, homography, new Size(resultWidth, resultHeight));

                next(finalMat);
                return finalMat;
            }
            else{
                next(warp);
                return warp;
            }
        }


    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = data + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = data+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    public void next(Mat mat){
        data = getFilesDir() + "/tesseract/";

        Bitmap img = Bitmap.createBitmap(mat.width(),mat.height(), Bitmap.Config.ARGB_8888 );
        Utils.matToBitmap(mat,img);

        Log.e("asdf",img.toString());

        checkFile(new File(data+"tessdata/"));

        mTess = new TessBaseAPI();
        mTess.init(data,"eng");
        mTess.setImage(img);
        Log.e("asdf2",mTess.getUTF8Text());
        startActivity(new Intent(MainActivity.this,Compile.class).putExtra("code",""+mTess.getUTF8Text()));
    }


    public static double d(Point a, Point b){
        return Math.sqrt(Math.pow(a.x-b.x,2)+Math.pow(a.y-b.y,2));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(imga != null)
            imga.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.e("asdf","asdf");
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        if (update){
            currentMat = inputFrame.gray();
            return inputFrame.rgba();
        }
        else
            return currentMat;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int  windowwidth = getWindowManager().getDefaultDisplay().getWidth();
        int windowheight = getWindowManager().getDefaultDisplay().getHeight();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    //transform();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moved  =true;
                    int x_cord = (int)event.getRawX();
                    int y_cord = (int)event.getRawY();

                    if(x_cord>windowwidth){x_cord=windowwidth;}
                    if(y_cord>windowheight){y_cord=windowheight;}


                    layoutParams.leftMargin = x_cord - 150  ;
                    layoutParams.topMargin = y_cord ;

                    v.setLayoutParams(layoutParams);
                    break;
                default:
                    break;
            }
            return true;



    }
}
