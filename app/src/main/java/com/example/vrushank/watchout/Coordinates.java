package com.example.vrushank.watchout;

/**
 * Created by vrushank on 22/10/16.
 */


public class Coordinates {
    double lat,longi;
    String html_dir;
    double index;
    Coordinates(double lat,double longi,String dir,int i){
        this.lat = lat;
        this.longi = longi;
        html_dir = dir;
        index = i;
    }
    public double distance(double x1,double x2,double y1,double y2){

        final int R = 6371; // Radious of the earth
        double lat1 = x1;
        double lon1 = y1;
        double lat2 = x2;
        double lon2 = y2;
        double latDistance = Math.toRadians(lat2-lat1);
        double lonDistance = Math.toRadians(lon2-lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d;
    }
}
