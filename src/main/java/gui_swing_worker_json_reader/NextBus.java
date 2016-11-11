package gui_swing_worker_json_reader;

import com.google.gson.stream.JsonReader;

import javax.swing.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by clara on 11/11/16.
 *
 * Get the next bus from MCTC to First Avenue with a HTTP request, and parsing the JSON.
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

            ArrayList<String> times = new ArrayList<>();

            URL getBusTimesUrl = new URL(getBusTimesUrlString);
            //Open the URL - connect to the URL, expecting a stream of data returned
            InputStream stream = getBusTimesUrl.openStream();
            //Create a InputStreamReader to read the Stream
            InputStreamReader streamReader = new InputStreamReader(stream);

            JsonReader reader = new JsonReader(streamReader);

            //How is the response structured? It's a JSON array, each array element contains an object.
            //The objects are structured as key-value pairs
            // Each object contains a key "DepartureText" with the value of either the time to next
            // departure (e.g. "12 Min") or scheduled departure time (e.g. "15:44")

            //Start reading the array
            reader.beginArray();

            //While there is another item in the array...
            while (reader.hasNext()) {

                //each item in the array is an object. Start reading this object
                reader.beginObject();

                //and while there is another key-value pair in the array
                while (reader.hasNext()) {

                    //read the name of the key
                    String name = reader.nextName();

                    //if the key is "DepartureText"
                    if (name.equals("DepartureText")) {

                        //read the String value for "DepartureText"
                        String departureString = reader.nextString();
                        times.add(departureString);

                    }
                    // If the name is not departureText, we don't care.
                    // We have to read every part of the JSON so can't just ignore, instead, tell the reader to skip to next value
                    else {
                        reader.skipValue();
                    }
                }

                //And once the entire object is processed, tell the reader to stop processing this object
                reader.endObject();
            }

            //And that's the end of the array.
            reader.endArray();

            //Close resources
            stream.close();
            streamReader.close();
            reader.close();

            return times;

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
            ArrayList<String> busTimes = get();    //get() fetches whatever you returned from doInBackground.
            resultListener.timesFetched(busTimes);
        } catch (ExecutionException | InterruptedException e) {
            resultListener.timesFetched(null);
        }

    }



}
