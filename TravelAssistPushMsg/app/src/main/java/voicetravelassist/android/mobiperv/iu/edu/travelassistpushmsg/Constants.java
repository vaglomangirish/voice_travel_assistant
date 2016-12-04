package voicetravelassist.android.mobiperv.iu.edu.travelassistpushmsg;

/**
 * Created by goshenoy on 12/4/16.
 */
public class Constants {

    public static final String SERVER_HOSTNAME = "52.53.190.177";

    public static final String SERVER_HOST_PORT = "5000";

    public static final String SEND_PUSH_URL = "http://" + SERVER_HOSTNAME + ":" + SERVER_HOST_PORT + "/send_push_notification/%s";

    // push messages to send
    public static final String WALK_STRAIGHT_MSG = "Walk straight.";
    public static final String WALK_BACK_MSG = "Turn backwards.";
    public static final String TURN_LEFT_MSG = "Turn left.";
    public static final String TURN_RIGHT_MSG = "Turn right.";
    public static final String STOP_MSG = "Stop! Obstacle ahead.";
}
