package gui_swing_worker_deserialize_json;

import com.google.gson.Gson;
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

class BusWorker extends SwingWorker<BusDeparture[], Void> {
    private NextBusGUI resultListener;
    private String getBusTimesUrlString;

    //Constructor - use to send data to your worker and do any initialization
    public BusWorker(String urlRequest, NextBusGUI resultListener) {
        this.getBusTimesUrlString = urlRequest;
        this.resultListener = resultListener;
    }

    @Override
    //This method is required. It must have the same return type as you specified in the class definition: BusWorker extends SwingWorker<Document, Void>. This is where you'll do the time-consuming task.
    protected BusDeparture[] doInBackground() throws Exception {
        try {

            URL getBusTimesUrl = new URL(getBusTimesUrlString);
            //Open the URL - connect to the URL, expecting a stream of data returned
            InputStream stream = getBusTimesUrl.openStream();
            //Create a InputStreamReader to read the Stream
            InputStreamReader streamReader = new InputStreamReader(stream);

            JsonReader reader = new JsonReader(streamReader);

            Gson gson = new Gson();

            //fromJson - tell it the source of JSON, the JsonReader
            //The JSON is an array of bus departure data objects
            //And the type of object to turn the JSON into - in this case, an array of BusDepartures object
            //And it will turn the JSON into an array of BusDepartures!

            BusDeparture[] departures = gson.fromJson(reader, BusDeparture[].class);

            //Close resources
            stream.close();
            streamReader.close();
            reader.close();

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
            BusDeparture[] departures = get();    //get() fetches whatever you returned from doInBackground.
            resultListener.timesFetched(departures);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            resultListener.timesFetched(null);
        }

    }



}
