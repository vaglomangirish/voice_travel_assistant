package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by goshenoy on 12/2/16.
 */
public class APIUtil {

    private static final String TAG = "APIUtil";
    public static boolean apiComplete = false;
    public static JSONObject responseObject;

    public void getNearestBusStops(double latitude, double longitude) {
        new NearestBusStopTask().execute(latitude, longitude);
    }

    private class NearestBusStopTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... doubles) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.NEAREST_BUS_URL, doubles[0], doubles[1]);
                Log.v(TAG, "Nearest bus-stops API Url: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                Log.e(TAG, "Exception calling API: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "API Response: " + response);
            // speak out response
            MainActivity.getTtsp().speak(String.format(Constants.NEAREST_BUS_RESPONSE_TEXT, response), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
