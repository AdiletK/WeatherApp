package com.webrand.weather.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class WeatherResponse {
    @SerializedName("cod")
    String code;

    @SerializedName("message")
    String message;

    @SerializedName("cnt")
    int count;

    @SerializedName("list")
    public ArrayList<WeatherData>weatherData = new ArrayList<>();

    @SerializedName("city")
    public City city;
}
