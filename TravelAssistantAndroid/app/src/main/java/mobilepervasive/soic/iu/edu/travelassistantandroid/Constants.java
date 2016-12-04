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

    public static final String REQUEST_UBER_RIDE_URL = "http://" + SERVER_HOSTNAME + ":" + SERVER_HOST_PORT + "/requestride";

    // response strings to speak
    public static final String NEAREST_BUS_RESPONSE_TEXT = "I have an answer! The nearest bus stop to your location is, %s.";
    public static final String REQUEST_UBER_SUCCESS = "Sure. I have submitted an UBER request for you. You should receive an SMS confirmation soon.";
    public static final String REQUEST_UBER_FAILURE = "Sorry. Something went wrong & I could not book an UBER for you. Please try again.";
    public static final String CANNOT_UNDERSTAND_WHAT_I_SEE = "I am sorry, but right now, I do not understand what I see.";
    public static final String ZEBRA_CROSSING_ALERT = "You are approaching a zebra crossing. Please be cautious.";
    public static final String PRE_OTHER_REC_ITEMS = "I see the following items,";

    // api success response
    public static final String API_SUCCESS_RESPONSE = "SUCCESS";

    // Constants for Image Recognition.
    public static final String IMAGE_CLASSIFIER_ID = "zcross_1753080880";

    public static final String IMAGE_VP_API_KEY = "b34b3d1f4dc43112ea5933a5b8cbee86ee0b17d5";

    public static final String IMAGE_VR_SERVICE_URL = "https://gateway-a.watsonplatform.net" +
            "/visual-recognition/api/v3/classify?api_key=" +
            IMAGE_VP_API_KEY + "&version=2016-05-20";

}
