package com.example.demo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

@RestController
public class DestinationsController {

    private static Map<String,String> destinationMap = new HashMap<>();

    Map<String,String> destType = new HashMap<String, String>(){
        {
            put("premise", "Others");
            put("mosque","Mosque");
            put("zoo","Zoo");
            put("hindu_temple","Temple");
            put("park","Park");
            put("museum","Museum");
            put("mosque","Mosque");
            put("aquarium","Aquarium");
            put("church","Church");
            put("art_gallery","ArtGallery");
            put("amusement_park","AmusementPark");
            put("natural_feature","Nautral");
            put(null,"Others");
            put("point_of_interest","Others");
            put("movie_theater","Theater");
            put("health","Health Center");
            put("place_of_worship","Temple");
            put("cemetery","Cemetery");
            put("atm","Atm");
        }
    };

    //Get API request to get the list of destinations in particular city.
    //It returns the List of destinations and their corrosponding type
    @RequestMapping(value="destinations", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public @ResponseBody ArrayList<String> getDestinations(@RequestParam("city") String city){

        //Google API is use to get the list of destinations in particular city.
        //Parameters need to be passed - city, language, radius, Google API key
        String urlPath = "https://maps.googleapis.com/maps/api/place/textsearch/json";
        Map<String,String> modifyMap = new HashMap<>();
        System.out.println("city is " + city);
        this.destinationMap.clear();
        ArrayList<String> destList = new ArrayList<>();
        String placeToVisit = city;
        String poi = "+point+of+interest";
        placeToVisit = placeToVisit.concat(poi);
        String[] parameters = {"query",placeToVisit,"language","en","radius","x`2000","key","AIzaSyAX6jx06WQhsYJZo28Id5qY49RvPNZMcdE"};
        urlPath = createURL(urlPath,parameters);
        try {
            String resp = findDestinations(urlPath);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(resp);
           //   System.out.println("jsonObj is " + jsonObj);
            JSONArray jsonArray = (JSONArray) jsonObj.get("results");
            //   System.out.println("size is " + jsonArray);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject resultObj = (JSONObject) jsonArray.get(i);
                JSONObject geometryObj = (JSONObject) resultObj.get("geometry");
              //    System.out.println("geometry is " + geometryObj);
                String address = (String) resultObj.get("name");
                JSONArray types = (JSONArray) resultObj.get("types");
                String type = (String)(types.get(0));
                //System.out.println("%%%%%%%%% address is " + address + " type is " + (String)(types.get(0)));
                modifyMap.put(address,destType.get(type));
                String result = address+":"+(String)(types.get(0));
                //System.out.println("formatted_address is " + resultObj.get("formatted_address"));
                //destList.add(result);
                String modAdress = address.split(",")[0];
                destinationMap.put(modAdress,(String)resultObj.get("formatted_address"));
                System.out.println("value is " + destinationMap.get(address));
            }
            destList = modifyDestMap(modifyMap);
        }catch (Exception e){

        }
        for(Map.Entry<String,String> entry: destinationMap.entrySet()){
            System.out.println("key is " + entry.getKey() + " value is " + entry.getValue());
        }
        System.out.println("list is " + destList);
        return destList;
    }

    //Sort the destination map according to destination type.
    //Convert the map into list.
    private ArrayList<String> modifyDestMap(Map<String,String> map){
        for(Map.Entry<String,String> entry:map.entrySet()){
            System.out.println("orginal key is " + entry.getKey() + " value is " + entry.getValue());
        }
        Set<Map.Entry<String,String>> set = map.entrySet();
        ArrayList<Map.Entry<String,String>> list = new ArrayList<>(set);
        /*Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                if(o1.getValue().compareTo(o2.getValue())>0)
                    return 1;
                else
                    return -1;
            }
        });*/
        ArrayList<String> destList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            System.out.println("new key is " + list.get(i).getKey() + " value is " + list.get(i).getValue());
            destList.add(list.get(i).getKey()+ ":" + list.get(i).getValue());
        }
        return destList;
    }

    public Map<String,String> getDestinationMap(){
        return destinationMap;
    }

    //Get the destinations for a particular city
    private String findDestinations(String urlPath) throws IOException {
        StringBuffer buffer = new StringBuffer();
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent","Mozilla/5.0");
        int respCode = connection.getResponseCode();
        if(respCode==HttpURLConnection.HTTP_OK){
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            while((inputLine=reader.readLine())!=null){
                buffer.append(inputLine);
            }
            reader.close();
           //   System.out.println(buffer.toString());
        }else{
            System.out.println("GET request not working");
        }
        return buffer.toString();
    }

    //Create the url with the input parameters
    private String createURL(String urlPath,String[] parameters){
        boolean first = true;
        StringBuilder query = new StringBuilder(urlPath);
       // System.out.println("url is " + urlPath);
        for(int i=0;i<parameters.length;i+=2){
           // System.out.println("parameter is " + parameters[i]);
            if(first) {
                query.append("?");
                first = false;
            }else
                query.append("&");
            /*if(parameters[i].equals("radius")){
                query.append(parameters[i]).append("=").append(Integer.valueOf(parameters[i+1]));
            }else {*/
            try {
                query.append(parameters[i]).append("=").append(URLEncoder.encode(parameters[i + 1], "UTF-8"));
            }catch (Exception e){

            }
            //  }
        }
        System.out.println("Query is " + query.toString());
        return query.toString();
    }
}
