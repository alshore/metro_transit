package command_line;

import com.google.gson.stream.JsonReader;

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

            /*

            //Could read all of the data directly from the InputStreamReader, into a String, like this
            //Don't do this AND the JsonReader - this code will consume the entire stream, and there will be nothing left for the JsonReader to read.

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

            */

            /* Use JsonReader to read the JSON as a stream.
            This approach is recommended, because you don't know how much data you'll get back.
            Trying to read a large amount of data into a String could overwhelm your computer.

             */

            JsonReader reader = new JsonReader(streamReader);

            //How is the response structured? It's a JSON array, each array element contains an object.
            //The objects are strucured as key-value pairs
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
                        //And print. Could also add to an ArrayList or do whatever else you need
                        System.out.println(departureString);
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


        } catch (Exception e) {
            e.printStackTrace();
            //todo deal properly with the various exceptions that could occur
            //malformed URL, web exceptions, json parsing exceptions..
        }

    }
}
