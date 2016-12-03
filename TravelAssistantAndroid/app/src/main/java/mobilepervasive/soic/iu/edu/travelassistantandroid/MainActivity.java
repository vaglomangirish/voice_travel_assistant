package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements RecognitionListener,
        SurfaceHolder.Callback {

    private static final String TAG = "TravelAssistantActivity";

    public static int DELAY_BETWEEN_CAPTURE =10000;
    public static int CAM_START_DELAY = 2000;
    public static String IMAGE_FILE_NAME = "capture.png";

    public static int CAPTURE_COUNT = 3;

    public static Boolean IS_NAVIGATION_ON = false;

    private TextView txtSpeechInput;
    private TextView captionTxt;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private BroadcastReceiver receiver;

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

    private static TextToSpeech ttsp;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String QUESTION_ASK = "question";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "hello assistant";

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 2;

    private SpeechRecognizer recognizer;

    /* Handler for adding delays */
    private final Handler handler = new Handler();

    /* GPS related variables */
    LocationManager lm;
    Location location;
    double longitude;
    double latitude;

    public static TextToSpeech getTtsp() {
        return ttsp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        //get the Image View at the main.xml file
        cap_image = (ImageView) findViewById(R.id.imgview);

        //get the Surface View at the main.xml file
        surfView = (SurfaceView) findViewById(R.id.surfview);

        //Get a surface
        surfhold = surfView.getHolder();

        //add the callback interface methods defined below as the Surface View callbacks
        surfhold.addCallback(this);

        //tells Android that this surface will have its data constantly replaced
        surfhold.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        captionTxt = (TextView) findViewById(R.id.captionText);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        // hide the action bar
        if(getActionBar() != null) {
            getActionBar().hide();
        }
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        ttsp=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttsp.setLanguage(Locale.US);
                }
            }
        });

        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        receiver = new BatteryLevelReceiver();
        registerReceiver(receiver, filter);

        // print startup message
        captionTxt.setText(R.string.startup_message);

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        runRecognizerSetup();

        // Check if user has given permission to access location
        permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
            return;
        }

        // initialize gps
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        longitude = location.getLongitude();
        latitude = location.getLatitude();
        Log.v(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private void runRecognizerSetup() {
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    txtSpeechInput.setText(
                            String.format(getString(R.string.init_fail), result));
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                runRecognizerSetup();
            } else {
                finish();
            }
        }
    }

    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();
        Log.v(TAG, "Text is: " + text);

        if (text.equals(KEYPHRASE)) {
            ttsp.speak("Hello, how may I help you?", TextToSpeech.QUEUE_FLUSH, null);
            switchSearch(QUESTION_ASK);
        } else if (text.contains(QUESTION_ASK)) {
            Log.v(TAG, "DOING NOTHING");
            ttsp.speak(getString(R.string.question_text), TextToSpeech.QUEUE_FLUSH, null);

            SystemClock.sleep(1500);
            switchSearch("asking-a-question");
        }
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        txtSpeechInput.setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

            Log.v(TAG, "ON RESULT | text: " + text);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // caption text
        String caption = "";

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            recognizer.startListening(searchName);
        }
        else if (searchName.equals(QUESTION_ASK)) {
            recognizer.startListening(searchName, 20000);
        }
        else {
            promptSpeechInput();
        }

        // print caption
        captionTxt.setText(R.string.kws_caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File questionGrammar = new File(assetsDir, "question_ask.gram");
        recognizer.addGrammarSearch(QUESTION_ASK, questionGrammar);
    }

    @Override
    public void onError(Exception error) {
        txtSpeechInput.setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something..");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry your device doesn't support text input.",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),
                    "Something went wrong!",
                    Toast.LENGTH_SHORT).show();
            switchSearch(KWS_SEARCH);
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String command = result.get(0);
                    txtSpeechInput.setText(command);

//                    ttsp.speak("I heard, " + command, TextToSpeech.QUEUE_FLUSH, null);

                    // process the voice command
                    processVoiceCommand(QUESTION_ASK, command);

                    // switch back to voice recognition
                    switchSearch(KWS_SEARCH);
                }
                else if (resultCode == RESULT_CANCELED) {
                    // the intent was cancelled, switch listener on
                    switchSearch(KWS_SEARCH);
                }
                break;
            }
        }
    }

    private void processVoiceCommand(String searchName, String commandText) {
        Log.v(TAG, "Processing voice command for category: " + searchName + ", command text: " + commandText);

        switch (searchName) {
            case QUESTION_ASK: {
                if(QuestionAnsweringUtil.belongsToCategory(commandText)) {
                    Log.v(TAG, "This question belongs to category: " + QuestionAnsweringUtil.getQuestionCategory());
                    QuestionAnsweringUtil.processQuestion(commandText, latitude, longitude);
                    if (QuestionAnsweringUtil.getDestinationName() != null) {
                        ttsp.speak("Okay. I figured out your destination as " + QuestionAnsweringUtil.getDestinationName(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                    break;
                } else {
//                    CAPTURE_COUNT = 3;
//                    runObjectDetection();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }


    ////////////////////// Camera Image Capture Section //////////////////////////////////////////

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
            Thread.sleep(CAM_START_DELAY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //sets what code should be executed after the picture is taken
        Camera.PictureCallback mCall = new Camera.PictureCallback()
        {
            @Override
            public void onPictureTaken(byte[] data, Camera camera)
            {
                Log.v(TAG, "On Picture taken invoked");
                FileOutputStream out = null;

                //decode the data obtained by the camera into a Bitmap
                bitmp = BitmapFactory   .decodeByteArray(data, 0, data.length);
                //set the cap_image
                cap_image.setImageBitmap(bitmp);


                try {
                    out = new FileOutputStream(IMAGE_FILE_NAME);
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

                Log.v(TAG, "Analysing image: " + IMAGE_FILE_NAME);
                //ttsp.speak("Approaching Crossroads", TextToSpeech.QUEUE_ADD, null);

                CAPTURE_COUNT --;
                runObjectDetection();
            }
        };

        capcam.takePicture(null, null, mCall);
    }

    /**
     * Function that runs in background while navigating, detecting objects on the way.
     */
    public void runObjectDetection() {

        // TODO: Should loop while IS_NAVIGATION_ON is true.
        //if(IS_NAVIGATION_ON) {
        if(CAPTURE_COUNT >= 0) {
            Log.v(TAG, "Capture Count is " + CAPTURE_COUNT);
            capturePicture();
        }
    }


    ////////////////////// Camera Image Capture Section ENDS //////////////////////////////////////
}
