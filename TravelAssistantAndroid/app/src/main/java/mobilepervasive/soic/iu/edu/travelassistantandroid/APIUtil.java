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

    public void getNextBusToDest(String destination, double latitude, double longitude) {
        new NextBusToDestTask().execute(destination, latitude, longitude);
    }

    public void getNextBusDetails(String destination, double latitude, double longitude) {
        new NextBusDetailsTask().execute(destination, latitude, longitude);
    }

    public void getTimeToDest(String destination, double latitude, double longitude) {
        new TimeToDestTask().execute(destination, latitude, longitude);
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
                Log.e(TAG, "nearest_bus_stop | Exception calling API: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "nearest_bus_stop | API Response: " + response);
            // speak out response
            MainActivity.getTtsp().speak(String.format(Constants.NEAREST_BUS_RESPONSE_TEXT, response), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class NextBusToDestTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... objects) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.NEXT_BUS_TO_DEST_URL, objects[0], objects[1], objects[2]);
                Log.v(TAG, "Next bus to destination API Url: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                Log.e(TAG, "next_bus_to_dest | Exception calling API: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "next_bus_to_dest | API Response: " + response);
            // speak out response
            MainActivity.getTtsp().speak(response, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class NextBusDetailsTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... objects) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.NEXT_BUS_DETAILS_URL, objects[0], objects[1], objects[2]);
                Log.v(TAG, "Next bus details API Url: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                Log.e(TAG, "next_bus_details | Exception calling API: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "next_bus_details | API Response: " + response);
            // speak out response
            MainActivity.getTtsp().speak(response, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private class TimeToDestTask extends AsyncTask<Object, Void, String> {
        @Override
        protected String doInBackground(Object... objects) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.TIME_TO_DEST_URL, objects[0], objects[1], objects[2]);
                Log.v(TAG, "Time to dest API Url: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                Log.e(TAG, "time_to_dest | Exception calling API: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "time_to_dest | API Response: " + response);
            // speak out response
            MainActivity.getTtsp().speak(response, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
