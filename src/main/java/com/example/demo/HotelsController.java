package com.example.demo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
class HotelsController {

    public Map<String, String> getMapOfHotels(String city) throws IOException, ParseException, org.json.simple.parser.ParseException {

        //URL to get the list of hotels in a given city
        String urlPath = "http://engine.hotellook.com/api/v2/lookup.json";
        //Getting eight hotels
        String[] parameters = {"query",city,"lang","en","lookFor","both","limit","8","token","216586"};
        urlPath = createHotelsURL(urlPath,parameters);
        System.out.println("url is " + urlPath);
        String resp = getTheResponse(urlPath);
        return processTheResponse(resp,city);
    }

    //Return the hotels map. Key is hotel name and key is hotel address
    public static Map<String,String> processTheResponse(String result,String city) throws ParseException, org.json.simple.parser.ParseException {
        Map<String,String>  hotels =  new HashMap<>();
       // List<String> hotels = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(result);
        JSONObject jsonObj = (JSONObject) object.get("results");
        JSONArray jsonArray = (JSONArray) jsonObj.get("hotels");
        for(int i=0;i<jsonArray.size();i++){
            JSONObject obj = (JSONObject) jsonArray.get(i);
          //  System.out.println(obj.get("label"));
            String fullName = (String) obj.get("fullName");
            if(fullName.contains("India")&&(fullName.contains(city)))
                hotels.put((String) obj.get("label"),fullName);
        }
        return hotels;
    }

    //create the url to get the list of hotels in a given city using the parameters
    public static String createHotelsURL(String url,String[] parameters) throws UnsupportedEncodingException {
        boolean first = true;
        StringBuffer buffer = new StringBuffer(url);
        for(int i=0;i<parameters.length;i+=2){
            if(first){
                buffer.append("?");
                first = false;
            }else{
                buffer.append("&");
            }
            buffer.append(parameters[i]).append("=").append(URLEncoder.encode(parameters[i + 1], "UTF-8"));
        }
         System.out.println("*********** hotel url is " + buffer.toString());
        return buffer.toString();
    }

    //Function to get the response from the hotel's url
    public static String getTheResponse(String urlPath) throws IOException {
        StringBuffer buffer = new StringBuffer();
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent","Mozilla/5.0");
        connection.setRequestMethod("GET");
        int respCode = connection.getResponseCode();
        if(respCode==HttpURLConnection.HTTP_OK){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String readLine;
            while((readLine=reader.readLine())!=null){
                buffer.append(readLine);
            }
            reader.close();
          //  System.out.println(buffer.toString());
        }
        return buffer.toString();
    }
}
