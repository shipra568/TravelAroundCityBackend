package com.example.demo;

import java.util.ArrayList;

public class PossiblePaths {
    public ArrayList<String> findPossiblePaths(String[] destList){
        //make a string from index of destination
        String str = "";
        for(int i=0;i<destList.length;i++){
            str = str + String.valueOf(i+1);
        }
       // System.out.println("string is "+ str);
        //find permutation of given string
        ArrayList<String> listofDest =getPermutation(str);
        //printArrayList(listofDest);
        return listofDest;
    }

    private void printArrayList(ArrayList<String> arrL)
    {
        arrL.remove("");
        for (int i = 0; i < arrL.size(); i++) {
            System.out.println("Possible path " + arrL.get(i));
        }
    }

    // Function to returns the arraylist which contains
    // all the permutation of str
    private ArrayList<String> getPermutation(String str)
    {

        // If string is empty
        if (str.length() == 0) {
            ArrayList<String> empty = new ArrayList<>();
            empty.add("");
            return empty;
        }
        char ch = str.charAt(0);
        String subStr = str.substring(1);
        ArrayList<String> prevResult = getPermutation(subStr);
        ArrayList<String> Res = new ArrayList<>();

        for (String val : prevResult) {
            for (int i = 0; i <= val.length(); i++) {
                Res.add(val.substring(0, i) + ch + val.substring(i));
            }
        }
        return Res;
    }
}
