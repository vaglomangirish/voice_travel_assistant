package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;

public class VisualRecDemo extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = "VisualRecDemo";

    //a variable to store a reference to the Image View at the main.xml file
    private ImageView cap_image;
    //a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView surfView;

    //a bitmap to display the captured image
    private Bitmap bitmp;

    //Camera variables
    //a surface holder
    private SurfaceHolder surfhold;
    //a variable to control the camera
    private Camera capcam;
    //the camera parameters
    private Camera.Parameters parameters;

    private ImageRecognitionUtil imageRecUtil;

    private Button clickButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_rec_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cap_image = (ImageView)findViewById(R.id.imgviewd);
        surfView = (SurfaceView)findViewById(R.id.surfviewd);
        clickButton = (Button)findViewById(R.id.click_button);

        surfhold = surfView.getHolder();

        //add the callback interface methods defined below as the Surface View callbacks
        surfhold.addCallback(this);

        //tells Android that this surface will have its data constantly replaced
        surfhold.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        imageRecUtil = new ImageRecognitionUtil();

        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capturePicture();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.v(TAG, "SurfaceCreatedInvoked");
        // The Surface has been created, acquire the camera and tell it where
        // to draw the preview.
        capcam = Camera.open();
        try {
            capcam.setPreviewDisplay(holder);

        } catch (IOException exception) {
            capcam.release();
            capcam = null;
            exception.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(TAG, "surface changed invoked.");
        // Do Nothing
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        //stop the preview
        capcam.stopPreview();
        //release the camera
        capcam.release();
        //unbind the camera from this object
        capcam = null;
    }

    public void capturePicture() {

        Log.v(TAG, "ca " + capcam);

        //get camera parameters
        parameters = capcam.getParameters();

        //set camera parameters
        capcam.setParameters(parameters);
        capcam.startPreview();

        try {
            Thread.sleep(MainActivity.CAM_START_DELAY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //sets what code should be executed after the picture is taken
        Camera.PictureCallback mCall = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.v(TAG, "On Picture taken invoked");
                FileOutputStream out = null;

                //decode the data obtained by the camera into a Bitmap
                bitmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                //set the cap_image
                cap_image.setImageBitmap(bitmp);


                try {
                    out = new FileOutputStream("/sdcard/Download/" + MainActivity.IMAGE_FILE_NAME);
                    bitmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Save to file.
                Log.v(TAG, "Picture taken");

                Log.v(TAG, "Analysing image: " + MainActivity.IMAGE_FILE_NAME);
                imageRecUtil.classifyImage("/sdcard/Download/" + MainActivity.IMAGE_FILE_NAME);
            }
        };

        capcam.takePicture(null, null, mCall);
    }
}
