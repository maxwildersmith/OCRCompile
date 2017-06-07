package com.example.max.aicomplier;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageAdjust extends AppCompatActivity {

    private TessBaseAPI mTess;
    private String data;

    private ImageView display;
    private TouchView touch;
    private Button next, back;
    private Mat mat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_adjust);
        display = (ImageView)findViewById(R.id.display);
        touch = (TouchView)findViewById(R.id.touch);
        next = (Button)findViewById(R.id.next);
        back = (Button)findViewById(R.id.back);
        mat = new Mat();
//        Utils.bitmapToMat((Bitmap)getIntent().getParcelableExtra("bmp"),mat);
        try {
            FileInputStream fileInputStream = new FileInputStream("bmp.bmp");
            //Log.e("asdf",fileInputStream.);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        Bitmap bmp =
//        display.setImageBitmap(bmp);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
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

    public void next(){
        data = getFilesDir() + "/tesseract/";

//        Bitmap img =

        checkFile(new File(data+"tessdata/"));

        mTess = new TessBaseAPI();
        mTess.init(data,"eng");
//        mTess.setImage(img);
        Log.e("asdf2",mTess.getUTF8Text());
        startActivity(new Intent(ImageAdjust.this,Compile.class).putExtra("code",mTess.getUTF8Text()));
    }
}
