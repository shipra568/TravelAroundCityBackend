package com.example.demo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GetMinDistHotels {

    @GetMapping("/hotels")
    @CrossOrigin(origins = "http://localhost:4200")
    private Map<String,String> getMinDistHotels(@RequestParam("destList") String destString,@RequestParam("city") String city) throws ParseException, org.json.simple.parser.ParseException, IOException {
        System.out.println("destList is " + destString + " city is " + city);
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        HotelsController hotelsController = new HotelsController();

        //hotelMap contains key as hotel name and value as address of hotel
        Map<String,String> hotelMap = hotelsController.getMapOfHotels(city);
        System.out.println("set of hotel " + hotelMap.keySet());

        //destinationMap contains destination name as key and destination address as value
        DestinationsController destinationsController = new DestinationsController();
        Map<String,String> destinationMap = destinationsController.getDestinationMap();
        for(Map.Entry<String,String > entry: destinationMap.entrySet())
            System.out.println("entry key is " + entry.getKey() + " entry value is " + entry.getValue());

        //Array of destinations received from user
        String[] destList = destString.split(",");

        //created a map. Key will be (srcIndex+destIndex).Long will be the distance between them.
        Map<String ,Double> distanceMap = new HashMap<>();

        //calculate distance between all destinations.
        for(int i=0;i<destList.length;i++){
           // System.out.println("source dest " + destList[i]);
            for(int j=i+1;j<destList.length;j++){
                //System.out.println("$$$$$$$$$$ src is " + destList[i] + " dest is " + destList[j] + " $$$$$$$$$$$");
                System.out.println("src address is " + destinationMap.get(destList[i]) + " dest address is " + destinationMap.get(destList[j]) + " $$$$$$$$$$$$$");
                double distance = distanceCalculator.getDistance(destinationMap.get(destList[i]),destinationMap.get(destList[j]));
                String str = (String.valueOf(i+1)) + (String.valueOf(j+1));
                System.out.println("distance is " + distance + " str is " + str);
                distanceMap.put(str,distance);
            }
        }
        //get all ways to visit selected destinations
        PossiblePaths possiblePaths = new PossiblePaths();
        ArrayList<String> listOfDest = possiblePaths.findPossiblePaths(destList);

        //result map-> key is hotel,value is combination of path,distance
        Map<String,String> result = new HashMap<>();

        //calculate distance of hotel from each destination and put in map with index considered as zero.
        for(Map.Entry<String,String> entry: hotelMap.entrySet()){
            //Finding distance of each destination from hotel and put in map.
            for(int j=0;j<destList.length;j++){
                System.out.println("src is " + entry.getValue() + " dest is " + destinationMap.get(destList[j]) + " ************");
                double distance = distanceCalculator.getDistance(entry.getValue(),destinationMap.get(destList[j]));
                //hotel is considered at 0 index
                String str = (String.valueOf(0)) + (String.valueOf(j+1));
                System.out.println("distance is " + distance + " str is " + str);
                distanceMap.put(str,distance);
            }
            System.out.println("Distance calc done for hotel " + entry.getValue());

            //calculate the optimized required to cover to all the selected destinations from given hotel
            String finalPath = "";
            double minDistance = Double.MAX_VALUE;
            for(int j=0;j<listOfDest.size();j++){
                double totalDist = 0L;
                String path = listOfDest.get(j);
                //modified the path to start with hotel and end with hotel
                String modPath = String.valueOf(0) + path + String.valueOf(0);
               // System.out.println("modPath is " + modPath);
                char[] pathArray = modPath.toCharArray();
                for(int k=0;k<(pathArray.length-1);k++){
                    char src = pathArray[k];
                    char dest = pathArray[k+1];
                    System.out.println("src is " + src + " dest is " + dest);
                    String s1 = String.valueOf(src) + String.valueOf(dest);
                    String s2 = String.valueOf(dest) + String.valueOf(src);
                    if(distanceMap.get(s1)!=null){
                        totalDist = totalDist + distanceMap.get(s1);
                    }else{
                        totalDist = totalDist + distanceMap.get(s2);
                    }
                }
                //checking the path that takes minimum distance
                if(totalDist<minDistance) {
                    minDistance = totalDist;
                    finalPath = modPath;
                }
            }
            //result map contains key as hotel name and finalPath&&minDistance
            result.put(entry.getKey().split(",")[0],finalPath + "&&" + String.valueOf(minDistance));
        }
        for(Map.Entry<String,String> entry: result.entrySet())
            System.out.println("key is " + entry.getKey() + "value is " + entry.getValue());
        return result;
    }
}
