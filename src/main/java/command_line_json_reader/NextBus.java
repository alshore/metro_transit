package command_line_json_reader;

import com.google.gson.stream.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by clara on 11/11/16.
 *
 * Get the next bus from MCTC to First Avenue with a HTTP request, and parsing the JSON.
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

        Use GSON JSON processing library, in particular the JsonReader class, to process the JSON.

        https://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/stream/JsonReader.html

        */


        try {

            URL getBusTimesURL = new URL("http://svc.metrotransit.org/NexTrip/6/4/MCTC?format=json");

            //Open the URL - connect to the URL, expecting a stream of data returned
            InputStream stream = getBusTimesURL.openStream();
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

//            JSONObject jsonArrayOfDepartures = new JSONObject(responseString);
            //The response is an array of objects
            JSONArray departuresArray = new JSONArray(responseString);

            //Loop over this JSONArray
            for ( int i = 0 ; i < departuresArray.length() ; i++) {

                //Get an object from the array
                JSONObject departureObject = departuresArray.getJSONObject(i);
                //get the DepartureText element
                String departureText = departureObject.getString("DepartureText");
                System.out.println(departureText);

            }



            //Close resources
            stream.close();
            streamReader.close();


        } catch (Exception e) {
            e.printStackTrace();
            //todo deal properly with the various exceptions that could occur
            //malformed URL, web exceptions, json parsing exceptions..
        }

    }
}
