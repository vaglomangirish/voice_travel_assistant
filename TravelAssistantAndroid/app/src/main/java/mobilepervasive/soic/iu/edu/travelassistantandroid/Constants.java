package mobilepervasive.soic.iu.edu.travelassistantandroid;

/**
 * Created by goshenoy on 12/2/16.
 */
public class Constants {

    public static final String SERVER_HOSTNAME = "52.53.190.177";

    public static final String SERVER_HOST_PORT = "5000";

    public static final String NEAREST_BUS_URL = "http://" + SERVER_HOSTNAME + ":" + SERVER_HOST_PORT + "/nearest_bustop?lat=%s&long=%s";

    public static final String NEXT_BUS_TO_DEST_URL = "http://" + SERVER_HOSTNAME + ":" + SERVER_HOST_PORT + "/next_bus_to_dest/%s?lat=%s&long=%s";

    public static final String NEXT_BUS_DETAILS_URL = "http://" + SERVER_HOSTNAME + ":" + SERVER_HOST_PORT + "/next_bus_details/%s?lat=%s&long=%s";

    public static final String TIME_TO_DEST_URL = "http://" + SERVER_HOSTNAME + ":" + SERVER_HOST_PORT + "/time_to_dest/%s?lat=%s&long=%s";

    // response strings to speak
    public static final String NEAREST_BUS_RESPONSE_TEXT = "I have an answer! The nearest bus stop to your location is, %s.";

}
