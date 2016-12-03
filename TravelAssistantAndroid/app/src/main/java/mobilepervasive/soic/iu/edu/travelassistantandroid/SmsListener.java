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

        String sender = null, messageBody = "";

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                sender = smsMessage.getDisplayOriginatingAddress();
                String localMessageBody = smsMessage.getMessageBody();

                if (sender.startsWith("1410200")) {
                    sender = "UBER Taxi Service";
                } else {
                    Log.v(TAG, sender + ", does not match");
                }

                Log.v(TAG, "localMessageBody: " + localMessageBody);
                Log.v(TAG, "len: " + localMessageBody.split("\n").length);

                if(localMessageBody.split("\n").length == 2) {
                    // if message is long, and broken into 2 sms's:
                    //      this will be second part, and remove "(End)"
                    //      from end of message body
                    messageBody += localMessageBody.split("\n")[1].replace("(End)", "");
                }
                else if(localMessageBody.split("\n").length == 3) {
                    // if message is normal length:
                    //      this will be the only part
                    messageBody = localMessageBody.split("\n")[2];
                    messageBody = messageBody.split(":")[1];
                } else if (localMessageBody.split("\n").length > 3) {
                    // if message is long, and broken into 2 sms's:
                    //      this will be first part
                    localMessageBody = localMessageBody.split("\n")[3];
                    messageBody += localMessageBody.split(":")[1] + " ";
                }

                Log.v(TAG, "MESSAGE-BODY: " + messageBody);
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