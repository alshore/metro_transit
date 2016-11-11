package command_line_deserialize_json;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by clara on 11/11/16.
 *
 * Get the next bus from MCTC to First Avenue with a HTTP request
 *
 * Deserialize JSON into Java objects
 *
 *
 */
public class NextBus {

    public static void main(String[] args) {

        /* When is the next northbound 6 bus from MCTC?

        Metro Transit has an API with live bus times. Make a HTTP request to a particular URL and the response will contain the data we need (hopefully)

        What's the URL? Consult the API documentation at

        http://svc.metrotransit.org/

        And we need to use this URL - option 6, GetTimepointDepartures from a particular destination

        http://svc.metrotransit.org/NexTrip/6/4/MCTC?format=json

        And we'll ask for data in JSON, instead of the default, XML.

        Use GSON JSON processing library to turn the JSON into Java Objects

        https://github.com/google/gson/blob/master/UserGuide.md#TOC-Object-Examples

        */

        try {

            URL getBusTimesURL = new URL("http://svc.metrotransit.org/NexTrip/6/4/MCTC?format=json");

            //Open the URL - connect to the URL, expecting a stream of data returned
            InputStream stream = getBusTimesURL.openStream();
            //Create a InputStreamReader to read the Stream
            InputStreamReader streamReader = new InputStreamReader(stream);

            JsonReader reader = new JsonReader(streamReader);

            //Make a Gson object, which will do the deserialization work for us
            Gson gson = new Gson();

            //fromJson - tell it the source of JSON, the JsonReader
            //The JSON is an array of bus departure data objects
            //And the type of object to turn the JSON into - in this case, an array of BusDepartures object
            //And it will turn the JSON into an array of BusDepartures!
            BusDeparture[] departures = gson.fromJson(reader, BusDeparture[].class);

            //Close resources
            reader.close();
            stream.close();
            streamReader.close();

            for (BusDeparture dep : departures) {
                System.out.println(dep);   //The entire object
                System.out.println(dep.getDepartureText());   //or just one attribute
            }


        } catch (Exception e) {
            e.printStackTrace();
            //todo deal properly with the various exceptions that could occur
            //malformed URL, web exceptions, json parsing exceptions..
        }

    }
}
