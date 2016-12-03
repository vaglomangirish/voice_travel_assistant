package mobilepervasive.soic.iu.edu.travelassistantandroid;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by goshenoy on 12/2/16.
 */
public class QuestionAnsweringUtil {

    private static final String TAG = "QuestionAnsweringUtil";

    private static Map<String, List<String>> questionMap = new HashMap<>();
    private static String lastQuestion;
    private static String questionCategory;
    private static String destinationName;

    // categories of questions
    private static final String NEAREST_BUS_STATIONS = "nearest_bus_stations";
    private static final String NEXT_BUS_TO_DEST = "next_bus_to_dest";
    private static final String NEXT_BUS_DETAILS = "next_bus_details";

    // utility class for calling apis
    private static APIUtil apiUtil = new APIUtil();

    static {
        questionMap.put(NEAREST_BUS_STATIONS, getTagsForNearestBusstops());
        questionMap.put(NEXT_BUS_TO_DEST, getTagsForNextBusToDest());
        questionMap.put(NEXT_BUS_DETAILS, getTagsForNextBusDetails());
    }

    public static void processQuestion(String questionText, double latitude, double longitude) {
        lastQuestion = questionText;
        try {
            if (questionCategory.equals(NEAREST_BUS_STATIONS)) {
                // submit api call
                apiUtil.getNearestBusStops(latitude, longitude);
            } else if (questionCategory.equals(NEXT_BUS_TO_DEST)) {
                // submit api call
                apiUtil.getNextBusToDest(destinationName, latitude, longitude);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to process question, reason: " + ex);
        }
    }

    public static String getLastQuestion() {
        return lastQuestion;
    }

    public static String getDestinationName() {
        return destinationName;
    }

    public static String getQuestionCategory() {
        return questionCategory;
    }

    /**
     * Gets the tags for questions related to nearest bus stops
     *  example questions:
     *      - what are the nearest bus stops
     *      - which are the bus stations near me
     *      - are there any bus stops close to me
     * @return
     */
    private static List<String> getTagsForNearestBusstops() {
        List<String> tags = new ArrayList<String>();
        // denote closeness
        tags.add("closest");
        tags.add("nearest");
        tags.add("near");
        // bus stops
        tags.add("bus");
        tags.add("stops");
        tags.add("stop");
        tags.add("station");
        tags.add("stations");
        // person
        tags.add("me");
        // determiners
        tags.add("to");
        tags.add("are");
        tags.add("is");
        tags.add("the");
        tags.add("there");
        // question start
        tags.add("tell");
        tags.add("which");
        tags.add("what");
        return tags;
    }

    /**
     * Gets the tags for questions related to next bus to a destination
     *  example questions:
     *      - what is the next bus to xx
     *      - tell me the next bus to xx
     *      - which bus goes to xx
     * @return
     */
    private static List<String> getTagsForNextBusToDest() {
        List<String> tags = new ArrayList<String>();
        // general
        tags.add("next");
        tags.add("bus");
        tags.add("goes");
        // person
        tags.add("me");
        // determiners
        tags.add("to");
        tags.add("are");
        tags.add("is");
        tags.add("the");
        tags.add("there");
        // question start
        tags.add("give");
        tags.add("tell");
        tags.add("which");
        tags.add("what");

        // destinations
        tags.add("sample");
        tags.add("gates");

        return tags;
    }

    /**
     * Gets the tags for question related to getting more details about next bus
     *  example questions:
     *      - can you give me some more details about this bus
     *      - i need more details about this bus
     *      - tell me more about this bus
     * @return
     */
    private static List<String> getTagsForNextBusDetails() {
        List<String> tags = new ArrayList<String>();
        // start of question
        tags.add("can");
        tags.add("you");
        tags.add("give");
        tags.add("tell");
        // person
        tags.add("i");
        tags.add("me");
        // general
        tags.add("need");
        tags.add("some");
        tags.add("more");
        tags.add("details");
        tags.add("about");
        tags.add("this");
        tags.add("bus");
        return tags;
    }

    public static boolean belongsToCategory(String commandText) {
        boolean belongs = false;
        StringBuilder destination = new StringBuilder();

        for(String questionCategory : questionMap.keySet()) {

            String lastWord = "";
            for(String commandWord : commandText.split(" ")) {
                // figure out the destination name
                if (lastWord.equals("to")) {
                    if (questionCategory.equals(NEXT_BUS_TO_DEST)) {
                        destination.append(commandWord.toLowerCase())
                                .append(" ");
                        continue;
                    }
                }
                if(questionMap.get(questionCategory).contains(commandWord.toLowerCase())) {
                    belongs = true;
                } else {
                    belongs = false;
                    break;
                }

                // set last word
                lastWord = commandWord;
            }

            if (belongs) {
                QuestionAnsweringUtil.questionCategory = questionCategory;
                break;
            }
        }

        // set the destination
        if(destination.length() != 0) {
            destination.append("Bloomington, Indiana");
            QuestionAnsweringUtil.destinationName = destination.toString().trim();
            Log.v(TAG, "The destination name is: " + getDestinationName());
        }

        return belongs;
    }
}
