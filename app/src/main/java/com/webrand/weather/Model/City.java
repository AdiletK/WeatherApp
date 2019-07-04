package com.webrand.weather.Model;

import com.google.gson.annotations.SerializedName;

public class City {
    @SerializedName("id")
    int id;

    @SerializedName("name")
    public String name;

    @SerializedName("coord")
    Coordinate coordinate;

    @SerializedName("country")
    String country;

    @SerializedName("timezone")
    String timezone;


}
