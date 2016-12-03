package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by mangirish on 11/29/16.
 */

public class SmsListener extends BroadcastReceiver{

    private static final String TAG = "SmsListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                String sender = smsMessage.getDisplayOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();

                if (sender.startsWith("1410200")) {
                    sender = "UBER Taxi Service";
                } else {
                    Log.v(TAG, sender + ", does not match");
                }

                if(messageBody.split("\n").length == 3) {
                    messageBody = messageBody.split("\n")[2];
                    messageBody = messageBody.split(":")[1];
                }

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