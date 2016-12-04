package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mangirish on 12/3/16.
 */

public class ImageRecognitionUtil {

    private static final String TAG = "ImageRecognitionUtil";

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    public static final MediaType JSON_PARAM = MediaType.parse("application/json; charset=utf-8");

    public void classifyImage(String filePath) {
        new ClassifyImage().execute(filePath);
    }

    private class ClassifyImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.IMAGE_VR_SERVICE_URL);
                Log.v(TAG, "Image recognition URL: " + url);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("images_file", MainActivity.IMAGE_FILE_NAME,
                                RequestBody.create(MEDIA_TYPE_PNG,
                                        new File(params[0])))

                        .addFormDataPart("parameters", "myparams.json",
                                RequestBody.create(JSON_PARAM,
                                        "{\"classifier_ids\": [\"" + Constants.IMAGE_CLASSIFIER_ID +
                                                "\", \"default\"]}"))
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    //TODO Speak out the identified objects.
                    String responseStr = response.body().string();

                    Log.v(TAG, responseStr);

                    speakWhatSeen(responseStr);
                }


            } catch (Exception ex) {
                Log.e(TAG, "Watson Visual Recognition failed: " + ex);
                ex.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        protected void speakWhatSeen(String responseBody){

            Set<String> objList = new HashSet<String>();
            Boolean isZebraCrossingDetected = false;

            try {
                JSONObject response = new JSONObject(responseBody);
                JSONArray imagesArr = (JSONArray) response.get("images");
                JSONArray classifierArr = (JSONArray) new JSONObject(imagesArr.get(0).toString())
                        .get("classifiers");

                // handle empty response.
                if (classifierArr.length() == 0 ) {
                    VisualRecDemo.getTtsp().speak(Constants.CANNOT_UNDERSTAND_WHAT_I_SEE,
                            TextToSpeech.QUEUE_ADD, null);

                    return;
                }

                JSONObject classifier;
                JSONArray itemList;
                String item;

                for( int x=0; x < classifierArr.length(); x++ ) {
                    classifier = classifierArr.getJSONObject(x);
                    itemList = classifier.getJSONArray("classes");

                    for ( int y = 0; y < itemList.length(); y++ ) {
                        item = itemList.getJSONObject(y).getString("class");

                        if(item.equals("zcross")){
                            isZebraCrossingDetected = true;
                        } else {
                            objList.add(item);
                        }
                    }
                }

                // check if crossroads
                if(isZebraCrossingDetected) {
                    MainActivity.getTtsp().speak(Constants.ZEBRA_CROSSING_ALERT,
                            TextToSpeech.QUEUE_ADD, null);
                }

                // speak out other items.
                if (objList.size() > 0) {
                    String to_speak = Constants.PRE_OTHER_REC_ITEMS;

                    for (String classItem : objList) {
                        to_speak += " " + classItem + ",";
                    }

                    VisualRecDemo.getTtsp().speak(to_speak, TextToSpeech.QUEUE_ADD, null);
                } else {
                    VisualRecDemo.getTtsp().speak(Constants.CANNOT_UNDERSTAND_WHAT_I_SEE,
                            TextToSpeech.QUEUE_ADD, null);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
    }
}
