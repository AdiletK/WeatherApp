package com.webrand.weather.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherData {
    @SerializedName("dt")
    String dt;

    @SerializedName("main")
    public Main main;

    @SerializedName("weather")
    public ArrayList<SkyPosition> skyPosition = new ArrayList<>();

    @SerializedName("clouds")
     public Cloud clouds;

    @SerializedName("wind")
    public Wind wind;

    @SerializedName("dt_txt")
    public String date;
}

