package gui_swing_worker_org_json_parsing;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by clara on 11/11/16.
 *
 * Get the next bus from MCTC to First Avenue with a HTTP request, and deserializing the JSON.
 *
 * Use a SwingWorker to execute the fetch in the background.
 *
 */
public class NextBus {

        /* When is the next northbound 6 bus from MCTC?

        Metro Transit has an API with live bus times. Make a HTTP request to a particular URL and the response will contain the data we need (hopefully)

        What's the URL? Consult the API documentation at

        http://svc.metrotransit.org/

        And we need to use this URL - option 6, GetTimepointDepartures from a particular destination

        http://svc.metrotransit.org/NexTrip/6/4/MCTC?format=json

        And we'll ask for data in JSON, instead of the default, XML.

        Use GSON JSON processing library, in particular the JsonReader class, to process the JSON.

        https://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/stream/JsonReader.html

        */


    public static void getTimes(NextBusGUI sendResultsHere) {

        String url = "http://svc.metrotransit.org/NexTrip/6/4/MCTC?format=json\n";
        BusWorker worker = new BusWorker(url, sendResultsHere);
        worker.execute();
    }
}


//The types in pointy brackets: ArrayList<String> is the type of object the doInBackground method returns.
// Optionally, some backgroundTask make progress notifications by returning objects periodically -
//Set it to Void (just an empty placeholder) if you don't need to make progress updates.

class BusWorker extends SwingWorker<ArrayList<String>, Void> {
    private NextBusGUI resultListener;
    private String getBusTimesUrlString;

    //Constructor - use to send data to your worker and do any initialization
    public BusWorker(String urlRequest, NextBusGUI resultListener) {
        this.getBusTimesUrlString = urlRequest;
        this.resultListener = resultListener;
    }

    @Override
    //This method is required. It must have the same return type as you specified in the class definition: BusWorker extends SwingWorker<Document, Void>. This is where you'll do the time-consuming task.
    protected ArrayList<String> doInBackground() throws Exception {
        try {

            ArrayList<String> departures = new ArrayList<>();

            URL getBusTimesUrl = new URL(getBusTimesUrlString);
            //Open the URL - connect to the URL, expecting a stream of data returned
            InputStream stream = getBusTimesUrl.openStream();
            //Create a InputStreamReader to read the Stream
            InputStreamReader streamReader = new InputStreamReader(stream);

            //Read all of the data directly from the InputStreamReader, into a String, like this

            int char_int = 0;
            String responseString = "";  //to store the String of the response
            //the reader returns characters (as integers) until the end of the stream is reached, when it returns -1
            while ((char_int = streamReader.read()) != -1) {
                //cast the int into a char
                char character = (char) char_int;
                //and append to the String
                responseString = responseString + character;
            }

            System.out.println(responseString);   //should contain the entire text of the JSON response

            // Reading the documentation to figure out the structure of the JSON helps figure out how to parse it in your code.

            //The response is an array of objects
            JSONArray departuresArray = new JSONArray(responseString);

            //Loop over this JSONArray
            for ( int i = 0 ; i < departuresArray.length() ; i++) {

                //Get an object from the array
                JSONObject departureObject = departuresArray.getJSONObject(i);
                //get the DepartureText element
                String departureText = departureObject.getString("DepartureText");
                departures.add(departureText);

            }

            //Close resources
            stream.close();
            streamReader.close();

            return departures;

        } catch (Exception e) {
            e.printStackTrace();
            //todo deal properly with the various exceptions that could occur
            //malformed URL, web exceptions, json parsing exceptions..
            return null;
        }
    }

    @Override
    protected void done() {

        try {
            ArrayList<String> departures = get();    //get() fetches whatever you returned from doInBackground.
            resultListener.timesFetched(departures);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            resultListener.timesFetched(null);
        }

    }



}
