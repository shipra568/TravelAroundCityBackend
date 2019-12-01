package com.example.demo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;

public class DistanceCalculator {

    //Function is use to get the distance between two destinations using Goggle API
    public double getDistance(String src,String dest){
        String[] parameters = {"origins",src,"destinations",dest,"key","AIzaSyAX6jx06WQhsYJZo28Id5qY49RvPNZMcdE"};
        String urlPath = "https://maps.googleapis.com/maps/api/distancematrix/json";
        double dist = 0L;

        //create the url
        urlPath = createUrlPath(urlPath,parameters);
        String resp = getResponse(src,dest,urlPath);
        System.out.println("response is " + resp);
        //Process the response and calculate the distance between two destinations
        try {
            JSONParser parse = new JSONParser();
            JSONObject jsonObj = (JSONObject) parse.parse(resp);
            JSONArray jArray = (JSONArray) jsonObj.get("rows");
            JSONObject jElementObj = (JSONObject) jArray.get(0);
            JSONArray elementsArray = (JSONArray) jElementObj.get("elements");
            JSONObject jsonDuraDistObj = (JSONObject) elementsArray.get(0);
            JSONObject distObj = (JSONObject) jsonDuraDistObj.get("distance");
            System.out.println("distance is " + distObj.get("text"));
            //System.out.println("duration is " + distObj.get("value"));
            dist = ((long) distObj.get("value"))/1000.0;
            System.out.println("distance is " + dist);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dist;
    }

    //Create the distance url
    private static String createUrlPath(String urlPath, String[] parameters){
        StringBuilder query = new StringBuilder(urlPath);
        boolean first = true;
        for(int i=0;i<parameters.length;i+=2){
            if(first) {
                query.append("?");
                first = false;
            }else
                query.append("&");
            try{
                query.append(parameters[i]).append("=").append(URLEncoder.encode(parameters[i+1],"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        System.out.println("distance url is " + query.toString());
        return query.toString();
    }

    private static String getResponse(String src,String dest,String urlPath){
        try {
            StringBuffer buffer = new StringBuffer();
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent","Mozilla/5.0");
            int respCode = connection.getResponseCode();
            if(respCode==HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while((inputLine=in.readLine())!=null)
                    buffer.append(inputLine);
                in.close();
              //  System.out.println(buffer.toString());
            }else{
                System.out.println("GET request not working");
            }
            return buffer.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NOT_VALID";
    }
}