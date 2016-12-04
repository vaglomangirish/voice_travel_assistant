package mobilepervasive.soic.iu.edu.travelassistantandroid;

/**
 * Created by goshenoy on 12/3/16.
 */
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        new UpdateRefreshTokenTask().execute(token);
    }

    private class UpdateRefreshTokenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... token) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.UPDATE_REFRESH_TOKEN_URL, token[0]);
                Log.v(TAG, "Update Refresh Token Url: " + url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                Log.e(TAG, "update_refresh_token | Exception calling API: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d(TAG, "update_refresh_token | API Response: " + response);
        }
    }
}
