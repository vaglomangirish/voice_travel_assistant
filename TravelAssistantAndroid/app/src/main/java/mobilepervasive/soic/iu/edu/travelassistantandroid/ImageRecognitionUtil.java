package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                /*Request request = new Request.Builder()
                        .url(url)
                        .build();

                //Response response = client.newCall(request).execute();*/

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

                    System.out.println(response.body().string());
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
    }
}
