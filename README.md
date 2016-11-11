#Metro Transit API

Various approaches to fetching data from an API

**command_line_json_reader**

Make web request, use GSON to process JSON piece by piece

**command_line_deserialize_json**

Make web request, use GSON to turn JSON into Java object

**gui_swing_worker_json_reader**

Use a SwingWorker to execute the web request and JSON parsing in a separate thread

Like command line version - makes web request, use GSON to process JSON piece by piece

**gui_swing_worker_deserialize_json**

Use a SwingWorker to execute the web request and JSON parsing in a separate thread

Like command line version - makes web request, Use GSON to turn JSON into Java object

**command_line_org_json**

Uses a different library org.json to process the JSON.

This can be used to turn the JSON response into JSON objects and arrays, and extract data.


