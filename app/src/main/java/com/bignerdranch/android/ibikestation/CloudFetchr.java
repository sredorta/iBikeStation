package com.bignerdranch.android.ibikestation;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sredorta on 11/24/2016.
 */

//ser=id228014_sergi&password=HIB2oB2f

public class CloudFetchr {
    private static final String TAG = "SERGI:CloudFetchr";
    private static final String URI_BASE = "http://ibikestation.000webhostapp.com/";
    private static final String PHP_CONNECTION_CHECK = "db_connect_checker.php";                // Params required : none
    private static final String PHP_STATION_UPDATE = "db_station_update.php";                   // Params required : name + latitude...
    private static final String PHP_STATION_REGISTERED = "db_station_registered.php";           // Params required : name
    private static final String PHP_STATION_ADD = "db_station_add.php";                         // Params required: name
    private static final String PHP_STATION_STATUS_REQUEST = "db_station_status_request.php";   // Params required: name

    private static final String USER = "sergi";
    private static final String PASSWORD = "HIB2oB2f" ;


    //Build http string besed on method and query
    private URL buildUrl(String Action,HashMap<String, String> params) {
        Uri ENDPOINT = Uri
                .parse(URI_BASE + Action)
                .buildUpon()
                .build();

        URL url = null;
        Uri.Builder uriBuilder = ENDPOINT.buildUpon();
        //Add GET query parameters using the HashMap
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                uriBuilder.appendQueryParameter(URLEncoder.encode(entry.getKey(), "utf-8"), URLEncoder.encode(entry.getValue(), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            // do nothing
        }
        String result = uriBuilder.build().toString();
        try {
            url = new URL(result);
        } catch(MalformedURLException e) {
            //Do nothing
        }
        Log.i(TAG,"Final URL :" + url.toString());
        return url;
    }

    //Get raw data from URL
    private String getURLString(URL url) throws IOException {

        HttpURLConnection connection;
        OutputStreamWriter request = null;
        String response = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            //Required to enable input stream, otherwhise we get EOF (When using POST DoOutput is required
            connection.setDoInput(true);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            connection.setRequestMethod("GET");
/*            request = new OutputStreamWriter(connection.getOutputStream());
              request.write(getPostDataJsonString(parameters));
              request.flush();
              request.close();
*/
            String line = "";
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            // You can perform UI operations here
            Log.i(TAG, "Message from Server: \n" + response);
            isr.close();
            reader.close();

        } catch (IOException e) {
            // Error
            Log.e(TAG, "POST method try", e);
        }
        Log.i(TAG, response);
        return response;
    }

    public String getAction() {
        //Define the POST parameters in a HashMap
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", iBikeStationFragment.LOCKER_NAME);


        URL url = buildUrl(PHP_STATION_STATUS_REQUEST,parameters);
        JsonItem networkAnswer = getJSON(url);
        return (networkAnswer.getAction());
    }

    public Boolean setLocation(String longitude,String latitude) {
        //Define the POST parameters in a HashMap
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", iBikeStationFragment.LOCKER_NAME);
        parameters.put("longitude", longitude);
        parameters.put("latitude", latitude);

        URL url = buildUrl(PHP_STATION_UPDATE,parameters);
        JsonItem networkAnswer = getJSON(url);
        return (networkAnswer.getSuccess());
    }

    public Boolean isCloudConnected() {
        //Define the POST parameters in a HashMap
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("name", iBikeStationFragment.LOCKER_NAME);

        URL url = buildUrl(PHP_CONNECTION_CHECK,parameters);
        JsonItem networkAnswer = getJSON(url);
        return (networkAnswer.getSuccess());
    }

    // Sends PHP request and returns JSON object
    private JsonItem getJSON(URL url){
        JsonItem item = new JsonItem();
        try {
            String jsonString = getURLString(url);
            Log.i(TAG, "Received JSON:" + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            item = JsonItem.parseJSON(jsonBody.toString());
        } catch (JSONException je) {
            Log.e(TAG,"Failed to parse JSON", je);
        } catch (IOException ioe) {
            //Toast.makeText (mActivity,"Error JSON",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Falied to fetch items !", ioe);
        }
        return item;
    }
}

