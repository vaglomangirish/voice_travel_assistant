package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by mangirish on 11/29/16.
 */

public class SmsListener extends BroadcastReceiver{

    private SharedPreferences preferences;
    TextToSpeech t1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String sender = smsMessage.getDisplayOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();

                Toast toast = Toast.makeText(context, messageBody, Toast.LENGTH_LONG);
                toast.show();

                MainActivity.getTtsp().speak("You received a message text from, " + sender,
                        TextToSpeech.QUEUE_ADD, null);

                MainActivity.getTtsp().speak("Message is," + messageBody, TextToSpeech.QUEUE_ADD,
                        null);
            }
        }
    }
}