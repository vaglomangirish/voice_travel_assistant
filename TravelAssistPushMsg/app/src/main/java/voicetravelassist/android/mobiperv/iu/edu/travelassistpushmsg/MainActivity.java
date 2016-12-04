package voicetravelassist.android.mobiperv.iu.edu.travelassistpushmsg;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TravelAssistPushMsg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendPushMsg(View view) {
        int id = view.getId();
        Button button = (Button) findViewById(id);
        String str = button.getText().toString();

        if (str.equals(getString(R.string.walk_straight))) {
            new SendPushNotificationTask().execute(Constants.WALK_STRAIGHT_MSG);
        } else if (str.equals(getString(R.string.walk_back))) {
            new SendPushNotificationTask().execute(Constants.WALK_BACK_MSG);
        } else if (str.equals(getString(R.string.turn_left))) {
            new SendPushNotificationTask().execute(Constants.TURN_LEFT_MSG);
        } else if (str.equals(getString(R.string.turn_right))) {
            new SendPushNotificationTask().execute(Constants.TURN_RIGHT_MSG);
        } else if (str.equals(getString(R.string.stop))) {
            new SendPushNotificationTask().execute(Constants.STOP_MSG);
        }
    }

    private class SendPushNotificationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... message) {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = String.format(Constants.SEND_PUSH_URL, message[0]);
                Log.v(TAG, "Send Push Url: " + url);

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
        }
    }
}
