package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mangirish on 11/30/16.
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    int scale = -1;
    int level = -1;
    int voltage = -1;
    int temp = -1;
    float batterycnt = (float)0.0;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("BatteryLevelReceiver", "BatteryLevelReceiver inoked on action "
                + intent.getAction().toString());
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        //scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        //temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        //voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

        if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            MainActivity.getTtsp().speak("Your device battery level is: " + level + " percent",
                    TextToSpeech.QUEUE_ADD, null);
        }

        if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
            MainActivity.getTtsp().speak("Your device is running out of battery."
                    + "Please find alternative help.", TextToSpeech.QUEUE_ADD, null);
        }
    }
}
