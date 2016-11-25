package com.bignerdranch.android.ibikestation;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sredorta on 11/24/2016.
 */

//ser=id228014_sergi&password=HIB2oB2f

public class CloudFetchr {
    private static final String TAG = "SERGI:CloudFetchr";
    private static final String URI_BASE = "http://ibikestation.000webhostapp.com/db_connect_checker_wi.php";
    private static final String USER = "sergi";  //"id228014" will fail ,not allwed in http query !;
    private static final String PASSWORD = "HIB2oB2f" ;
    private static final Uri ENDPOINT = Uri
            .parse(URI_BASE)
            .buildUpon()
            .build();

    //Build http string besed on method and query
    private URL buildUrl(HashMap<String, String> params) {
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

        try
        {
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
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            // You can perform UI operations here
            Log.i(TAG,"Message from Server: \n"+ response);
            isr.close();
            reader.close();

        }
        catch(IOException e)
        {
            // Error
            Log.e(TAG,"POST method try", e);
        }
        Log.i(TAG,response);
        return response;


/*
        Log.i(TAG,"Setted connection timeout");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Log.i(TAG,"Connection before4");

            InputStream in            =  connection.getInputStream();
//            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Log.i(TAG,"Connection after");
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": width " + urlSpec);
            }
            Log.i(TAG, "Just before crash");
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer))>0) {
                Log.i(TAG,"read " + bytesRead);
                out.write(buffer,0,bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
        */
    }

    //Get string data from URL
/*    private String getURLString(String urlSpec) throws IOException {
        return new String(getURLBytes(urlSpec));
    }
*/
    public JsonItem isCloudConnected() {

        //Define the POST parameters in a HashMap
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("user", USER);
        parameters.put("password",  PASSWORD);
        //Parameters like id091919 will fail !!! Be carefull EOF will be returned

        URL url = buildUrl(parameters);
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
